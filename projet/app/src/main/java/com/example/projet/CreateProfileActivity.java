package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

public class CreateProfileActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        Button create_profil_button = (Button) findViewById(R.id.create_profile_button);
        create_profil_button.setOnClickListener(v -> {
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
            ImageView imageView = findViewById(R.id.imageView);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }
}