package com.photogram.backup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {
    
    private List<FolderItem> folderList;
    
    public FolderAdapter(List<FolderItem> folderList) {
        this.folderList = folderList;
    }
    
    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        FolderItem folder = folderList.get(position);
        
        holder.folderName.setText(folder.getName());
        holder.folderPath.setText(folder.getPath());
        holder.photoCount.setText(folder.getPhotoCount() + " photos");
        holder.checkBox.setChecked(folder.isSelected());
        
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            folder.setSelected(isChecked);
        });
    }
    
    @Override
    public int getItemCount() {
        return folderList.size();
    }
    
    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView folderName;
        TextView folderPath;
        TextView photoCount;
        
        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            folderName = itemView.findViewById(R.id.folderName);
            folderPath = itemView.findViewById(R.id.folderPath);
            photoCount = itemView.findViewById(R.id.photoCount);
        }
    }
}