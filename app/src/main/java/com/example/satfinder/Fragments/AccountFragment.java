package com.example.satfinder.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.satfinder.Activities.ProfileActivity;
import com.example.satfinder.Adapters.SavedSatelliteAdapter;
import com.example.satfinder.Managers.SatelliteManager;
import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.Objects.Interfaces.IN2YOCallback;
import com.example.satfinder.Objects.Interfaces.ISatelliteResponse;
import com.example.satfinder.Objects.Interfaces.IStorageCallback;
import com.example.satfinder.Objects.SatelliteTLEResponse;
import com.example.satfinder.R;

import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends Fragment {

    private EditText etDisplayName, etSatelliteID;
    private Button btnSaveDisplayName, btnAddSatellite;
    private RecyclerView recyclerSatelliteList;
    private SavedSatelliteAdapter adapter;
    private List<String> satelliteIds;
    private TextView tvDisplayName, tvEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize views
        tvDisplayName = view.findViewById(R.id.tv_display_name);
        tvEmail = view.findViewById(R.id.tv_email);
        etDisplayName = view.findViewById(R.id.et_display_name);
        etSatelliteID = view.findViewById(R.id.et_satellite_id);
        btnSaveDisplayName = view.findViewById(R.id.btn_save_display_name);
        btnAddSatellite = view.findViewById(R.id.btn_add_satellite);
        recyclerSatelliteList = view.findViewById(R.id.recycler_satellite_list);

        // Set up RecyclerView
        satelliteIds = new ArrayList<>();
        adapter = new SavedSatelliteAdapter(satelliteIds);
        recyclerSatelliteList.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerSatelliteList.setAdapter(adapter);

        // Set up button listeners
        btnSaveDisplayName.setOnClickListener(v -> saveDisplayName());
        btnAddSatellite.setOnClickListener(v -> addSatellite());

        // Load the initial list of satellite IDs
        updateSatelliteList();
        // Load / Reload the user's details
        reloadUserDetails();

        return view;
    }

    private void reloadUserDetails() {
        String displayName = ((ProfileActivity) requireActivity()).getUserDisplayName();
        String email = ((ProfileActivity) requireActivity()).getUserEmail();

        if (displayName != null) {
            tvDisplayName.setText(displayName);
        } else {
            tvDisplayName.setText("Error getting display name");
        }

        if (email != null) {
            tvEmail.setText(email);
        } else {
            tvEmail.setText("Error getting email");
        }
    }

    private void saveDisplayName() {
        String newName = etDisplayName.getText().toString().trim();
        if (!newName.isEmpty()) {
            ((ProfileActivity) requireActivity()).setUserDisplayName(newName);
            Toast.makeText(requireContext(), "Updating Display name: " + newName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Please enter a valid display name.", Toast.LENGTH_SHORT).show();
        }

        reloadUserDetails();
    }

    private void addSatellite() {
        String satelliteId = etSatelliteID.getText().toString().trim();
        if (!satelliteId.isEmpty()) {
            isSatelliteIdValid(satelliteId, new IStorageCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    if (result) {
                        StorageManager.getInstance(getContext()).addFavouriteSatelliteId(Integer.parseInt(satelliteId), new IStorageCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                satelliteIds.add(satelliteId);
                                adapter.notifyItemInserted(satelliteIds.size() - 1);
                                Toast.makeText(requireContext(), "Satellite added!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(requireContext(), "Failed to add satellite: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(requireContext(), "Invalid satellite ID or no TLE data found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(requireContext(), "Error validating satellite: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "Please enter a valid satellite ID.", Toast.LENGTH_SHORT).show();
        }
    }

    private void isSatelliteIdValid(String satelliteId, IStorageCallback<Boolean> callback) {
        SatelliteManager.getInstance().fetchSatelliteTLE(Integer.parseInt(satelliteId), new IN2YOCallback() {
            @Override
            public void onSuccess(ISatelliteResponse response) {
                SatelliteTLEResponse tleResponse = (SatelliteTLEResponse) response;
                callback.onSuccess(tleResponse.getTle() != null && !tleResponse.getTle().isEmpty());
            }

            @Override
            public void onError(String errorMessage) {
                callback.onFailure("Invalid satellite ID or no TLE found.");
            }
        });
    }


    private void updateSatelliteList() {
        StorageManager.getInstance(this.getContext()).getFavouriteSatelliteIds(new IStorageCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                satelliteIds.clear();
                satelliteIds.addAll(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
