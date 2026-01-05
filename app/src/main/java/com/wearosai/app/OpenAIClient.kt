package com.wearosai.app

import android.content.Context
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OpenAIClient(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun isConfigured(): Boolean {
        val prefs = context.getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val apiKey = prefs.getString(SettingsActivity.KEY_API_KEY, "") ?: ""
        return apiKey.isNotEmpty()
    }

    fun sendRequest(prompt: String): String {
        val prefs = context.getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val apiKey = prefs.getString(SettingsActivity.KEY_API_KEY, "") ?: ""
        val apiUrl = prefs.getString(SettingsActivity.KEY_API_URL, SettingsActivity.DEFAULT_API_URL) ?: SettingsActivity.DEFAULT_API_URL
        val modelName = prefs.getString(SettingsActivity.KEY_MODEL_NAME, SettingsActivity.DEFAULT_MODEL) ?: SettingsActivity.DEFAULT_MODEL

        val jsonBody = JSONObject().apply {
            put("model", modelName)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""

        if (!response.isSuccessful) {
            throw Exception("API Error: ${response.code} - $responseBody")
        }

        return parseResponse(responseBody)
    }

    private fun parseResponse(responseBody: String): String {
        return try {
            val jsonResponse = JSONObject(responseBody)
            val choices = jsonResponse.getJSONArray("choices")
            if (choices.length() > 0) {
                val firstChoice = choices.getJSONObject(0)
                val message = firstChoice.getJSONObject("message")
                message.getString("content")
            } else {
                "No response content"
            }
        } catch (e: Exception) {
            "Error parsing response: ${e.message}\n\nRaw response: $responseBody"
        }
    }
}
