package com.photogram.backup;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaStoreScanner {
    
    private static final String TAG = "MediaStoreScanner";
    private Context context;
    private ContentResolver contentResolver;
    
    public MediaStoreScanner(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }
    
    public List<FolderItem> scanPhotoFolders() {
        List<FolderItem> folders = new ArrayList<>();
        Map<String, FolderItem> folderMap = new HashMap<>();
        
        String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID
        };
        
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";
        
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        
        try (Cursor cursor = contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder)) {
            
            if (cursor != null) {
                int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                int bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                int bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
                
                while (cursor.moveToNext()) {
                    String filePath = cursor.getString(dataColumn);
                    String bucketName = cursor.getString(bucketNameColumn);
                    String bucketId = cursor.getString(bucketIdColumn);
                    
                    if (filePath != null && bucketName != null) {
                        // Extract folder path
                        File file = new File(filePath);
                        String folderPath = file.getParent();
                        
                        if (folderPath != null) {
                            FolderItem folder = folderMap.get(bucketId);
                            if (folder == null) {
                                folder = new FolderItem(bucketName, folderPath, 1, true);
                                folderMap.put(bucketId, folder);
                            } else {
                                folder.setPhotoCount(folder.getPhotoCount() + 1);
                            }
                        }
                    }
                }
                
                // Convert map to list
                folders.addAll(folderMap.values());
                
                Log.d(TAG, "Found " + folders.size() + " photo folders");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error scanning MediaStore", e);
        }
        
        return folders;
    }
    
    public List<String> getNewPhotosSince(long lastSyncTime) {
        List<String> newPhotos = new ArrayList<>();
        
        String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };
        
        String selection = MediaStore.Images.Media.DATE_ADDED + " > ?";
        String[] selectionArgs = {String.valueOf(lastSyncTime)};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " ASC";
        
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        
        try (Cursor cursor = contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder)) {
            
            if (cursor != null) {
                int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
                int bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                
                while (cursor.moveToNext()) {
                    String filePath = cursor.getString(dataColumn);
                    long dateAdded = cursor.getLong(dateAddedColumn);
                    String bucketName = cursor.getString(bucketNameColumn);
                    
                    if (filePath != null) {
                        newPhotos.add(filePath);
                        Log.d(TAG, "New photo: " + filePath + " (bucket: " + bucketName + ")");
                    }
                }
                
                Log.d(TAG, "Found " + newPhotos.size() + " new photos since " + lastSyncTime);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting new photos", e);
        }
        
        return newPhotos;
    }
    
    public int getTotalPhotoCount() {
        String[] projection = {MediaStore.Images.Media._ID};
        
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        
        try (Cursor cursor = contentResolver.query(
                collection,
                projection,
                null,
                null,
                null)) {
            
            if (cursor != null) {
                return cursor.getCount();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting total photo count", e);
        }
        
        return 0;
    }
}