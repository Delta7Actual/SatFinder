package com.example.satfinder.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.satfinder.Activities.ProfileActivity;
import com.example.satfinder.R;

public class AccountFragment extends Fragment {

    public AccountFragment() {
        // Required empty public constructor
    }

    EditText etDisplayName, etSatelliteID;
    Button btnSaveDisplayName, btnAddSatellite;
    TextView tvSatelliteList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        
        etDisplayName = view.findViewById(R.id.et_display_name);
        etSatelliteID = view.findViewById(R.id.et_satellite_id);
        btnSaveDisplayName = view.findViewById(R.id.btn_save_display_name);
        btnAddSatellite = view.findViewById(R.id.btn_add_satellite);
        tvSatelliteList = view.findViewById(R.id.tv_satellite_list);
        
        btnSaveDisplayName.setOnClickListener(this::handleClick);
        btnAddSatellite.setOnClickListener(this::handleClick);
        
        return view;
    }
    
    private void handleClick(View view) {
        if (view == btnSaveDisplayName) {
            String newName = String.valueOf(etDisplayName.getText());
            ((ProfileActivity) requireActivity()).setUserDisplayName(newName);
        } else if (view == btnAddSatellite) {
            Toast.makeText(AccountFragment.this.getContext(), "Satellite added!", Toast.LENGTH_LONG).show();
        }
    }
}