package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateProfileActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private Bitmap imageBitmap = null;
    private Constants constants = new Constants();
    private ImageView imageView = null;
    private  UserSingleton userSingleton = UserSingleton.getInstance();
    private String currentPhotoPath = null;
    private EditText userIdView = null;
    private UserInfo newUserInfo = new UserInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(userSingleton.getCurrentUserTheme().equals(constants.LIGHT_THEME)){
            setTheme(R.style.Theme_projet);
        }else{
            setTheme(R.style.Theme_projet_dark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        userIdView = findViewById(R.id.user_id_view);
        Button create_profile_button = (Button) findViewById(R.id.create_profile_button);
        Button add_photo_button = (Button) findViewById(R.id.add_photo_button);
        imageView = findViewById(R.id.imageView);

        create_profile_button.setOnClickListener(v -> {
            String newUserId = userIdView.getText().toString();
            if(newUserId.trim().length() == 0){
                Toast.makeText(this,"Please enter a valid username", Toast.LENGTH_LONG).show();
            }else {

                newUserInfo.setUserId(userIdView.getText().toString());
                userSingleton.setCurrentUser(newUserInfo);

                //Add user infos to database
                userSingleton.updateUserInfo();

                saveImage();

                //Fetch detected devices
                userSingleton.fetchCurrentUserDevices();
                Intent welcomeAct = new Intent(CreateProfileActivity.this, MainActivity.class);
                startActivity(welcomeAct);
                finish();
                }
        });

        add_photo_button.setOnClickListener(v -> {
            dispatchTakePictureIntent();
        });

        userIdView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length() == 0){
                    create_profile_button.setEnabled(false);
                } else {
                    create_profile_button.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //Souce: https://developer.android.com/training/camera/photobasics#java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }else{
            Toast.makeText(this, "Picture taking failed", Toast.LENGTH_SHORT).show();
        }
    }

    //Souce: https://developer.android.com/training/camera/photobasics#java
    private void saveImage(){
        System.out.println("HERE"+ userSingleton.getCurrentUserId());
        StorageReference userRef = storageReference.child(userSingleton.getCurrentUserId());
       if(imageBitmap != null) {
           ByteArrayOutputStream baos = new ByteArrayOutputStream();
           imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
           byte[] data = baos.toByteArray();

           UploadTask uploadTask = userRef.putBytes(data);

           uploadTask.addOnSuccessListener(taskSnapshot -> {
               newUserInfo.setHasProfilePhoto(true);
           });

           uploadTask.addOnFailureListener(exception -> {
               System.out.println("Profile adding failed");
           });

       }
    }

    //Souce: https://developer.android.com/training/camera/photobasics#java
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Souce: https://developer.android.com/training/camera/photobasics#java
    private void dispatchTakePictureIntent() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Create the File where the photo should go
            File photoFile = null;
            System.out.println("HERRRRE");
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println(ex.getMessage());
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.projet.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }catch (Exception error){
            System.out.println(error.getMessage());
        }
    }

    //Souce: https://developer.android.com/training/camera/photobasics#java
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        imageBitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(imageBitmap);
    }
}

