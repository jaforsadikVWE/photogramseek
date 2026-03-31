package com.guardx.mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "guardx.db";
    private static final int DATABASE_VERSION = 1;
    
    // Table names
    private static final String TABLE_UPLOADS = "uploads";
    private static final String TABLE_FOLDERS = "folders";
    private static final String TABLE_SYNC_HISTORY = "sync_history";
    
    // Common column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CREATED_AT = "created_at";
    
    // Uploads table columns
    private static final String COLUMN_FILE_NAME = "file_name";
    private static final String COLUMN_FILE_PATH = "file_path";
    private static final String COLUMN_FOLDER_NAME = "folder_name";
    private static final String COLUMN_THREAD_ID = "thread_id";
    private static final String COLUMN_UPLOAD_TIME = "upload_time";
    private static final String COLUMN_FILE_SIZE = "file_size";
    private static final String COLUMN_STATUS = "status";
    
    // Folders table columns
    private static final String COLUMN_FOLDER_PATH = "folder_path";
    private static final String COLUMN_PHOTO_COUNT = "photo_count";
    private static final String COLUMN_IS_SELECTED = "is_selected";
    private static final String COLUMN_LAST_SCAN_TIME = "last_scan_time";
    
    // Sync history table columns
    private static final String COLUMN_SYNC_TIME = "sync_time";
    private static final String COLUMN_PHOTOS_UPLOADED = "photos_uploaded";
    private static final String COLUMN_TOTAL_SIZE = "total_size";
    private static final String COLUMN_DURATION = "duration";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create uploads table
        String createUploadsTable = "CREATE TABLE " + TABLE_UPLOADS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FILE_NAME + " TEXT NOT NULL, " +
                COLUMN_FILE_PATH + " TEXT NOT NULL, " +
                COLUMN_FOLDER_NAME + " TEXT NOT NULL, " +
                COLUMN_THREAD_ID + " TEXT, " +
                COLUMN_UPLOAD_TIME + " INTEGER NOT NULL, " +
                COLUMN_FILE_SIZE + " INTEGER, " +
                COLUMN_STATUS + " INTEGER DEFAULT 1, " + // 1 = success, 0 = failed
                COLUMN_CREATED_AT + " INTEGER DEFAULT (strftime('%s', 'now'))" +
                ");";
        
        // Create folders table
        String createFoldersTable = "CREATE TABLE " + TABLE_FOLDERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FOLDER_NAME + " TEXT NOT NULL, " +
                COLUMN_FOLDER_PATH + " TEXT NOT NULL UNIQUE, " +
                COLUMN_PHOTO_COUNT + " INTEGER DEFAULT 0, " +
                COLUMN_IS_SELECTED + " INTEGER DEFAULT 1, " +
                COLUMN_THREAD_ID + " TEXT, " +
                COLUMN_LAST_SCAN_TIME + " INTEGER, " +
                COLUMN_CREATED_AT + " INTEGER DEFAULT (strftime('%s', 'now'))" +
                ");";
        
        // Create sync history table
        String createSyncHistoryTable = "CREATE TABLE " + TABLE_SYNC_HISTORY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SYNC_TIME + " INTEGER NOT NULL, " +
                COLUMN_PHOTOS_UPLOADED + " INTEGER DEFAULT 0, " +
                COLUMN_TOTAL_SIZE + " INTEGER DEFAULT 0, " +
                COLUMN_DURATION + " INTEGER DEFAULT 0, " +
                COLUMN_STATUS + " INTEGER DEFAULT 1, " +
                COLUMN_CREATED_AT + " INTEGER DEFAULT (strftime('%s', 'now'))" +
                ");";
        
        db.execSQL(createUploadsTable);
        db.execSQL(createFoldersTable);
        db.execSQL(createSyncHistoryTable);
        
        Log.d(TAG, "Database tables created");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UPLOADS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYNC_HISTORY);
        onCreate(db);
    }
    
    // Upload operations
    public long recordUpload(String fileName, String filePath, String folderName, 
                            long uploadTime, String threadId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_FILE_NAME, fileName);
        values.put(COLUMN_FILE_PATH, filePath);
        values.put(COLUMN_FOLDER_NAME, folderName);
        values.put(COLUMN_THREAD_ID, threadId);
        values.put(COLUMN_UPLOAD_TIME, uploadTime);
        values.put(COLUMN_STATUS, 1);
        
        long id = db.insert(TABLE_UPLOADS, null, values);
        db.close();
        
        Log.d(TAG, "Recorded upload: " + fileName + " (ID: " + id + ")");
        return id;
    }
    
    public int getTotalUploadCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_UPLOADS + 
                      " WHERE " + COLUMN_STATUS + " = 1";
        
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        cursor.close();
        db.close();
        return count;
    }
    
    public int getTodayUploadCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Get today's date in format YYYY-MM-DD
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        String query = "SELECT COUNT(*) FROM " + TABLE_UPLOADS + 
                      " WHERE " + COLUMN_STATUS + " = 1 AND " +
                      "date(" + COLUMN_UPLOAD_TIME + "/1000, 'unixepoch') = date('" + today + "')";
        
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        cursor.close();
        db.close();
        return count;
    }
    
    public long getTotalUploadSize() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COLUMN_FILE_SIZE + ") FROM " + TABLE_UPLOADS + 
                      " WHERE " + COLUMN_STATUS + " = 1";
        
        Cursor cursor = db.rawQuery(query, null);
        long totalSize = 0;
        
        if (cursor.moveToFirst()) {
            totalSize = cursor.getLong(0);
        }
        
        cursor.close();
        db.close();
        return totalSize;
    }
    
    // Folder operations
    public long addOrUpdateFolder(FolderItem folder) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOLDER_NAME, folder.getName());
        values.put(COLUMN_FOLDER_PATH, folder.getPath());
        values.put(COLUMN_PHOTO_COUNT, folder.getPhotoCount());
        values.put(COLUMN_IS_SELECTED, folder.isSelected() ? 1 : 0);
        values.put(COLUMN_THREAD_ID, folder.getThreadId());
        values.put(COLUMN_LAST_SCAN_TIME, System.currentTimeMillis());
        
        // Try to update first
        int rowsAffected = db.update(TABLE_FOLDERS, values, 
                COLUMN_FOLDER_PATH + " = ?", new String[]{folder.getPath()});
        
        long id;
        if (rowsAffected == 0) {
            // Insert new row
            id = db.insert(TABLE_FOLDERS, null, values);
        } else {
            // Get the ID of updated row
            Cursor cursor = db.query(TABLE_FOLDERS, new String[]{COLUMN_ID},
                    COLUMN_FOLDER_PATH + " = ?", new String[]{folder.getPath()},
                    null, null, null);
            if (cursor.moveToFirst()) {
                id = cursor.getLong(0);
            } else {
                id = -1;
            }
            cursor.close();
        }
        
        db.close();
        return id;
    }
    
    public List<FolderItem> getSelectedFolders() {
        List<FolderItem> folders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_FOLDERS + 
                      " WHERE " + COLUMN_IS_SELECTED + " = 1" +
                      " ORDER BY " + COLUMN_FOLDER_NAME;
        
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
                FolderItem folder = new FolderItem(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOLDER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOLDER_PATH)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_COUNT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_SELECTED)) == 1
                );
                folder.setThreadId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THREAD_ID)));
                folders.add(folder);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return folders;
    }
    
    // Sync history operations
    public long recordSync(int photosUploaded, long totalSize, long duration, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_SYNC_TIME, System.currentTimeMillis());
        values.put(COLUMN_PHOTOS_UPLOADED, photosUploaded);
        values.put(COLUMN_TOTAL_SIZE, totalSize);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_STATUS, status);
        
        long id = db.insert(TABLE_SYNC_HISTORY, null, values);
        db.close();
        
        Log.d(TAG, "Recorded sync: " + photosUploaded + " photos, " + totalSize + " bytes");
        return id;
    }
    
    public long getLastSyncTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT MAX(" + COLUMN_SYNC_TIME + ") FROM " + TABLE_SYNC_HISTORY + 
                      " WHERE " + COLUMN_STATUS + " = 1";
        
        Cursor cursor = db.rawQuery(query, null);
        long lastSyncTime = 0;
        
        if (cursor.moveToFirst()) {
            lastSyncTime = cursor.getLong(0);
        }
        
        cursor.close();
        db.close();
        return lastSyncTime;
    }
    
    public int getTotalSyncCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_SYNC_HISTORY;
        
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        cursor.close();
        db.close();
        return count;
    }
}