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

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    private EditText etEmail, etPassword;
    private Button btnLogin, btnForgotPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);

        btnLogin = view.findViewById(R.id.button_login);
        btnForgotPassword = view.findViewById(R.id.btn_forgot_password);

        btnLogin.setOnClickListener(v -> handleLogin());
        btnForgotPassword.setOnClickListener(v -> handlePasswordReset());

        return view;
    }

    private void handlePasswordReset() {
        String email = String.valueOf(etEmail.getText());

        if (email.isEmpty())
        {
            Toast.makeText(LoginFragment.this.getContext(), "Fill in recovery email address!", Toast.LENGTH_LONG).show();
            return;
        }

        // Call LoginActivity method -> Preserve separation of concerns
        ((LoginActivity) requireActivity()).recoverPassword(email);
    }

    private void handleLogin()
    {
        String email = String.valueOf(etEmail.getText());
        String password = String.valueOf(etPassword.getText());

        if (email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(LoginFragment.this.getContext(), "All input fields must be filled!", Toast.LENGTH_LONG).show();
            return;
        }
        // Call LoginActivity method -> Preserve separation of concerns
        ((LoginActivity) requireActivity()).loginUser(email, password);

    }
}