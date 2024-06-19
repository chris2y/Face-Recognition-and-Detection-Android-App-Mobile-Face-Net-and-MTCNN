package com.example.facerecognition20.Util;

import androidx.recyclerview.widget.DiffUtil;

import com.example.facerecognition20.Model.ShareModel;

import java.util.List;

public class ShareDiffCallback extends DiffUtil.Callback {

    private List<ShareModel> oldList;
    private List<ShareModel> newList;

    public ShareDiffCallback(List<ShareModel> oldList, List<ShareModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        ShareModel oldItem = oldList.get(oldItemPosition);
        ShareModel newItem = newList.get(newItemPosition);
        // Compare unique identifiers of the Home, e.g.,
        return oldItem.getKey().equals(newItem.getKey());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ShareModel oldItem = oldList.get(oldItemPosition);
        ShareModel newItem = newList.get(newItemPosition);
        // Compare the content of the Home to check if they are the same
        return oldItem.equals(newItem);
    }

}