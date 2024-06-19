package com.example.facerecognition20.Util;


import android.text.format.DateUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class FirebaseUtil {

    public static FirebaseAuth getAuthInstance(){
        return FirebaseAuth.getInstance();
    }

    public static String getCurrentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn(){
        if(getCurrentUserId() == null){
            return false;
        }
        return true;
    }

    public static CollectionReference allUserCollection() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getCurrentUserDocument() {
        return allUserCollection().document(getCurrentUserId());
    }


    public static void logout(){
       FirebaseAuth.getInstance().signOut();
    }

    public static void loadProfileImage(String userId, final profileCallback profileCallback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Reference to the "users" collection in Firestore
        CollectionReference usersCollection = firestore.collection("users");

        // Reference to the specific user document
        DocumentReference userDocRef = usersCollection.document(userId);

        // Get the profile image URL from Firestore
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Retrieve the profile image URL
                    String profileImageUrl = document.getString("dataProfileImage");

                    // Callback with the result
                    profileCallback.onResult(profileImageUrl);
                } else {
                    // Document doesn't exist, use a default image or handle the absence of profile image here
                }
            } else {
                // Handle errors
                Exception exception = task.getException();
                if (exception != null) {
                    // Handle the exception
                }
            }
        });
    }
    public static void loadFullName(String userId, final fullNameCallback profileCallback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Reference to the "users" collection in Firestore
        CollectionReference usersCollection = firestore.collection("users");

        // Reference to the specific user document
        DocumentReference userDocRef = usersCollection.document(userId);

        // Get the profile image URL from Firestore
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Retrieve the profile image URL
                    String fullName = document.getString("fullName");

                    // Callback with the result
                    profileCallback.onResult(fullName);
                } else {
                    // Document doesn't exist, use a default image or handle the absence of profile image here
                }
            } else {
                // Handle errors
                Exception exception = task.getException();
                if (exception != null) {
                    // Handle the exception
                }
            }
        });
    }

    public static String getRelativeTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        CharSequence relativeTimeSpan = DateUtils.getRelativeTimeSpanString(timestamp, now, DateUtils.MINUTE_IN_MILLIS);
        return relativeTimeSpan.toString();
    }


    public interface profileCallback {
        void onResult(String profileImageUrl);
    }
    public interface fullNameCallback {
        void onResult(String fullName);
    }
}
