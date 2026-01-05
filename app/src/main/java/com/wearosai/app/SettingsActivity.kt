package com.wearosai.app

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SettingsActivity : ComponentActivity() {

    private lateinit var apiKeyInput: EditText
    private lateinit var apiUrlInput: EditText
    private lateinit var modelNameInput: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        apiKeyInput = findViewById(R.id.apiKeyInput)
        apiUrlInput = findViewById(R.id.apiUrlInput)
        modelNameInput = findViewById(R.id.modelNameInput)
        saveButton = findViewById(R.id.saveButton)

        loadSettings()

        saveButton.setOnClickListener {
            saveSettings()
        }
    }

    private fun loadSettings() {
        val prefs = getEncryptedPrefs()
        apiKeyInput.setText(prefs.getString(KEY_API_KEY, ""))
        apiUrlInput.setText(prefs.getString(KEY_API_URL, DEFAULT_API_URL))
        modelNameInput.setText(prefs.getString(KEY_MODEL_NAME, DEFAULT_MODEL))
    }

    private fun saveSettings() {
        val prefs = getEncryptedPrefs()
        prefs.edit().apply {
            putString(KEY_API_KEY, apiKeyInput.text.toString().trim())
            putString(KEY_API_URL, apiUrlInput.text.toString().trim())
            putString(KEY_MODEL_NAME, modelNameInput.text.toString().trim())
            apply()
        }
        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun getEncryptedPrefs(): SharedPreferences {
        return getEncryptedPrefs(this)
    }

    companion object {
        const val PREFS_NAME = "wearosai_secure_prefs"
        const val KEY_API_KEY = "api_key"
        const val KEY_API_URL = "api_url"
        const val KEY_MODEL_NAME = "model_name"
        const val DEFAULT_API_URL = "https://api.openai.com/v1/chat/completions"
        const val DEFAULT_MODEL = "gpt-3.5-turbo"

        fun getEncryptedPrefs(context: Context): SharedPreferences {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            return EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }
}
