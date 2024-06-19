package com.example.facerecognition20.Util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.facerecognition20.Model.ReportModel;
import com.example.facerecognition20.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ReportDialog extends Dialog {

    String myUid;
    String otherUid;
    String postId;
    String reportType;
    ProgressBar progressBar;

    Button submit;
    TextInputLayout editText;
    TextView reportTypeTextView;
    String reportToast = "Report Successful";
    private DatabaseReference reportsRef;



    public ReportDialog(@NonNull Context context, String myUid, String otherUid, String postId) {
        super(context);
        this.myUid = myUid;
        this.otherUid = otherUid;
        this.postId = postId;
        reportsRef = FirebaseDatabase.getInstance().getReference().child("reports");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_dialog);

        submit = findViewById(R.id.submitBtn);
        Button cancel = findViewById(R.id.cancelBtn);
        editText = findViewById(R.id.reportEditTxt);
        progressBar = findViewById(R.id.reportProgressBar);
        reportTypeTextView = findViewById(R.id.reportTextView);

        // Set the dialog's width to MATCH_PARENT and center it horizontally
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.CENTER;
        getWindow().setAttributes(layoutParams);


        submit.setOnClickListener(v -> {
            editText.setError(null); // Clear any previous errors
            String reportText = editText.getEditText().getText().toString().trim();
            int reportLength = reportText.length();

            // Check if the review length is less than 20 or greater than 500
             if (reportLength == 0) {
                editText.setError("Report must not be empty");
            } else if (reportLength < 20) {
                 editText.setError("Report must be at least 20 characters");
             }else if (reportLength > 500) {
                 editText.setError("Report must be less than 500 characters");
             }else {
                 progressBar.setVisibility(View.VISIBLE);
                 saveDataToRealtimeDatabase(reportText);
             }

        });

        cancel.setOnClickListener(v -> {
            dismiss();
        });

    }

    private void saveDataToRealtimeDatabase(String reportText) {
        // Get the current user ID
        String currentUserId = FirebaseUtil.getCurrentUserId();

        // Get a reference to the "reports" node in the Realtime Database
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference().child("reports");

        // Create a new child node under "reports" with a unique ID
        String reportId = reportsRef.push().getKey();

        // Create a Map to represent the data
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("reportText", reportText);
        reportData.put("timestamp", ServerValue.TIMESTAMP); // Add timestamp
        reportData.put("reporterId", currentUserId);
        reportData.put("otherUid", otherUid);
        reportData.put("postId", postId);

        // Set the data in the Realtime Database
        reportsRef.child(postId).child(currentUserId).setValue(reportData)
                .addOnSuccessListener(aVoid -> {
                    // Report saved successfully
                    progressBar.setVisibility(View.INVISIBLE);
                    dismiss();
                    Toast.makeText(getContext(), "Report Successful", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Error Reporting", Toast.LENGTH_SHORT).show();
                });
    }


}


