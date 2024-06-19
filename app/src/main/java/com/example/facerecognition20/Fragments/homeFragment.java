package com.example.facerecognition20.Fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.MODE_PRIVATE;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.facerecognition20.FaceRecognitionProcessor;
import com.example.facerecognition20.Helper;
import com.example.facerecognition20.R;
import com.example.facerecognition20.Util.ShareDialog;
import com.example.facerecognition20.WebViewActivity;
import com.example.facerecognition20.mobilefacenet.MobileFaceNet;
import com.example.facerecognition20.mtcnn.FirebaseUtil;
import com.example.facerecognition20.mtcnn.MTCNN;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class homeFragment extends Fragment {
    private static String TAG = "MainActivity";

    private MTCNN mtcnn;
    private MobileFaceNet mfn;
    private ImageView imageView;
    private TextView nameTextView,jobTextView,similarityTextView;

    private Uri currentphotoUri;
    private File currentphotoFile;
    private String timeStamp;

    private final String APP_LOCATION = "faceRecognition";
    private final String FILE_PROVIDER_PATH = "com.nomihsa.facerecognition.fileprovider";
    private Interpreter faceNetInterpreter;
    private FaceRecognitionProcessor faceRecognitionProcessor;
    public boolean imageSelected = true;

    LinearLayout infoLayout,detailLayout;
    TextView unknownTextView;
    ProgressBar progressBar;
    ImageButton googleButton,tiktokButton,instagramButton,xButton,youtubeButton;

    String celebName,celebJob;
    Bitmap socialShareBitmap;
    View view;
    Button btn_social_hare;
    String xLink = "",youtubeLink = "",instagramLink = "",tiktokLink = "";

    private SharedPreferences sharedPreferences;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);



        //setSupportActionBar(toolbar);

        Button btn_register = view.findViewById(R.id.btn_recognize);
        Button btn_recognize = view.findViewById(R.id.btn_recognize);
        Button cropFaces = view.findViewById(R.id.cropFaces);
        Button registerFaces = view.findViewById(R.id.registerFaces);
        btn_social_hare = view.findViewById(R.id.btn_scocial_share);

        unknownTextView = view.findViewById(R.id.unknownTextView);
        imageView = view.findViewById(R.id.imageView);
        nameTextView = view.findViewById(R.id.textView);
        similarityTextView = view.findViewById(R.id.similarityTextView);
        jobTextView = view.findViewById(R.id.jobTextView);

        infoLayout = view.findViewById(R.id.infoLayout);
        detailLayout = view.findViewById(R.id.detailLayout);
        progressBar = view.findViewById(R.id.progressBar);

        googleButton = view.findViewById(R.id.googleButton);
        xButton = view.findViewById(R.id.xButton);
        instagramButton = view.findViewById(R.id.instaButton);
        youtubeButton = view.findViewById(R.id.youtubeButton);
        tiktokButton = view.findViewById(R.id.tiktokButton);


       googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create an intent to start WebViewActivity
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                String combinedQuery = celebName + " Ethiopian " + celebJob;
                intent.putExtra("query",combinedQuery);
                intent.putExtra("to","google");
                startActivity(intent);
            }
        });

        youtubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create an intent to start WebViewActivity
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("query",youtubeLink);
                intent.putExtra("to","youtube");
                startActivity(intent);

            }
        });

        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("query",instagramLink);
                intent.putExtra("to","instagram");
                startActivity(intent);
            }
        });

        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("query",xLink);
                intent.putExtra("to","x");
                startActivity(intent);
            }
        });

        tiktokButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("query",tiktokLink);
                intent.putExtra("to","x");
                startActivity(intent);
            }
        });




        try {
            faceNetInterpreter = new Interpreter(FileUtil.loadMappedFile(getActivity(), "mobile_face_net.tflite"), new Interpreter.Options());
        } catch (IOException e) {
            e.printStackTrace();
        }
        faceRecognitionProcessor = new FaceRecognitionProcessor(faceNetInterpreter,view.getContext().getApplicationContext());

        if (!checkPermissions()) {
            requestPermissions();
        }
        start();
        try {
            mtcnn = new MTCNN(view.getContext().getAssets());
            mfn = new MobileFaceNet(view.getContext().getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    take_photo(getActivity(), 0, "register");
                } else {
                    requestPermissions();
                }

            }
        });

        btn_social_hare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareDialog reportDialog = new ShareDialog(getContext(),socialShareBitmap);
                reportDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                reportDialog.setCancelable(false);
                reportDialog.show();
            }
        });

        btn_recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    take_photo(getActivity(), 2, "recognize");
                } else {
                    requestPermissions();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imageSelected) {
                    if (checkPermissions()) {
                        take_photo(getActivity(), 2, "recognize");
                    } else {
                        requestPermissions();
                    }
                }
            }
        });


        cropFaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    load_all_face_path();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        registerFaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    registerAllFace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });



        return view;
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                start();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.create()
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void start() {
        try {
            File folder = new File(view.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + APP_LOCATION + "/faces");
            if (!folder.exists()) {
                if (folder.mkdir()) {
                    Toast.makeText(getActivity(), "App folder created...", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception ex) {
            Log.e("Folder", "Error with creating Folder");
        }
    }

    private void take_photo(Context context, final int reference, String module) {
        final CharSequence[] options_reg = { "Choose from Gallery", "Cancel"};
        final CharSequence[] options_rec = { "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a photo");
        if (module.equals("register")) {
            builder.setItems(options_reg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options_reg[item].equals("Take Photo")) {
                        takeCameraPhoto(reference, "register");
                    } else if (options_reg[item].equals("Choose from Gallery")) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, reference + 1);
                    } else if (options_reg[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
        }
        if (module.equals("recognize")) {
            builder.setItems(options_rec, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options_rec[item].equals("Take Photo")) {
                        takeCameraPhoto(reference, "recognize");
                    } else if (options_rec[item].equals("Choose from Gallery")) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, reference + 1);
                    } else if (options_rec[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
        }
        builder.show();
    }


    private void takeCameraPhoto(final int requestCode, String module) {
        String storagePath = Environment.DIRECTORY_PODCASTS;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(view.getContext().getPackageManager()) != null) {
            try {
                timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                File storageDir = view.getContext().getExternalFilesDir(storagePath);

                if (storageDir != null) {
                    File image = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
                    currentphotoFile = image;

                    // Continue only if the File was successfully created
                    if (image != null) {
                        Uri photoUri = FileProvider.getUriForFile(getActivity(), FILE_PROVIDER_PATH, image);
                        currentphotoUri = photoUri;
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(takePictureIntent, requestCode);
                    } else {
                        // Handle the case when the file was not created successfully
                        showToast("Failed to create image file");
                    }
                } else {
                    // Handle the case when the storage directory is null
                    showToast("Failed to access storage directory");
                }
            } catch (IOException ex) {
                // Handle IOException
                ex.printStackTrace();
                showToast("Error occurred while creating image file");
            }
        } else {
            // Provide a user-friendly message or prompt to install a camera app
            showDialogToInstallCameraApp();
        }
    }
    private void showDialogToInstallCameraApp() {
        // Implement a dialog or navigate the user to the Play Store to install a camera app
        showToast("No camera app available. Please install a camera app.");
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0: {
                    request_name(currentphotoUri, "camera");
                }
                break;
                case 1: {
                    Uri selectedImage = data.getData();
                    request_name(selectedImage, "gallery");
                }
                break;
                case 2: {
                    recognize_face(currentphotoUri, "camera");
                }
                break;
                case 3: {
                    Uri selectedImage = data.getData();
                    recognize_face(selectedImage, "gallery");
                }
                break;
            }
        }
    }

    private void registerAllFace() throws IOException {
        File registeredFacesPath = view.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (registeredFacesPath.exists()) {
            File[] subdirectories = registeredFacesPath.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory();
                }
            });

            for (File subdirectory : subdirectories) {
                List<Bitmap> registeredFace = new ArrayList<>();

                File[] faces = subdirectory.listFiles();
                for (File faceData : faces) {
                    Bitmap bitmap = BitmapFactory.decodeFile(faceData.getAbsolutePath());
                    // Optionally, you can handle bitmap rotation or other preprocessing steps here if needed.
                    registeredFace.add(bitmap);
                }

                // Pass the list of registered face images to the vectorFromMultipleImages method
                faceRecognitionProcessor.vectorFromMultipleImages(registeredFace, subdirectory.getName());

                // Do something with the total vector if needed
            }
        }
    }



    private void load_all_face_path() throws IOException {
        File registeredFacesPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File saveFacesPath = view.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (registeredFacesPath.exists()) {
            // Get all subdirectories (child folders) in the Downloads directory
            File[] subdirectories = registeredFacesPath.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory();
                }
            });

            for (File subdirectory : subdirectories) {
                // Iterate through files within each subdirectory
                File[] faces = subdirectory.listFiles();

                // Check if the directory contains any images
                if (faces != null && faces.length > 0) {
                    for (File faceData : faces) {
                        // Get the URI of the registered face file
                        Uri registeredFaceUri = Uri.fromFile(faceData);
                        Bitmap bitmap = Helper.handleSamplingAndRotationBitmap(getActivity(), registeredFaceUri);
                        bitmap = Helper.detect_and_crop_face(getActivity(), mtcnn, bitmap);

                        if (bitmap != null){
                            String folderName = subdirectory.getName();
                            String fileName = registeredFaceUri.getLastPathSegment();
                            File cropped = new File(saveFacesPath, folderName + File.separator + fileName + "_cropped.jpg");

                            // Create folder if it doesn't exist
                            File folder = new File(cropped.getParent());
                            if (!folder.exists()) {
                                folder.mkdirs();
                            }

                            FileOutputStream out = new FileOutputStream(cropped);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                            Toast.makeText(getActivity(), "Face Cropped successfully", Toast.LENGTH_SHORT).show();
                            Log.d("Image Uri", registeredFaceUri.toString());
                        }
                    }
                }
            }
        }
    }






    private void register_face(Uri uri, String module, String person_name) {
        try {
            Bitmap bitmap = Helper.handleSamplingAndRotationBitmap(getActivity(), uri);



            imageView.setImageBitmap(bitmap);
            bitmap = Helper.detect_and_crop_face(getActivity(), mtcnn, bitmap);
            //register_all_face();


            if (bitmap != null) {
                float[] vector = faceRecognitionProcessor.vectorFromImage(bitmap);
                faceRecognitionProcessor.registerFace(person_name,vector);

                /*
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File cropped = new File(storageDir, person_name + ".jpg");
//              File cropped = File.createTempFile(person_name, ".jpg", storageDir);

                FileOutputStream out = new FileOutputStream(cropped);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                Toast.makeText(this, "Face registered successfully", Toast.LENGTH_SHORT).show();
                */
            }
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }

    private void delete_temp(){
        try {
            if (currentphotoFile.delete()) {
                if (currentphotoFile.exists()) {
                    currentphotoFile.getCanonicalFile().delete();
                    if (currentphotoFile.exists()) {
                        view.getContext().getApplicationContext().deleteFile(currentphotoFile.getName());
                    }
                }
            } else {
                Log.e("", "File not Deleted " + currentphotoUri.getPath());
            }
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private void request_name(final Uri uri, final String module) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Person Name");
        builder.setMessage("Enter the person's name you want to register. (else face will not be registered)");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);


        final EditText input = new EditText(getActivity());
        layout.addView(input);
        builder.setView(layout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                if (!text.equals("")) {
                    register_face(uri, module, text);
                } else {
                    delete_temp();
                    dialog.cancel();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }



    private void recognize_face(Uri uri, String module) {
        imageView.setImageBitmap(null);

        try {
            Bitmap new_image = Helper.handleSamplingAndRotationBitmap(getActivity(), uri);
            socialShareBitmap = new_image;

            imageView.setImageBitmap(new_image);
            imageSelected = false;
            btn_social_hare.setVisibility(View.GONE);
            unknownTextView.setVisibility(View.GONE);

            detailLayout.setVisibility(View.GONE);
            infoLayout.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.VISIBLE);

            new_image = Helper.detect_and_crop_face(getActivity(), mtcnn, new_image);
            Pair<String, Float> result = faceRecognitionProcessor.detectInImage(new_image);

            if (result != null && result.second < 1.0f) {
                Toast.makeText(getContext(),result.first,Toast.LENGTH_SHORT).show();

                xButton.setVisibility(View.VISIBLE);
                instagramButton.setVisibility(View.VISIBLE);
                youtubeButton.setVisibility(View.VISIBLE);
                tiktokButton.setVisibility(View.VISIBLE);

                FirebaseUtil.fetchDataFromDB(result.first, (name, job, imageUrl,x,youtube,tiktok,instagram) -> {

                    nameTextView.setTextColor(Color.BLACK);
                    nameTextView.setText(name);
                    jobTextView.setTextColor(Color.BLACK);
                    jobTextView.setText(job);

                    celebJob = job;
                    celebName = name;

                    xLink = x;
                    youtubeLink = youtube;
                    tiktokLink = tiktok;
                    instagramLink = instagram;

                    if (xLink == null || xLink.equals("")) {
                        xButton.setVisibility(View.GONE);
                    }
                    if (instagramLink == null || instagramLink.equals("")) {
                        instagramButton.setVisibility(View.GONE);
                    }
                    if (youtubeLink == null || youtubeLink.equals("")) {
                        youtubeButton.setVisibility(View.GONE);
                    }
                    if (tiktokLink == null || tiktokLink.equals("")) {
                        tiktokButton.setVisibility(View.GONE);
                    }

                    Glide.with(getContext())
                            .load(imageUrl)
                            .into(imageView);
                    progressBar.setVisibility(View.GONE);

                    infoLayout.setVisibility(View.GONE);
                    detailLayout.setVisibility(View.VISIBLE);

                    if(result.second < 0.6f){
                        similarityTextView.setTextColor(Color.GREEN);
                        similarityTextView.setText(String.format("%.2f", result.second));
                    }
                    else if(result.second < 0.8f){
                        similarityTextView.setTextColor(Color.YELLOW);
                        similarityTextView.setText(String.format("%.2f", result.second));
                    }
                    else if(result.second < 0.99f){
                        similarityTextView.setTextColor(Color.RED);
                        similarityTextView.setText(String.format("%.2f", result.second));
                    }
                });

            } else {
                infoLayout.setVisibility(View.VISIBLE);
                detailLayout.setVisibility(View.GONE);
                unknownTextView.setTextColor(Color.RED);
                unknownTextView.setVisibility(View.VISIBLE);
                unknownTextView.setText("Unknown Person");

               if(com.example.facerecognition20.Util.FirebaseUtil.isLoggedIn()){
                   btn_social_hare.setVisibility(View.VISIBLE);
               }

                progressBar.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }

}
