package com.example.projet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<UserInfo> {
        private  UserSingleton userSingleton = UserSingleton.getInstance();
        // constructor for our list view adapter.

        private static ImageView profilePictureView = null;
        public UserAdapter(@NonNull Context context, ArrayList<UserInfo> dataModalArrayList) {
            super(context, 0, dataModalArrayList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.user_item, parent, false);
            }

            UserInfo currentUser = getItem(position);

            // initializing our UI components of list view item.
            TextView usernameView = listItemView.findViewById(R.id.username);
            profilePictureView = listItemView.findViewById(R.id.photo);

            usernameView.setText(currentUser.getUserId());
            if(currentUser.hasProfilePicture()){
                loadProfilePicture(currentUser.getUserId());
            }

            // click listener for our item of list view.
            listItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getContext() instanceof WelcomeActivity) {
                        try {
                            ((WelcomeActivity)v.getContext()).openAccount(position);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
            return listItemView;
        }


    private void loadProfilePicture(String userId){
        try {
            // Reference to an image file in Cloud Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(userId);

            // Download directly from StorageReference using Glide
            // (See MyAppGlideModule for Loader registration)
            Glide.with(getContext())
                    .load(storageReference)
                    .into(profilePictureView);
        }
        catch (Exception error){
            System.out.println(error.getMessage());
        }
    }
}
