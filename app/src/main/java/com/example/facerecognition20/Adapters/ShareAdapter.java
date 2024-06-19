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
import com.example.facerecognition20.Model.ShareModel;
import com.example.facerecognition20.R;
import com.example.facerecognition20.SocialShareDetailActivity;
import com.example.facerecognition20.Util.FirebaseUtil;


import java.util.List;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.MyViewHolder> {

    Context context;
    List<ShareModel> items;


    public ShareAdapter(Context context, List<ShareModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_scocial_share_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ShareModel currentItem = items.get(position);

        holder.detail.setText(currentItem.getReportText());
        holder.postedTime.setText("Posted: " + FirebaseUtil.getRelativeTimeAgo(currentItem.getTimestamp()));

        FirebaseUtil.loadFullName(currentItem.getUserId(),
                fullName -> holder.fullName.setText(fullName));


        FirebaseUtil.loadProfileImage(currentItem.getUserId(), profileImageUrl ->
                Glide.with(context.getApplicationContext())
                        .load(profileImageUrl)
                        .into(holder.profile));

        Glide.with(context.getApplicationContext())
                .load(currentItem.getImageUrl())
                .into(holder.sharedImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                String itemKey = items.get(position).getKey();

                Intent intent = new Intent(context, SocialShareDetailActivity.class);
                intent.putExtra("Key", itemKey);
                intent.putExtra("imageUrl",currentItem.getImageUrl());
                intent.putExtra("postedTime",FirebaseUtil.getRelativeTimeAgo(currentItem.getTimestamp()));
                intent.putExtra("description",currentItem.getReportText());
                intent.putExtra("poster",currentItem.getUserId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView detail, postedTime , fullName;
        ImageView profile,sharedImage;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            detail = itemView.findViewById(R.id.descriptionTextView);
            postedTime = itemView.findViewById(R.id.postedTimeTextView);
            fullName = itemView.findViewById(R.id.fullNameTextView);
            sharedImage = itemView.findViewById(R.id.imageView);
            profile = itemView.findViewById(R.id.profile);

        }
    }


}
