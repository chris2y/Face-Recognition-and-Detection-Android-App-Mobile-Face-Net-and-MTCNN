package com.example.facerecognition20.mtcnn;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseUtil {

    public static void fetchDataFromDB(String name , final clientDetailCallback clientDetailCallback) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("celebrities").child(name);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String job = dataSnapshot.child("job").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    String x = dataSnapshot.child("x").getValue(String.class);
                    String youtube = dataSnapshot.child("youtube").getValue(String.class);
                    String tiktok = dataSnapshot.child("tiktok").getValue(String.class);
                    String instagram = dataSnapshot.child("instagram").getValue(String.class);

                    clientDetailCallback.onResult(name,job,imageUrl,x,youtube,tiktok,instagram);
                } else {
                    // Use a default image or handle the absence of profile image here
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

    }

    public interface clientDetailCallback {
        void onResult(String name,String job,String imageUrl,String x,String youtube,String tiktok,String instagram);
    }

}
