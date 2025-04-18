package com.example.satfinder.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.satfinder.Fragments.LoginFragment;
import com.example.satfinder.Fragments.SignUpFragment;
import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.Managers.UserManager;
import com.example.satfinder.Objects.Interfaces.IStorageCallback;
import com.example.satfinder.Objects.Interfaces.IUserAuthCallback;
import com.example.satfinder.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "SatLogin";

    private Button btnSwitch;
    private TextView tvLoginSignup;
    private boolean isLogin = true;

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
        
        handleInterruptedSession();
        setupUI();
        toggleFragment();
    }

    private void setupUI() {
        Log.d(TAG, "Setting up UI components...");
        
        btnSwitch = findViewById(R.id.btn_switch);
        tvLoginSignup = findViewById(R.id.tv_login_signup);
        btnSwitch.setOnClickListener(v -> handleSwitchClick());
    }

    // User might still be logged in from a previous session
    private void handleInterruptedSession() {
        Log.d(TAG, "Checking if user has an open session already...");
        
        if (UserManager.getInstance().isUserLoggedIn()) {
            Log.d(TAG, "User already logged in, continuing previous session.");
            Toast.makeText(this, "Continuing previous session", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
    
    private void handleSwitchClick() {
            Log.d(TAG, "Switch button clicked, toggling fragment.");
            toggleFragment();
    }

    private void toggleFragment() {
        Log.d(TAG, "Toggling fragment. Current mode: " + (isLogin ? "Login" : "Sign Up"));
        if (isLogin) {
            replaceFragment(new SignUpFragment());
            btnSwitch.setText(R.string.switch_to_login);
            tvLoginSignup.setText(R.string.sign_up);
        } else {
            replaceFragment(new LoginFragment());
            btnSwitch.setText(R.string.switch_to_sign_up);
            tvLoginSignup.setText(R.string.login);
        }
        this.isLogin = !isLogin;
    }

    private void replaceFragment(Fragment fragment) {
        Log.d(TAG, "Replacing fragment with " + fragment.getClass().getSimpleName());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Add the transaction to the backstack
                .commit();
    }

    public void signUpUser(String name, String email, String password, String confirmPassword) {
        Log.d(TAG, "Attempting to sign up user with email: " + email + "/ password: " + password);

        if (isPasswordinValid(password)) return;

        if (!password.equals(confirmPassword)) {
            Log.d(TAG, "Passwords do not match.");
            Toast.makeText(LoginActivity.this, "Passwords do not match!", Toast.LENGTH_LONG).show();
            return;
        }

        UserManager.getInstance().signUpUser(name, email, password, new IUserAuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Log.d(TAG, "User created successfully: " + user.getEmail());
                Toast.makeText(LoginActivity.this, "User created successfully!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Sign up failed: " + error);
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loginUser(String email, String password) {
        Log.d(TAG, "Attempting to log in user with email: " + email + "/ password: " + password);

        if (isPasswordinValid(password)) return;

        UserManager.getInstance().loginUser(email, password, new IUserAuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Log.d(TAG, "Login successful for user: " + user.getDisplayName());
                Toast.makeText(LoginActivity.this, "Logged in successfully! Hello " + user.getDisplayName(), Toast.LENGTH_LONG).show();

                // Fetch and save favourite satellite IDs locally
                StorageManager.getInstance(LoginActivity.this).getFavouriteSatelliteIds(new IStorageCallback<List<String>>() {
                    @Override
                    public void onSuccess(List<String> result) {
                        if (result == null) {
                            Log.e(TAG, "Fetched NULL IDs!");
                        } else {
                            Log.d(TAG, "Fetched IDs: " + result.size() + " items");
                            StorageManager.getInstance(LoginActivity.this).spSaveUserFavoriteSatellites(result);
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e(TAG, "Failed to retrieve IDs from Firestore");
                        Toast.makeText(LoginActivity.this, "Couldn't fetch favourite satellites", Toast.LENGTH_SHORT).show();
                    }
                });
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Login failed: " + error);
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void recoverPassword(String email) {
        Log.d(TAG, "Attempting to recover password for email: " + email);

        UserManager.getInstance().recoverPassword(email, new IUserAuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Log.d(TAG, "Password reset email sent successfully.");
                Toast.makeText(LoginActivity.this, "Password reset email sent!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Password recovery failed: " + error);
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isPasswordinValid(String password) {
        Log.d(TAG, "Checking password: " + password + "...");
        if (password.length() < 6) {
            Log.e(TAG, "Password too short! Length: " + password.length());
            Toast.makeText(LoginActivity.this, "Password should be at least 6 digits!", Toast.LENGTH_LONG).show();
            return true;
        }

        boolean hasUppercase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUppercase = true;
            if (Character.isDigit(c)) hasDigit = true;
        }

        if (!hasUppercase) {
            Log.e(TAG, "Password does not contain uppercase: " + password);
            Toast.makeText(LoginActivity.this, "Password should contain at least one uppercase letter!", Toast.LENGTH_LONG).show();
            return true;
        }

        if (!hasDigit) {
            Log.e(TAG, "Password does not contain digit: " + password);
            Toast.makeText(LoginActivity.this, "Password should contain at least one digit!", Toast.LENGTH_LONG).show();
            return true;
        }

        return false;
    }
}