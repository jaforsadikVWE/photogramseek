package com.photogram.backup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private static final int PERMISSION_REQUEST_CODE = 100;
    private Button btnSyncNow, btnDashboard, btnSettings;
    private RecyclerView folderRecyclerView;
    private FolderAdapter folderAdapter;
    private List<FolderItem> folderList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize UI components
        btnSyncNow = findViewById(R.id.btnSyncNow);
        btnDashboard = findViewById(R.id.btnDashboard);
        btnSettings = findViewById(R.id.btnSettings);
        folderRecyclerView = findViewById(R.id.folderRecyclerView);
        
        // Setup RecyclerView
        folderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        folderAdapter = new FolderAdapter(folderList);
        folderRecyclerView.setAdapter(folderAdapter);
        
        // Request permissions
        if (!hasRequiredPermissions()) {
            requestPermissions();
        } else {
            loadFolders();
        }
        
        // Set up button listeners
        btnSyncNow.setOnClickListener(v -> startSync());
        btnDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }
    
    private boolean hasRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                   ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }
    
    private void requestPermissions() {
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        
        ActivityCompat.requestPermissions(this, 
            permissions.toArray(new String[0]), 
            PERMISSION_REQUEST_CODE);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                loadFolders();
            } else {
                Toast.makeText(this, "Permissions are required for the app to function", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void loadFolders() {
        // Simulate loading folders (in real app, would scan MediaStore)
        folderList.clear();
        folderList.add(new FolderItem("Camera", "DCIM/Camera", 150, true));
        folderList.add(new FolderItem("Screenshots", "Pictures/Screenshots", 89, true));
        folderList.add(new FolderItem("Downloads", "Download", 45, false));
        folderList.add(new FolderItem("WhatsApp Images", "WhatsApp/Media/WhatsApp Images", 320, true));
        folderAdapter.notifyDataSetChanged();
    }
    
    private void startSync() {
        Intent serviceIntent = new Intent(this, BackupService.class);
        serviceIntent.setAction("START_SYNC");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        Toast.makeText(this, "Sync started in background", Toast.LENGTH_SHORT).show();
    }
}