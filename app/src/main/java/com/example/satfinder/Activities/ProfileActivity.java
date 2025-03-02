package com.example.satfinder.Activities;

import android.os.Bundle;
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

    private void setupUI() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new AccountFragment())
                .commit();
    }

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

    public String getUserDisplayName() {
        UserManager manager = UserManager.getInstance();
        return manager.getCurrentUserDisplayName();
    }

    public String getUserEmail() {
        UserManager manager = UserManager.getInstance();
        return manager.getCurrentUserEmail();
    }

    public void setUserDisplayName(String newName) {
        UserManager manager = UserManager.getInstance();
        manager.setUserDisplayName(newName, new IUserAuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Toast.makeText(ProfileActivity.this, "Display name updated to " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
