package com.guardx.mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.json.JSONObject;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BackupWorker extends Worker {
    
    private static final String TAG = "BackupWorker";
    private Context context;
    private TelegramHelper telegramHelper;
    private DatabaseHelper databaseHelper;
    
    public BackupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.telegramHelper = new TelegramHelper(context);
        this.databaseHelper = new DatabaseHelper(context);
    }
    
    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "BackupWorker started");
        
        try {
            // Check if Telegram is configured
            if (!telegramHelper.isConfigured()) {
                Log.w(TAG, "Telegram not configured, skipping backup");
                return Result.failure();
            }
            
            // Check daily limit
            if (isDailyLimitExceeded()) {
                Log.w(TAG, "Daily upload limit exceeded");
                return Result.success();
            }
            
            // Check network constraints
            SharedPreferences prefs = context.getSharedPreferences("GuardXPrefs", Context.MODE_PRIVATE);
            boolean wifiOnly = prefs.getBoolean("wifi_only", true);
            
            // In real app, check network type here
            
            // Start backup process
            boolean success = performBackup();
            
            if (success) {
                Log.d(TAG, "Backup completed successfully");
                updateLastSyncTime();
                return Result.success();
            } else {
                Log.e(TAG, "Backup failed");
                return Result.failure();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in BackupWorker", e);
            return Result.failure();
        }
    }
    
    private boolean isDailyLimitExceeded() {
        SharedPreferences prefs = context.getSharedPreferences("GuardXPrefs", Context.MODE_PRIVATE);
        int dailyLimit = prefs.getInt("daily_limit", 500);
        
        // Get today's upload count from database
        int todayUploads = databaseHelper.getTodayUploadCount();
        
        return todayUploads >= dailyLimit;
    }
    
    private boolean performBackup() {
        Log.d(TAG, "Performing backup");
        
        try {
            // In a real app, this would:
            // 1. Scan for new photos using MediaStore
            // 2. Filter by selected folders
            // 3. Upload each photo to Telegram
            // 4. Update database
            
            // For demonstration, simulate uploading 5 photos
            for (int i = 0; i < 5; i++) {
                // Simulate photo upload
                String photoName = "photo_" + System.currentTimeMillis() + "_" + i + ".jpg";
                String folderName = "Camera";
                String threadId = "1"; // Default thread ID for Camera folder
                
                // In real app: telegramHelper.sendPhoto(photoFile, caption, threadId);
                boolean uploadSuccess = true; // Simulated success
                
                if (uploadSuccess) {
                    // Record upload in database
                    databaseHelper.recordUpload(
                            photoName,
                            folderName,
                            threadId,
                            new Date().getTime(),
                            "simulated_path.jpg"
                    );
                    
                    // Update daily count
                    incrementDailyUploadCount();
                    
                    Log.d(TAG, "Uploaded: " + photoName);
                }
                
                // Simulate delay between uploads
                Thread.sleep(1000);
            }
            
            // Send completion notification to Telegram
            String timestamp = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                    .format(new Date());
            String message = "✅ Backup completed at " + timestamp + "\n" +
                           "Uploaded 5 photos to Telegram";
            
            telegramHelper.sendMessage(message, null);
            
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Backup failed", e);
            
            // Send error notification
            try {
                String errorMessage = "❌ Backup failed: " + e.getMessage();
                telegramHelper.sendMessage(errorMessage, null);
            } catch (Exception ex) {
                Log.e(TAG, "Failed to send error notification", ex);
            }
            
            return false;
        }
    }
    
    private void incrementDailyUploadCount() {
        SharedPreferences prefs = context.getSharedPreferences("GuardXPrefs", Context.MODE_PRIVATE);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String key = "daily_count_" + today;
        
        int currentCount = prefs.getInt(key, 0);
        prefs.edit().putInt(key, currentCount + 1).apply();
    }
    
    private void updateLastSyncTime() {
        SharedPreferences prefs = context.getSharedPreferences("GuardXPrefs", Context.MODE_PRIVATE);
        long currentTime = System.currentTimeMillis();
        prefs.edit().putLong("last_sync_time", currentTime).apply();
    }
}