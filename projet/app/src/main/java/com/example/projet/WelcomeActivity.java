package com.example.projet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.internal.Sleeper;
import java.util.ArrayList;
import com.example.projet.databinding.ActivityWelcomeBinding;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class WelcomeActivity extends AppCompatActivity {
    private Constants constants = new Constants();
    private UserSingleton userSingleton = UserSingleton.getInstance();

    private String language;
    private ActivityWelcomeBinding binding;

    public static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        language = App.localeManager.getAppLanguage();
        if(userSingleton.getAllUserInfos().size() == 0){
            binding.warning.setText(R.string.no_profile_found);
        }
        binding.createButton.setOnClickListener(v -> {
            Intent welcomeAct = new Intent(WelcomeActivity.this, CreateProfileActivity.class);
            startActivity(welcomeAct);
            finish();
        });

        loadGrid();

        binding.changeLanguageButton.setOnClickListener(v -> {
            showChangeLanguageDialog();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(userSingleton.getAllUserInfos().size() == 0) {
            if (!language.equals(App.localeManager.getAppLanguage())) {
                Context context = App.localeManager.setAppLocale(this);
                Resources resources = context.getResources();

                binding.warning.setText(resources.getString(R.string.no_profile_found));

                language = App.localeManager.getLanguage();
            }
            Log.d(TAG, "onResume");
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    /** Load grid and display all profiles in the database
     * @param -
     * @return -
     */
    void loadGrid(){
        UserAdapter adapter = new UserAdapter(WelcomeActivity.this, userSingleton.getAllUserInfos());
        binding.grid.setAdapter(adapter);
    }

    /** Open specific user profile
     * @param position - the user position in users array(userSingleton)
     * @return
     */

    public void openAccount(int position) throws InterruptedException {
        userSingleton.setCurrentUser(userSingleton.getAllUserInfos().get(position));
        userSingleton.fetchCurrentUserDevices();
        TimeUnit.MILLISECONDS.sleep(500);
        Intent mainAct = new Intent(this, MainActivity.class);
        startActivity(mainAct);
        finish();
    }

    /** Show language options to user
     * https://www.youtube.com/watch?v=zILw5eV9QBQ
     * @param -
     * @return -
     */
    private void showChangeLanguageDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(WelcomeActivity.this);
        mBuilder.setTitle(getResources().getString(R.string.choose_language));
        mBuilder.setSingleChoiceItems(constants.LANGUAGES, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // French
                        App.localeManager.setNewAppLocale(WelcomeActivity.this, App.localeManager.LANGUAGE_FRENCH);
                        break;
                    case 1:
                        // English
                        App.localeManager.setNewAppLocale(WelcomeActivity.this, App.localeManager.LANGUAGE_ENGLISH);
                        break;
                    default:
                        Log.d("PROFILE", "No language chosen");
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

}