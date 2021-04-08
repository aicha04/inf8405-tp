package com.example.projet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class CreateProfileActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private  Intent intentData = null;
    private Bitmap imageBitmap = null;
    private Constants constants = new Constants();
    private  UserSingleton userSingleton = UserSingleton.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(userSingleton.getCurrentTheme().equals(constants.LIGHT_THEME)){
            setTheme(R.style.Theme_projet);
        }else{
            setTheme(R.style.Theme_projet_dark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        Button create_profile_button = (Button) findViewById(R.id.create_profile_button);
        create_profile_button.setOnClickListener(v -> {
            saveImage();
            Intent welcomeAct = new Intent(CreateProfileActivity.this, MainActivity.class);
            startActivity(welcomeAct);
            finish();
        });

        Button add_photo_button = (Button) findViewById(R.id.add_photo_button);
        add_photo_button.setOnClickListener(v -> {
            addPhoto();
        });
    }

    //Souce: https://developer.android.com/training/camera/photobasics#java
    private void addPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    //Souce: https://developer.android.com/training/camera/photobasics#java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            intentData = data;
            displayImage();
        }else{
            Toast.makeText(this, "Picture taking failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void displayImage(){
        if(intentData != null) {
            ImageView imageView = findViewById(R.id.imageView);
             imageBitmap = (Bitmap) intentData.getExtras().get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

   private void saveImage(){
        StorageReference userRef = storageReference.child(userSingleton.getUserId());
       if(imageBitmap != null) {
           ByteArrayOutputStream baos = new ByteArrayOutputStream();
           imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
           byte[] data = baos.toByteArray();

           UploadTask uploadTask = userRef.putBytes(data);

           uploadTask.addOnFailureListener(exception -> {
               System.out.println("Profile adding failed");
           });

           uploadTask.addOnSuccessListener(taskSnapshot -> {
               System.out.println("Profile successfully added");
               userSingleton.setHasProfilePhoto(true);
           });
       }
    }
}