package com.example.projet;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.projet.databinding.ActivityCreateProfileBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateProfileActivity extends AppCompatActivity {
    private static final String TAG = "CreateProfileActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private Bitmap imageBitmap = null;
    private Constants constants = new Constants();
    private  UserSingleton userSingleton = UserSingleton.getInstance();
    private String currentPhotoPath = null;
    private UserInfo newUserInfo = new UserInfo();

    private ActivityCreateProfileBinding binding;
    private String language;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(App.localeManager.setAppLocale(base));
        Log.d(TAG, "attachBaseContext");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(userSingleton.getCurrentUserTheme().equals(constants.LIGHT_THEME)){
            setTheme(R.style.Theme_projet);
        }else{
            setTheme(R.style.Theme_projet_dark);
        }
        super.onCreate(savedInstanceState);
        binding = ActivityCreateProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        language = App.localeManager.getAppLanguage();

        binding.createProfileButton.setOnClickListener(v -> {
            String newUserId = binding.userIdView.getText().toString().trim();
            if(newUserId.trim().length() == 0){
                showToast(getApplicationContext(), R.string.please_enter_valid_username);
            }else if(userSingleton.userExists(newUserId)) {
                showToast(getApplicationContext(), R.string.username_already_exists);
            }else{
                newUserInfo.setUserId(binding.userIdView.getText().toString());
                userSingleton.setCurrentUser(newUserInfo);

                saveImage();

                //Add user infos to database
                userSingleton.addNewUser(newUserInfo);

                //Fetch detected devices

                userSingleton.fetchCurrentUserDevices();
                Intent welcomeAct = new Intent(CreateProfileActivity.this, MainActivity.class);
                startActivity(welcomeAct);
                finish();
            }
        });

        binding.addPhotoButton.setOnClickListener(v -> {
            dispatchTakePictureIntent();
        });

        binding.cancelButton.setOnClickListener(v -> {
            Intent welcomeAct = new Intent(CreateProfileActivity.this, WelcomeActivity.class);
            startActivity(welcomeAct);
            finish();
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!language.equals(App.localeManager.getAppLanguage())) {
            Context context = App.localeManager.setAppLocale(this);
            Resources resources = context.getResources();

            binding.title.setText(resources.getString(R.string.profile_creation));
            binding.textView2.setText(resources.getString(R.string.pick_a_username));
            binding.textView3.setText(resources.getString(R.string.add_a_profile_picture));
            binding.createProfileButton.setText(resources.getString(R.string.create_profile));
            binding.cancelButton.setText(R.string.cancel);

            language = App.localeManager.getAppLanguage();
        }
        Log.d(TAG, "onResume");
    }

    //Souce: https://developer.android.com/training/camera/photobasics#java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            newUserInfo.setHasProfilePhoto(true);
            setPic();
        }else{
            Toast.makeText(this, "Picture taking failed", Toast.LENGTH_SHORT).show();
        }
    }

    //Souce: https://developer.android.com/training/camera/photobasics#java
    private void saveImage(){

        StorageReference userRef = storageReference.child(userSingleton.getCurrentUserId());
       if(imageBitmap != null) {
           ByteArrayOutputStream baos = new ByteArrayOutputStream();
           imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
           byte[] data = baos.toByteArray();

           UploadTask uploadTask = userRef.putBytes(data);

           uploadTask.addOnSuccessListener(taskSnapshot -> {
               System.out.println("Profile added successfully");
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
        int targetW = binding.imageView.getWidth();
        int targetH = binding.imageView.getHeight();

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
        binding.imageView.setImageBitmap(imageBitmap);
    }

    protected void showToast(Context c, @StringRes int id) {
        Resources resources = App.localeManager.setAppLocale(c).getResources();
        Toast.makeText(c, resources.getString(id), Toast.LENGTH_LONG).show();
    }
}

