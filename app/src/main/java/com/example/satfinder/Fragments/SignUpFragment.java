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

public class SignUpFragment extends Fragment {

    private static final String TAG = "SatSignUpF";

    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        setupUI(view);
        return view;
    }

    private void setupUI(View view) {
        Log.d(TAG, "Setting up UI components...");

        etName = view.findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);

        Button buttonSignUp = view.findViewById(R.id.button_signup);
        buttonSignUp.setOnClickListener(v -> handleSignUp());
    }

    private void handleSignUp() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Log.w(TAG, "Received some empty fields!");
            Toast.makeText(SignUpFragment.this.getContext(), "All input fields must be filled!", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "Signing up " + name + " with email: " + email);
        ((LoginActivity) requireActivity()).signUpUser(name, email, password, confirmPassword);
    }
}
