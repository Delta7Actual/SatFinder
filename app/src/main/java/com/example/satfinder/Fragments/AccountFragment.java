package com.example.satfinder.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.satfinder.Activities.ProfileActivity;
import com.example.satfinder.Adapters.SatelliteIdAdapter;
import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.Objects.Interfaces.IStorageCallback;
import com.example.satfinder.R;

import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends Fragment {

    private EditText etDisplayName, etSatelliteID;
    private Button btnSaveDisplayName, btnAddSatellite;
    private RecyclerView recyclerSatelliteList;
    private SatelliteIdAdapter adapter;
    private List<String> satelliteIds;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize views
        etDisplayName = view.findViewById(R.id.et_display_name);
        etSatelliteID = view.findViewById(R.id.et_satellite_id);
        btnSaveDisplayName = view.findViewById(R.id.btn_save_display_name);
        btnAddSatellite = view.findViewById(R.id.btn_add_satellite);
        recyclerSatelliteList = view.findViewById(R.id.recycler_satellite_list);

        // Set up RecyclerView
        satelliteIds = new ArrayList<>();
        adapter = new SatelliteIdAdapter(satelliteIds);
        recyclerSatelliteList.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerSatelliteList.setAdapter(adapter);

        // Set up button listeners
        btnSaveDisplayName.setOnClickListener(v -> saveDisplayName());
        btnAddSatellite.setOnClickListener(v -> addSatellite());

        // Load the initial list of satellite IDs
        updateSatelliteList();

        return view;
    }

    private void saveDisplayName() {
        String newName = etDisplayName.getText().toString().trim();
        if (!newName.isEmpty()) {
            ((ProfileActivity) requireActivity()).setUserDisplayName(newName);
            Toast.makeText(requireContext(), "Display name updated: " + newName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Please enter a valid display name.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addSatellite() {
        String satelliteId = etSatelliteID.getText().toString().trim();
        if (!satelliteId.isEmpty()) {
            StorageManager.getInstance().addFavouriteSatelliteId(Integer.parseInt(satelliteId), new IStorageCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    satelliteIds.add(satelliteId);
                    adapter.notifyItemInserted(satelliteIds.size() - 1);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "Please enter a valid satellite ID.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSatelliteList() {
        StorageManager.getInstance().getFavouriteSatelliteIds(new IStorageCallback<List<String>>() {
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
