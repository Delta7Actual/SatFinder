package com.example.satfinder.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.satfinder.Activities.LoginActivity;
import com.example.satfinder.R;

public class LoginFragment extends Fragment {

    private static final String TAG = "SatLoginF";

    private EditText etEmail, etPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        setupUI(view);
        return view;
    }

    private void setupUI(View view) {
        Log.d(TAG, "Setting up UI components...");

        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);

        Button btnLogin = view.findViewById(R.id.button_login);
        Button btnForgotPassword = view.findViewById(R.id.btn_forgot_password);

        btnLogin.setOnClickListener(v -> handleLogin());
        btnForgotPassword.setOnClickListener(v -> handlePasswordReset());
    }

    private void handlePasswordReset() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Log.w(TAG, "handlePasswordReset: Email field is empty");
            Toast.makeText(getContext(), "Fill in recovery email address!", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "Requesting password reset for " + email);
        ((LoginActivity) requireActivity()).recoverPassword(email);
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Log.w(TAG, "handleLogin: One or more input fields are empty");
            Toast.makeText(getContext(), "All input fields must be filled!", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "Attempting login with email: " + email);
        ((LoginActivity) requireActivity()).loginUser(email, password);
    }
}