package com.example.satfinder.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.example.satfinder.Managers.UserManager;
import com.example.satfinder.Objects.Interfaces.IUserAuthCallback;
import com.example.satfinder.R;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button btnSwitch;
    private TextView tvLoginSignup;
    private boolean isLogin = true;

    private void setupUI()
    {
        btnSwitch = findViewById(R.id.btn_switch);
        tvLoginSignup = findViewById(R.id.tv_login_signup);
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

        // Check if user is logged in before toggling fragments
        if (UserManager.getInstance().isUserLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return; // Don't show the login fragment if user is already logged in
        }

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
                .addToBackStack(null) // Add the transaction to the backstack
                .commit();
    }

    public void signUpUser(String name, String email, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            Toast.makeText(LoginActivity.this, "Passwords do not match!", Toast.LENGTH_LONG).show();
            return;
        }
        UserManager manager = UserManager.getInstance();
        manager.signUpUser(name, email, password, new IUserAuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Toast.makeText(LoginActivity.this, "User created successfully!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loginUser(String email, String password) {

        UserManager manager = UserManager.getInstance();
        manager.loginUser(email, password, new IUserAuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Toast.makeText(LoginActivity.this, "Logged in successfully! Hello " + user.getDisplayName(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void recoverPassword(String email) {
        UserManager manager = UserManager.getInstance();
        manager.recoverPassword(email, new IUserAuthCallback() {

            @Override
            public void onSuccess(FirebaseUser user) {
                Toast.makeText(LoginActivity.this, "Password reset email sent!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}