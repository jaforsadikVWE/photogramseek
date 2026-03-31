package com.guardx.mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import okhttp3.*;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TelegramHelper {
    
    private static final String TAG = "TelegramHelper";
    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";
    
    private Context context;
    private OkHttpClient client;
    private String botToken;
    private String chatId;
    
    public TelegramHelper(Context context) {
        this.context = context;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        
        SharedPreferences prefs = context.getSharedPreferences("GuardXPrefs", Context.MODE_PRIVATE);
        this.botToken = prefs.getString("bot_token", "");
        this.chatId = prefs.getString("chat_id", "");
    }
    
    public boolean isConfigured() {
        return !botToken.isEmpty() && !chatId.isEmpty();
    }
    
    public void updateCredentials(String botToken, String chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
    }
    
    public boolean testConnection() throws IOException {
        if (!isConfigured()) {
            return false;
        }
        
        String url = TELEGRAM_API_URL + botToken + "/getMe";
        Request request = new Request.Builder()
                .url(url)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                return json.getBoolean("ok");
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Connection test failed", e);
            return false;
        }
    }
    
    public boolean sendMessage(String message, String threadId) throws IOException {
        if (!isConfigured()) {
            return false;
        }
        
        String url = TELEGRAM_API_URL + botToken + "/sendMessage";
        
        JSONObject json = new JSONObject();
        try {
            json.put("chat_id", chatId);
            json.put("text", message);
            if (threadId != null && !threadId.isEmpty()) {
                json.put("message_thread_id", threadId);
            }
            json.put("parse_mode", "HTML");
        } catch (Exception e) {
            Log.e(TAG, "JSON creation failed", e);
            return false;
        }
        
        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }
    
    public boolean sendPhoto(File photoFile, String caption, String threadId) throws IOException {
        if (!isConfigured()) {
            return false;
        }
        
        String url = TELEGRAM_API_URL + botToken + "/sendPhoto";
        
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("chat_id", chatId)
                .addFormDataPart("photo", photoFile.getName(),
                        RequestBody.create(photoFile, MediaType.parse("image/*")))
                .addFormDataPart("caption", caption)
                .addFormDataPart("message_thread_id", threadId)
                .build();
        
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }
    
    public boolean createTopic(String topicName) throws IOException {
        if (!isConfigured()) {
            return false;
        }
        
        String url = TELEGRAM_API_URL + botToken + "/createForumTopic";
        
        JSONObject json = new JSONObject();
        try {
            json.put("chat_id", chatId);
            json.put("name", topicName);
        } catch (Exception e) {
            Log.e(TAG, "JSON creation failed", e);
            return false;
        }
        
        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject responseJson = new JSONObject(responseBody);
                return responseJson.getBoolean("ok");
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Create topic failed", e);
            return false;
        }
    }
    
    public String getTopicId(String topicName) throws IOException {
        if (!isConfigured()) {
            return null;
        }
        
        String url = TELEGRAM_API_URL + botToken + "/getForumTopics";
        
        JSONObject json = new JSONObject();
        try {
            json.put("chat_id", chatId);
        } catch (Exception e) {
            Log.e(TAG, "JSON creation failed", e);
            return null;
        }
        
        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject responseJson = new JSONObject(responseBody);
                // Parse topics and find matching name
                // This is simplified - actual implementation would parse the topics array
                return "1"; // Default thread ID
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Get topics failed", e);
            return null;
        }
    }
}