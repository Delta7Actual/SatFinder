package com.example.satfinder.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.satfinder.Fragments.AccountFragment;
import com.example.satfinder.Managers.UserManager;
import com.example.satfinder.Objects.Interfaces.IUserAuthCallback;
import com.example.satfinder.R;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "SatProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();
    }

    private void setupUI() {
        Log.d(TAG, "Setting up UI: Initializing AccountFragment in fragment container...");

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new AccountFragment())
                .commit();
        Log.d(TAG, "AccountFragment successfully initialized.");
    }

    public String getUserDisplayName() {
        Log.d(TAG, "Fetching current user display name...");

        UserManager manager = UserManager.getInstance();
        String displayName = manager.getCurrentUserDisplayName();
        Log.d(TAG, "Current user display name: " + displayName);
        return displayName;
    }

    public String getUserEmail() {
        Log.d(TAG, "Fetching current user email...");

        UserManager manager = UserManager.getInstance();
        String userEmail = manager.getCurrentUserEmail();
        Log.d(TAG, "Current user email: " + userEmail);
        return userEmail;
    }

    public void setUserDisplayName(String newName) {
        Log.d(TAG, "Attempting to update user display name to: " + newName);

        UserManager manager = UserManager.getInstance();
        manager.setUserDisplayName(newName, new IUserAuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Log.d(TAG, "Display name successfully updated: " + user.getDisplayName());
                Toast.makeText(ProfileActivity.this, "Display name updated to " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Log.w(TAG, "Failed to update display name: " + error);
                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}