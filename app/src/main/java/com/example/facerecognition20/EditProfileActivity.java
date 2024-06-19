package com.example.facerecognition20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.facerecognition20.Util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
public class EditProfileActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private LinearLayout linearLayoutEditProfileWorker;
    private Button doneButton;
    private ImageButton backImage;

    private String email = "";
    private String phone = "";
    private String profession = "";
    private String education = "";
    private String skill = "";
    private String about = "";
    private String lookingTo = "";
    private String dataProfileImage = "";
    private String fullName = "";


    private EditText fullNameEditText, emailEditText, phoneEditText;
    private EditText professionEditText, educationEditText, skillEditText, aboutMeEditText;

    private StorageReference imageFolder;
    private FirebaseStorage storage;
    private String imageUrlForStoring;
    ValueEventListener eventListener;
    private static final int REQUEST_SELECT_IMAGE = 100;
    private Uri croppedImageUri;
    Uri selectedImageUri;
    ImageView selectImage,profileImageDisplay;
    String profileImagesUrl;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        doneButton = findViewById(R.id.doneButton);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        fullNameEditText = findViewById(R.id.fullNameEditTxt);

        selectImage = findViewById(R.id.editImage);
        profileImageDisplay = findViewById(R.id.accountimageEdit);



        selectImage.setOnClickListener(view -> selectImageIntent());




        doneButton.setOnClickListener(view -> {
            fullName = fullNameEditText.getText().toString();
            uploadProfileImage();
            Intent intent = getIntent();

            if (intent != null) {
                String email12 = intent.getStringExtra("Email");
                String uid = intent.getStringExtra("uid");
                String authToken = intent.getStringExtra("authToken");
                editor.putString("Email", email12);
                editor.putString("uid", uid);
                editor.putBoolean("isLoggedIn", true);
                editor.putString("authToken", authToken); // Save authentication token
                editor.apply();

            }

            // Assuming you have a reference to the Firestore collection where user profiles are stored
            DocumentReference userDocument = FirebaseUtil.getCurrentUserDocument();

            // Create a Map to store the data you want to update
            Map<String, Object> dataToUpdate = new HashMap<>();
            dataToUpdate.put("fullName", fullName);

            // Update the document with the new data
            userDocument.update(dataToUpdate)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Data updated successfully
                            startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                            Toast.makeText(EditProfileActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                            finish(); // or perform any other actions
                        } else {
                            // Handle task failure
                            Exception exception = task.getException();
                            if (exception != null) {
                                // Handle exception
                            }
                        }
                    });
        });



    }


    private void selectImageIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ActivityResult", "requestCode: " + requestCode + ", resultCode: " + resultCode);
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String randomFileName = "cropped_" + timeStamp + "_" + new Random().nextInt(1000);
            // Start the crop activity using UCrop library
            UCrop.of(selectedImageUri, Uri.fromFile(new File(getCacheDir(), randomFileName)))
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(720,720)
                    .start(this);

        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            croppedImageUri = UCrop.getOutput(data);
            // Assuming you have an ImageView named imageView and an Uri named imageUri
            Glide.with(this)
                    .load(croppedImageUri)
                    .into(profileImageDisplay);

            //uploadProfileImage();
            Log.d("Cropped then passed to Upload","Success");
            // Load the cropped image into an ImageView

        }
    }

    private void uploadProfileImage() {
        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        imageFolder = FirebaseStorage.getInstance().getReference().child("New User Profiles");
        StorageReference imageName = imageFolder.child(currentDate + " Image: " + selectedImageUri.getLastPathSegment());

        imageName.putFile(croppedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrlForStoring = uri.toString();
                        storeLinks(imageUrlForStoring);
                        Log.d("storeLinks","Success");

                    }
                });
            }
        });
    }

    private void storeLinks(String imageUrl) {
        Map<String, Object> data = new HashMap<>();
        data.put("dataProfileImage", imageUrl);
        dataProfileImage = imageUrl;

        FirebaseUtil.getCurrentUserDocument().update(data).addOnSuccessListener(unused -> {
            selectedImageUri = null;
            // If data uploaded successfully, show a toast
            Toast.makeText(getApplicationContext(), "Your data has been uploaded successfully", Toast.LENGTH_SHORT).show();
        });
    }


    private void deleteProfileImageFromStorage() {
        // Get a reference to the Firebase Storage instance
        storage = FirebaseStorage.getInstance();
        // Create a reference to the image file using its URL
        StorageReference imageRef = storage.getReferenceFromUrl(profileImagesUrl);
        // Delete the image file
        imageRef.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Image deleted successfully
                            storeLinks(imageUrlForStoring);
                            //deleteImageFromRealtimeDatabase();
                        } else {
                            // Handle the failure case when deleting the image
                            Exception e = task.getException();
                            if (e instanceof StorageException && ((StorageException) e).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                                // Handle the case when the object does not exist at the specified location
                                Log.e("ImageDeletionExample", "Image does not exist at the specified location");
                            } else {
                                // Handle other failure cases
                                Log.e("ImageDeletionExample", "Failed to delete image: " + e.getMessage(), e);
                            }
                        }
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}