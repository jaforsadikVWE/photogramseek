package com.guardx.mobile;

public class FolderItem {
    private String name;
    private String path;
    private int photoCount;
    private boolean selected;
    private String threadId;
    
    public FolderItem(String name, String path, int photoCount, boolean selected) {
        this.name = name;
        this.path = path;
        this.photoCount = photoCount;
        this.selected = selected;
        this.threadId = "";
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public int getPhotoCount() {
        return photoCount;
    }
    
    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public String getThreadId() {
        return threadId;
    }
    
    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }
}