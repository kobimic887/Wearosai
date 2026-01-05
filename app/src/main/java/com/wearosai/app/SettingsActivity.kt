package com.wearosai.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity

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
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        apiKeyInput.setText(prefs.getString(KEY_API_KEY, ""))
        apiUrlInput.setText(prefs.getString(KEY_API_URL, DEFAULT_API_URL))
        modelNameInput.setText(prefs.getString(KEY_MODEL_NAME, DEFAULT_MODEL))
    }

    private fun saveSettings() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().apply {
            putString(KEY_API_KEY, apiKeyInput.text.toString().trim())
            putString(KEY_API_URL, apiUrlInput.text.toString().trim())
            putString(KEY_MODEL_NAME, modelNameInput.text.toString().trim())
            apply()
        }
        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        const val PREFS_NAME = "wearosai_prefs"
        const val KEY_API_KEY = "api_key"
        const val KEY_API_URL = "api_url"
        const val KEY_MODEL_NAME = "model_name"
        const val DEFAULT_API_URL = "https://api.openai.com/v1/chat/completions"
        const val DEFAULT_MODEL = "gpt-3.5-turbo"
    }
}
