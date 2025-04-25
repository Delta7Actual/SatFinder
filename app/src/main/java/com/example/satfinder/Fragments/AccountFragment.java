package com.example.satfinder.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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

    private static final String TAG = "SatAccountF";

    private EditText etDisplayName, etSatelliteID;
    private SavedSatelliteAdapter adapter;
    private List<String> satelliteIds;
    private TextView tvDisplayName, tvEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        setupUI(view);
        updateSatelliteList();
        reloadUserDetails();

        return view;
    }

    private void setupUI(View view) {
        Log.d(TAG, "Setting up UI components...");

        tvDisplayName = view.findViewById(R.id.tv_display_name);
        tvEmail = view.findViewById(R.id.tv_email);
        etDisplayName = view.findViewById(R.id.et_display_name);
        etSatelliteID = view.findViewById(R.id.et_satellite_id);

        Button btnSaveDisplayName = view.findViewById(R.id.btn_save_display_name);
        Button btnAddSatellite = view.findViewById(R.id.btn_add_satellite);
        RecyclerView recyclerSatelliteList = view.findViewById(R.id.recycler_satellite_list);

        satelliteIds = new ArrayList<>();
        adapter = new SavedSatelliteAdapter(satelliteIds);
        recyclerSatelliteList.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerSatelliteList.setAdapter(adapter);

        btnSaveDisplayName.setOnClickListener(v -> saveDisplayName());
        btnAddSatellite.setOnClickListener(v -> addSatellite());
    }

    private void reloadUserDetails() {
        Log.d(TAG, "Reloading user display name and email");
        ProfileActivity activity = (ProfileActivity) requireActivity();

        String displayName = activity.getUserDisplayName();
        String email = activity.getUserEmail();

        tvDisplayName.setText(displayName != null ? displayName : getString(R.string.no_data_available));
        tvEmail.setText(email != null ? email : getString(R.string.no_data_available));
    }

    private void saveDisplayName() {
        String newName = etDisplayName.getText().toString().trim();
        if (!newName.isEmpty()) {
            if (newName.length() > 20) {
                newName = newName.substring(0, 20);
                Log.w(TAG, "Display name truncated to 20 characters");
            }
            Log.d(TAG, "Saving new display name: " + newName);
            ((ProfileActivity) requireActivity()).setUserDisplayName(newName);
            reloadUserDetails();
        } else {
            Log.w(TAG, "Attempted to save empty display name");
            Toast.makeText(requireContext(), "Please enter a valid display name.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addSatellite() {
        String satelliteId = etSatelliteID.getText().toString().trim();
        if (satelliteId.isEmpty()) {
            Log.w(TAG, "Empty satellite ID input");
            Toast.makeText(requireContext(), "Please enter a valid satellite ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Validating satellite ID: " + satelliteId);
        isSatelliteIdValid(satelliteId, new IStorageCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    int satId = Integer.parseInt(satelliteId);
                    Log.d(TAG, "Satellite ID valid, saving: " + satId);
                    StorageManager.getInstance(getContext()).addFavouriteSatelliteId(satId, new IStorageCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            satelliteIds.add(satelliteId);
                            adapter.notifyItemInserted(satelliteIds.size() - 1);
                            Toast.makeText(requireContext(), "Satellite added!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.w(TAG, "Failed to store satellite ID: " + errorMessage);
                            Toast.makeText(requireContext(), "Failed to add satellite: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.w(TAG, "Invalid TLE or satellite ID not found");
                    Toast.makeText(requireContext(), "Invalid satellite ID or no TLE data found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.w(TAG, "Error validating satellite ID: " + errorMessage);
                Toast.makeText(requireContext(), "Error validating satellite: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isSatelliteIdValid(String satelliteId, IStorageCallback<Boolean> callback) {
        try {
            int id = Integer.parseInt(satelliteId);
            SatelliteManager.getInstance().fetchSatelliteTLE(id, new IN2YOCallback() {
                @Override
                public void onSuccess(ISatelliteResponse response) {
                    SatelliteTLEResponse tleResponse = (SatelliteTLEResponse) response;
                    boolean isValid = tleResponse.getTle() != null && !tleResponse.getTle().isEmpty();
                    Log.d(TAG, "TLE validation result: " + isValid);
                    callback.onSuccess(isValid);
                }

                @Override
                public void onError(String errorMessage) {
                    Log.w(TAG, "Failed to fetch TLE: " + errorMessage);
                    callback.onFailure("Invalid satellite ID or no TLE found.");
                }
            });
        } catch (NumberFormatException e) {
            Log.e(TAG, "Satellite ID is not a valid number: " + satelliteId, e);
            callback.onFailure("Satellite ID must be a valid number.");
        }
    }

    private void updateSatelliteList() {
        Log.d(TAG, "Fetching stored satellite ID list");
        StorageManager.getInstance(getContext()).getFavouriteSatelliteIds(new IStorageCallback<List<String>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<String> result) {
                Log.d(TAG, "Fetched " + result.size() + " satellite IDs");
                satelliteIds.clear();
                satelliteIds.addAll(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.w(TAG, "Failed to fetch satellite list: " + errorMessage);
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}