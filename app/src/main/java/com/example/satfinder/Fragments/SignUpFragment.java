package com.example.satfinder.Fragments;

import android.os.Bundle;
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

    public SignUpFragment() {
        // Required empty public constructor
    }

    private EditText etName, etEmail, etPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        etName = view.findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);

        Button buttonSignUp = view.findViewById(R.id.button_signup);
        buttonSignUp.setOnClickListener(v -> handleSignUp());

        return view;
    }

    private void handleSignUp() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignUpFragment.this.getContext(), "All input fields must be filled!", Toast.LENGTH_LONG).show();
            return;
        }
        // Call LoginActivity method -> Preserve separation of concerns
        ((LoginActivity) requireActivity()).signUpUser(name, email, password);
    }
}
