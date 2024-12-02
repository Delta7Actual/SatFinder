package com.example.satfinder.Activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.satfinder.Fragments.AccountFragment;
import com.example.satfinder.Managers.UserManager;
import com.example.satfinder.Objects.Interfaces.UserAuthCallback;
import com.example.satfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    TextView tvGreeting;

    private void setupUI() {
        tvGreeting = findViewById(R.id.tv_greeting);
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvGreeting.setText("Hey, " + user.getDisplayName() + "!");
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new AccountFragment())
                .commit();
    }

    public void setUserDisplayName(String newName) {
        UserManager manager = UserManager.getInstance();
        manager.setUserDisplayName(newName, new UserAuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                tvGreeting.setText("Hey, " + user.getDisplayName() + "!");
                Toast.makeText(ProfileActivity.this, "Display name updated to " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
