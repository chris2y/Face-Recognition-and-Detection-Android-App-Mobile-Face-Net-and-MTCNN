package com.example.facerecognition20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.facerecognition20.Adapters.CommentAdapter;
import com.example.facerecognition20.Model.CommentModel;
import com.example.facerecognition20.Util.FirebaseUtil;
import com.example.facerecognition20.Util.ReportDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocialShareDetailActivity extends AppCompatActivity {

    TextInputLayout editText;
    ImageButton backBtn,reportBtn;
    ImageView profile,post;

    TextView fullNameTextView,descriptionTextView,postedTimeTextView;

    private String itemKey;
    private String imageUrl;
    private String postedTime;
    private String description;
    private String poster;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_share_detail);

        editText = findViewById(R.id.chatQuery);
        backBtn = findViewById(R.id.backButton);
        profile = findViewById(R.id.profile);
        post = findViewById(R.id.imageView);
        reportBtn = findViewById(R.id.reportButton);
        fullNameTextView = findViewById(R.id.fullNameTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        postedTimeTextView = findViewById(R.id.postedTimeTextView);

        Intent intent = getIntent();
        itemKey = intent.getStringExtra("Key");
        imageUrl = intent.getStringExtra("imageUrl");
        postedTime = intent.getStringExtra("postedTime");
        description = intent.getStringExtra("description");
        poster = intent.getStringExtra("poster");

        FirebaseUtil.loadFullName(poster, fullname ->
                fullNameTextView.setText(fullname));

        descriptionTextView.setText(description);
        postedTimeTextView.setText(String.valueOf(postedTime));

        updateRecyclerView();

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReportDialog reportDialog = new ReportDialog(SocialShareDetailActivity.this,
                        FirebaseUtil.getCurrentUserId(),poster,itemKey);
                reportDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                reportDialog.setCancelable(false);
                reportDialog.show();
            }
        });

        Glide.with(SocialShareDetailActivity.this)
                .load(imageUrl)
                .into(post);

        FirebaseUtil.loadProfileImage(poster, profileImageUrl ->
                Glide.with(SocialShareDetailActivity.this)
                        .load(profileImageUrl)
                        .into(profile));


        editText.getEditText().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Check if touch event is within the bounds of the drawable end
                if (event.getRawX() >= (editText.getEditText().getRight() - editText.getEditText().getCompoundDrawables()[2].getBounds().width())) {

                    String comment = editText.getEditText().getText().toString().trim();
                    if (comment.isEmpty()) {
                        editText.getEditText().setError("Enter comment");
                        return true;
                    }
                    sendComment(comment);
                    editText.getEditText().setText("");

                    return true;
                }
            }
            // Return false to allow other touch events to be handled
            return false;
        });


        editText.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                // Get the text from the TextInputEditText

                String comment = editText.getEditText().getText().toString().trim();
                if (comment.isEmpty()) {
                    editText.getEditText().setError("Enter comment");
                    return true;
                }
                sendComment(comment);
                editText.getEditText().setText("");
                return true;
            }
            return false;
        });

        backBtn.setOnClickListener((v)->{
            onBackPressed();
        });



    }



    private void sendComment(String comment) {

        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(itemKey);


        // Generate a unique comment ID
        String commentId = commentsRef.push().getKey();

        Map<String, Object> commentData = new HashMap<>();
        commentData.put("commentText", comment);
        commentData.put("timestamp", ServerValue.TIMESTAMP);
        commentData.put("userId", FirebaseUtil.getCurrentUserId());

        commentsRef.child(commentId).setValue(commentData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SocialShareDetailActivity.this, "Comment added successfully", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SocialShareDetailActivity.this, "Comment added failed", Toast.LENGTH_SHORT).show();

                });

    }

    private void updateRecyclerView() {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(itemKey);

        commentsRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<CommentModel> commentList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CommentModel comment = snapshot.getValue(CommentModel.class);
                    commentList.add(comment);
                }

                // Initialize RecyclerView and set the adapter
                RecyclerView recyclerView = findViewById(R.id.commentRecyclerView);
                CommentAdapter commentAdapter = new CommentAdapter(SocialShareDetailActivity.this,commentList);
                LinearLayoutManager manager = new LinearLayoutManager(SocialShareDetailActivity.this);
                manager.setReverseLayout(true);
                manager.setStackFromEnd(true);
                recyclerView.setLayoutManager(manager);

                recyclerView.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Toast.makeText(SocialShareDetailActivity.this, "Failed to retrieve comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

}