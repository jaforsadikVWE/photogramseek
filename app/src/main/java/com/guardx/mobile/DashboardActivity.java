package com.guardx.mobile;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {
    
    private TextView tvTotalPhotos, tvUploadedPhotos, tvRemainingPhotos;
    private TextView tvLastSync, tvStorageUsed, tvDailyLimit;
    private CardView cardStats, cardUsage, cardStatus;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        // Initialize UI components
        tvTotalPhotos = findViewById(R.id.tvTotalPhotos);
        tvUploadedPhotos = findViewById(R.id.tvUploadedPhotos);
        tvRemainingPhotos = findViewById(R.id.tvRemainingPhotos);
        tvLastSync = findViewById(R.id.tvLastSync);
        tvStorageUsed = findViewById(R.id.tvStorageUsed);
        tvDailyLimit = findViewById(R.id.tvDailyLimit);
        cardStats = findViewById(R.id.cardStats);
        cardUsage = findViewById(R.id.cardUsage);
        cardStatus = findViewById(R.id.cardStatus);
        
        // Load dashboard data
        loadDashboardData();
    }
    
    private void loadDashboardData() {
        // In a real app, this would fetch from database
        int totalPhotos = 1250;
        int uploadedPhotos = 845;
        int remainingPhotos = totalPhotos - uploadedPhotos;
        String lastSync = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                            .format(new Date());
        String storageUsed = "2.4 GB";
        String dailyLimit = "500 photos / day";
        
        tvTotalPhotos.setText(String.valueOf(totalPhotos));
        tvUploadedPhotos.setText(String.valueOf(uploadedPhotos));
        tvRemainingPhotos.setText(String.valueOf(remainingPhotos));
        tvLastSync.setText(lastSync);
        tvStorageUsed.setText(storageUsed);
        tvDailyLimit.setText(dailyLimit);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to activity
        loadDashboardData();
    }
}