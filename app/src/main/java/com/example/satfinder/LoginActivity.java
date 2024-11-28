package com.example.satfinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginActivity extends AppCompatActivity {

    private FrameLayout flFragmentContainer;
    private Button btnSwitch;
    private TextView tvLoginSignup;
    private boolean isLogin = true;
    private FirebaseAuth mAuth;

    private void setupUI()
    {
        flFragmentContainer = findViewById(R.id.fragment_container);
        btnSwitch = findViewById(R.id.btn_switch);
        tvLoginSignup = findViewById(R.id.tv_login_signup);
        mAuth = FirebaseAuth.getInstance();

        btnSwitch.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        if (view == btnSwitch) {
            toggleFragment();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();
        toggleFragment();
    }

    private void toggleFragment() {
        if (isLogin) {
            replaceFragment(new SignUpFragment());
            btnSwitch.setText("Switch to Login");
            tvLoginSignup.setText("SIGN UP");
        } else {
            replaceFragment(new LoginFragment());
            btnSwitch.setText("Switch to Sign Up");
            tvLoginSignup.setText("LOGIN");
        }
        this.isLogin = !isLogin;
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    protected void signUpUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser userCreated = mAuth.getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                        userCreated.updateProfile(profileUpdates).addOnCompleteListener(profileUpdateTask -> {
                            if (profileUpdateTask.isSuccessful()) {
                                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                                // TODO implement local storage save of information in SQLite?
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Failed to update profile name!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        Toast.makeText(this, "Something went wrong with account creation!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    protected void loginUser(String email, String password) {

    }
}