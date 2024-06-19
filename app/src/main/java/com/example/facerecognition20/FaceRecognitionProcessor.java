package com.example.facerecognition20;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FaceRecognitionProcessor {

    public static class Person implements Serializable {
        public String name;
        public float[] faceVector;

        public Person(String name, float[] faceVector) {
            this.name = name;
            this.faceVector = faceVector;
        }
    }
    private static final String FILE_NAME = "recognised_faces_data.txt";

    private static final String TAG = "FaceRecognitionProcessor";

    // Input image size for our facenet model
    private static final int FACENET_INPUT_IMAGE_SIZE = 112;

    private final Interpreter faceNetModelInterpreter;
    private final ImageProcessor faceNetImageProcessor;


    List<Person> recognisedFaceList = new ArrayList();

    Context context;

    public FaceRecognitionProcessor(Interpreter faceNetModelInterpreter, Context context) {
        // initialize processors
        this.faceNetModelInterpreter = faceNetModelInterpreter;
        loadRecognisedFacesFromExternalStorage(context);
        this.context = context;
        faceNetImageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(FACENET_INPUT_IMAGE_SIZE, FACENET_INPUT_IMAGE_SIZE, ResizeOp.ResizeMethod.BILINEAR))
                .add(new NormalizeOp(0f, 255f))
                .build();
    }
    public void saveRecognisedFacesToExternalStorage(Context context) {
        try {
            // Code to save recognisedFaceList to external storage
            File file = new File(context.getExternalFilesDir(null), FILE_NAME);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(recognisedFaceList);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error saving recognised faces data: " + e.getMessage());
        }
    }

    public void loadRecognisedFacesFromExternalStorage(Context context) {
        try {
            // Code to load recognisedFaceList from external storage
            File file = new File(context.getExternalFilesDir(null), FILE_NAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            recognisedFaceList = (List<Person>) objectInputStream.readObject();
            objectInputStream.close();
            Toast.makeText(context, "Recognised faces data loaded from external storage", Toast.LENGTH_SHORT).show();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Error loading recognised faces data: " + e.getMessage());
        }
    }


    public Pair<String, Float> detectInImage(Bitmap faceBitmap) {
        Pair<String, Float> result = null;
        if (faceBitmap == null) {
            Log.d("GraphicOverlay", "Face bitmap null");
            return null;
        }

        TensorImage tensorImage = TensorImage.fromBitmap(faceBitmap);
        ByteBuffer faceNetByteBuffer = faceNetImageProcessor.process(tensorImage).getBuffer();
        float[][] faceOutputArray = new float[1][192];
        faceNetModelInterpreter.run(faceNetByteBuffer, faceOutputArray);
        //Toast.makeText(context, "FaceOutputDone", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "output array: " + Arrays.deepToString(faceOutputArray));


        if (!recognisedFaceList.isEmpty()) {
           result = findNearestFace(faceOutputArray[0]);
            // if distance is within confidence
            if (result.second < 1.0f) {
                //faceGraphic.name = result.first;
                //Toast.makeText(context, result.first +"("+ result.second +")" , Toast.LENGTH_SHORT).show();
                //callback.onFaceRecognised(face, result.second, result.first);
            }
        }
        return result;
    }

    public float[] vectorFromImage(Bitmap faceBitmap) {
        // now we have a face, so we can use that to analyse age and gender
        if (faceBitmap == null) {
            Log.d("GraphicOverlay", "Face bitmap null");
            return null;
        }



        TensorImage tensorImage = TensorImage.fromBitmap(faceBitmap);
        ByteBuffer faceNetByteBuffer = faceNetImageProcessor.process(tensorImage).getBuffer();
        float[][] faceOutputArray = new float[1][192];
        faceNetModelInterpreter.run(faceNetByteBuffer, faceOutputArray);
        Toast.makeText(context,"Face Registered temp",Toast.LENGTH_SHORT).show();

        Log.d(TAG, "output array1: " + Arrays.deepToString(faceOutputArray));

        return faceOutputArray[0];

    }

    public void vectorFromMultipleImages(List<Bitmap> faceBitmaps, String name) {
        if (faceBitmaps == null || faceBitmaps.isEmpty()) {
            Log.d("FaceProcessor", "Face bitmap list is null or empty");
            return;
        }
        float[] totalVector = new float[192]; // Assuming the vector size is 192

        // Initialize the totalVector to zeros
        Arrays.fill(totalVector, 0f);

        for (Bitmap faceBitmap : faceBitmaps) {
            TensorImage tensorImage = TensorImage.fromBitmap(faceBitmap);
            ByteBuffer faceNetByteBuffer = faceNetImageProcessor.process(tensorImage).getBuffer();
            float[][] faceOutputArray = new float[1][192]; // Assuming output size is [1][192]
            faceNetModelInterpreter.run(faceNetByteBuffer, faceOutputArray);
            Log.d(TAG, "output array: " + Arrays.deepToString(faceOutputArray));

            // Accumulate the vectors
            for (int i = 0; i < totalVector.length; i++) {
                totalVector[i] += faceOutputArray[0][i];
            }
        }

        // Normalize the totalVector by dividing each element by the number of input images
        int numImages = faceBitmaps.size();
        for (int i = 0; i < totalVector.length; i++) {
            totalVector[i] /= numImages;
        }

        //Log.d(TAG, "Total Vector: " + Arrays.toString(totalVector));
        Log.d(TAG, name + " vectors saved");
        Toast.makeText(context, name + " vectors saved", Toast.LENGTH_SHORT).show();
        recognisedFaceList.add(new Person(name, totalVector));
        saveRecognisedFacesToExternalStorage(context);
    }



    // looks for the nearest vector in the dataset (using L2 norm)
    // and returns the pair <name, distance>
    private Pair<String, Float> findNearestFace(float[] vector) {

        Pair<String, Float> ret = null;
        for (Person person : recognisedFaceList) {
            final String name = person.name;
            final float[] knownVector = person.faceVector;

            float distance = 0;
            for (int i = 0; i < vector.length; i++) {
                float diff = vector[i] - knownVector[i];
                distance += diff*diff;
            }
            distance = (float) Math.sqrt(distance);
            if (ret == null || distance < ret.second) {
                ret = new Pair<>(name, distance);
            }
        }

        return ret;

    }

    public void registerFace(String input, float[] Vector) {
        recognisedFaceList.add(new Person(input.toString(), Vector));
        saveRecognisedFacesToExternalStorage(context); // Save the updated list to external storage
    }



}
