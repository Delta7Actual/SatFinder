package com.example.satfinder.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.satfinder.Fragments.LoginFragment;
import com.example.satfinder.Fragments.SignUpFragment;
import com.example.satfinder.Managers.UserManager;
import com.example.satfinder.Objects.Interfaces.UserAuthCallback;
import com.example.satfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FrameLayout flFragmentContainer;
    private Button btnSwitch;
    private TextView tvLoginSignup;
    private Toolbar toolbar;
    private boolean isLogin = true;

    private void setupUI()
    {
        flFragmentContainer = findViewById(R.id.fragment_container);
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
        if (!UserManager.getInstance().isLoggedIn()) {
            FirebaseAuth.getInstance().signOut();
        }

        setupUI();
        toggleFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_more) {
            Toast.makeText(this, "More clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.action_options) {
            Toast.makeText(this, "Options clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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

    public void signUpUser(String name, String email, String password) {
        UserManager manager = UserManager.getInstance();
        manager.signUpUser(name, email, password, new UserAuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Toast.makeText(LoginActivity.this, "User created successfully!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loginUser(String email, String password) {
        UserManager manager = UserManager.getInstance();
        manager.loginUser(email, password, new UserAuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Toast.makeText(LoginActivity.this, "Logged in successfully! Hello" + user.getDisplayName(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}