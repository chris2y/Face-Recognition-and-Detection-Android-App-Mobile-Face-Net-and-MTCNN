package com.example.facerecognition20.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.facerecognition20.Model.CommentModel;
import com.example.facerecognition20.Model.ShareModel;
import com.example.facerecognition20.R;
import com.example.facerecognition20.SocialShareDetailActivity;
import com.example.facerecognition20.Util.FirebaseUtil;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    Context context;
    List<CommentModel> items;


    public CommentAdapter(Context context, List<CommentModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_post_comment_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CommentModel currentItem = items.get(position);

        holder.detail.setText(currentItem.getCommentText());
        holder.postedTime.setText(FirebaseUtil.getRelativeTimeAgo(currentItem.getTimestamp()));


        FirebaseUtil.loadFullName(currentItem.getUserId(),
                fullName -> holder.fullName.setText(fullName));


        FirebaseUtil.loadProfileImage(currentItem.getUserId(), profileImageUrl ->
                Glide.with(context.getApplicationContext())
                        .load(profileImageUrl)
                        .into(holder.profile));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView detail, postedTime , fullName;
        ImageView profile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            detail = itemView.findViewById(R.id.commentTextView);
            postedTime = itemView.findViewById(R.id.postedTimeTextView);
            fullName = itemView.findViewById(R.id.fullNameTextView);
            profile = itemView.findViewById(R.id.profile);
        }
    }


}
