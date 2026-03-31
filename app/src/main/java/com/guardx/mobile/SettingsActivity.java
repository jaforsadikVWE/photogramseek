package com.guardx.mobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    
    private EditText etBotToken, etChatId, etDailyLimit;
    private Switch swAutoSync, swWiFiOnly, swNotifications;
    private Button btnSave, btnTestConnection;
    
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "GuardXPrefs";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Initialize UI components
        etBotToken = findViewById(R.id.etBotToken);
        etChatId = findViewById(R.id.etChatId);
        etDailyLimit = findViewById(R.id.etDailyLimit);
        swAutoSync = findViewById(R.id.swAutoSync);
        swWiFiOnly = findViewById(R.id.swWiFiOnly);
        swNotifications = findViewById(R.id.swNotifications);
        btnSave = findViewById(R.id.btnSave);
        btnTestConnection = findViewById(R.id.btnTestConnection);
        
        // Load preferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadSettings();
        
        // Set up button listeners
        btnSave.setOnClickListener(v -> saveSettings());
        btnTestConnection.setOnClickListener(v -> testConnection());
    }
    
    private void loadSettings() {
        etBotToken.setText(prefs.getString("bot_token", ""));
        etChatId.setText(prefs.getString("chat_id", ""));
        etDailyLimit.setText(String.valueOf(prefs.getInt("daily_limit", 500)));
        swAutoSync.setChecked(prefs.getBoolean("auto_sync", true));
        swWiFiOnly.setChecked(prefs.getBoolean("wifi_only", true));
        swNotifications.setChecked(prefs.getBoolean("notifications", true));
    }
    
    private void saveSettings() {
        SharedPreferences.Editor editor = prefs.edit();
        
        String botToken = etBotToken.getText().toString().trim();
        String chatId = etChatId.getText().toString().trim();
        String dailyLimitStr = etDailyLimit.getText().toString().trim();
        
        if (botToken.isEmpty() || chatId.isEmpty()) {
            Toast.makeText(this, "Bot Token and Chat ID are required", Toast.LENGTH_SHORT).show();
            return;
        }
        
        editor.putString("bot_token", botToken);
        editor.putString("chat_id", chatId);
        
        try {
            int dailyLimit = Integer.parseInt(dailyLimitStr);
            editor.putInt("daily_limit", dailyLimit);
        } catch (NumberFormatException e) {
            editor.putInt("daily_limit", 500);
        }
        
        editor.putBoolean("auto_sync", swAutoSync.isChecked());
        editor.putBoolean("wifi_only", swWiFiOnly.isChecked());
        editor.putBoolean("notifications", swNotifications.isChecked());
        
        editor.apply();
        
        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
    }
    
    private void testConnection() {
        String botToken = etBotToken.getText().toString().trim();
        String chatId = etChatId.getText().toString().trim();
        
        if (botToken.isEmpty() || chatId.isEmpty()) {
            Toast.makeText(this, "Please enter Bot Token and Chat ID first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // In a real app, this would test Telegram API connection
        Toast.makeText(this, "Testing connection to Telegram...", Toast.LENGTH_SHORT).show();
        
        // Simulate connection test
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                runOnUiThread(() -> {
                    Toast.makeText(SettingsActivity.this, 
                        "Connection successful! Bot is ready.", Toast.LENGTH_LONG).show();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Auto-save on pause
        saveSettings();
    }
}