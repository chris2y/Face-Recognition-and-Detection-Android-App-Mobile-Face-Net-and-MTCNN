package com.example.facerecognition20.Util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.facerecognition20.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ShareDialog extends Dialog {

    String myUid;
    String otherUid;
    String jobId;
    String reportType;
    Bitmap bitmap;
    ProgressBar progressBar;

    Button submit;
    ImageView imageView;
    TextInputLayout editText;
    TextView reportTypeTextView;
    String reportToast = "Report Successful";
    private DatabaseReference reportsRef;



    public ShareDialog(@NonNull Context context, Bitmap bitmap) {
        super(context);
        this.bitmap = bitmap;
        reportsRef = FirebaseDatabase.getInstance().getReference().child("reports");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_to_social_dialog);

        submit = findViewById(R.id.submitBtn);
        Button cancel = findViewById(R.id.cancelBtn);
        editText = findViewById(R.id.reportEditTxt);
        progressBar = findViewById(R.id.reportProgressBar);
        reportTypeTextView = findViewById(R.id.reportTextView);
        imageView = findViewById(R.id.profile_pic_layout);


        // Set the dialog's width to MATCH_PARENT and center it horizontally
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.CENTER;
        getWindow().setAttributes(layoutParams);

        imageView.setImageBitmap(bitmap);

        submit.setOnClickListener(v -> {
            editText.setError(null); // Clear any previous errors
            String reportText = editText.getEditText().getText().toString().trim();
            int reportLength = reportText.length();

            // Check if the review length is less than 20 or greater than 500
             if (reportLength == 0) {
                editText.setError("Detail must not be empty");
            }else if (reportLength > 100) {
                 editText.setError("Detail must be less than 100 characters");
             }else {
                 progressBar.setVisibility(View.VISIBLE);
                 saveReport(reportText);
             }

        });


        cancel.setOnClickListener(v -> {
            dismiss();
        });


    }

    private void saveReport(String reportText) {
        // Get a reference to the Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");

        // Create a unique ID for the image
        String imageId = reportsRef.push().getKey();

        // Create a reference to the image in Firebase Storage
        StorageReference imageRef = storageRef.child(imageId + ".jpg");

        // Convert the Bitmap to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload the image to Firebase Storage
        UploadTask uploadTask = imageRef.putBytes(data);

        // Listen for the success or failure of the upload
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL of the uploaded image
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Save the data to Firestore
                saveDataToRealtimeDatabase(reportText, uri.toString());
            });
        }).addOnFailureListener(exception -> {
            // Handle the error
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Error uploading image", Toast.LENGTH_SHORT).show();
        });
    }
    private void saveDataToRealtimeDatabase(String reportText, String imageUrl) {
        // Get the current user ID
        String currentUserId = FirebaseUtil.getCurrentUserId();

        // Get a reference to the "reports" node in the Realtime Database
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference().child("posts");

        // Create a new child node under "reports" with a unique ID
        String reportId = reportsRef.push().getKey();

        // Create a Map to represent the data
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("reportText", reportText);
        reportData.put("imageUrl", imageUrl);
        reportData.put("timestamp", ServerValue.TIMESTAMP); // Add timestamp
        reportData.put("userId", currentUserId); // Add user ID

        // Set the data in the Realtime Database
        reportsRef.child(reportId).setValue(reportData)
                .addOnSuccessListener(aVoid -> {
                    // Report saved successfully
                    progressBar.setVisibility(View.INVISIBLE);
                    dismiss();
                    Toast.makeText(getContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Error Uploading", Toast.LENGTH_SHORT).show();
                });
    }




}


