package com.photogram.backup;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class BackupService extends Service {
    
    private static final String TAG = "BackupService";
    private static final String CHANNEL_ID = "photogram_backup_channel";
    private static final int NOTIFICATION_ID = 1001;
    private static final String WORK_TAG = "photogram_backup_work";
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification("Backup service running"));
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("START_SYNC".equals(action)) {
                startSync();
            } else if ("SCHEDULE_AUTO_SYNC".equals(action)) {
                scheduleAutoSync();
            }
        }
        
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Photogram Backup",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Background photo backup service");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    
    private Notification createNotification(String content) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Photogram Backup")
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_cloud_upload)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();
    }
    
    private void startSync() {
        Log.d(TAG, "Starting manual sync");
        
        // Update notification
        Notification notification = createNotification("Syncing photos to Telegram...");
        startForeground(NOTIFICATION_ID, notification);
        
        // In a real app, this would start the actual sync process
        // For now, we'll simulate with a worker
        Data inputData = new Data.Builder()
                .putBoolean("manual", true)
                .build();
        
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                BackupWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        
        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(WORK_TAG, ExistingPeriodicWorkPolicy.KEEP, workRequest);
        
        // Update notification when done
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Simulate work
                Notification doneNotification = createNotification("Sync completed");
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.notify(NOTIFICATION_ID, doneNotification);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void scheduleAutoSync() {
        SharedPreferences prefs = getSharedPreferences("PhotogramPrefs", MODE_PRIVATE);
        boolean autoSync = prefs.getBoolean("auto_sync", true);
        
        if (!autoSync) {
            WorkManager.getInstance(this).cancelUniqueWork(WORK_TAG);
            return;
        }
        
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED) // Wi-Fi only if configured
                .build();
        
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                BackupWorker.class, 1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();
        
        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(WORK_TAG, ExistingPeriodicWorkPolicy.KEEP, workRequest);
        
        Log.d(TAG, "Auto sync scheduled every 1 hour");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Backup service destroyed");
    }
}