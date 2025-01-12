package com.example.satfinder.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.satfinder.Managers.SatelliteManager;
import com.example.satfinder.R;

public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    private EditText etSatelliteId;
    private Button btnSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etSatelliteId = view.findViewById(R.id.et_satellite_id);
        btnSearch = view.findViewById(R.id.btn_search);

        btnSearch.setOnClickListener(v -> handleSearch());

        return view;
    }

    private void handleSearch() {
        if (etSatelliteId.getText().toString().isEmpty()) {
            Toast.makeText(SearchFragment.this.getContext(), "Please enter a valid satellite ID!", Toast.LENGTH_LONG).show();
            return;
        }
        SatelliteManager manager = SatelliteManager.getInstance();
        int satelliteId = Integer.parseInt(etSatelliteId.getText().toString());

        // TODO: CONTINUE THIS SHIT
    }
}