package com.wearosai.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var promptInput: EditText
    private lateinit var sendButton: Button
    private lateinit var settingsButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var responseText: TextView
    private lateinit var apiClient: OpenAIClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        promptInput = findViewById(R.id.promptInput)
        sendButton = findViewById(R.id.sendButton)
        settingsButton = findViewById(R.id.settingsButton)
        progressBar = findViewById(R.id.progressBar)
        responseText = findViewById(R.id.responseText)

        apiClient = OpenAIClient(this)

        sendButton.setOnClickListener {
            sendRequest()
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun sendRequest() {
        val prompt = promptInput.text.toString().trim()
        if (prompt.isEmpty()) {
            return
        }

        if (!apiClient.isConfigured()) {
            Toast.makeText(this, R.string.configure_settings, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SettingsActivity::class.java))
            return
        }

        progressBar.visibility = View.VISIBLE
        sendButton.isEnabled = false
        responseText.text = ""

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiClient.sendRequest(prompt)
                withContext(Dispatchers.Main) {
                    responseText.text = response
                    progressBar.visibility = View.GONE
                    sendButton.isEnabled = true
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    responseText.text = "${getString(R.string.error)}: ${e.message}"
                    progressBar.visibility = View.GONE
                    sendButton.isEnabled = true
                }
            }
        }
    }
}
