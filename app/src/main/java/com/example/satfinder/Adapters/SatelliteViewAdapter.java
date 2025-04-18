package com.example.satfinder.Adapters;

import static java.lang.String.format;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.satfinder.Objects.SatelliteTLE;
import com.example.satfinder.Objects.SatelliteTLEResponse;
import com.example.satfinder.R;

import java.util.List;

public class SatelliteViewAdapter extends RecyclerView.Adapter<SatelliteViewAdapter.ViewHolder> {

    private static final String TAG = "SatViewAdapter";
    private final List<SatelliteTLEResponse> satelliteList;

    public SatelliteViewAdapter(List<SatelliteTLEResponse> satelliteList) {
        this.satelliteList = satelliteList;
        Log.d(TAG, "Adapter initialized with " + satelliteList.size() + " satellites");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "Creating ViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_satellite, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SatelliteTLEResponse response = satelliteList.get(position);
        SatelliteTLE tle = new SatelliteTLE(response.getTle());

        String name = response.getInfo().getSatname();
        int id = response.getInfo().getSatid();

        Log.d(TAG, "Binding satellite: " + name + " (ID: " + id + ") at position " + position);

        if (tle.getLine1().isEmpty() || tle.getLine2().isEmpty()) {
            Log.w(TAG, "Invalid TLE data for satellite: " + name);
            holder.bindInvalid();
        } else {
            Log.d(TAG, format("TLE parsed - Period: %.1f | Incl: %.1f | Apo: %.1f | Peri: %.1f",
                    tle.getOrbitalPeriod(), tle.getInclination(), tle.getApogee(), tle.getPerigee()));
            holder.bindData(name, id, tle);
        }
    }

    @Override
    public int getItemCount() {
        return satelliteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvId, tvPeriod, tvInclination, tvApogee, tvPerigee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_satellite_name);
            tvId = itemView.findViewById(R.id.tv_satellite_id);
            tvPeriod = itemView.findViewById(R.id.tv_orbital_period);
            tvInclination = itemView.findViewById(R.id.tv_inclination);
            tvApogee = itemView.findViewById(R.id.tv_apogee);
            tvPerigee = itemView.findViewById(R.id.tv_perigee);
        }

        public void bindInvalid() {
            tvName.setText(R.string.invalid);
            tvId.setText(R.string.invalid);
            tvPeriod.setText(R.string.invalid);
            tvInclination.setText(R.string.invalid);
            tvApogee.setText(R.string.invalid);
            tvPerigee.setText(R.string.invalid);
        }

        @SuppressLint("DefaultLocale")
        public void bindData(String name, int id, SatelliteTLE tle) {
            tvName.setText(name);
            tvId.setText(format("ID: %d", id));
            tvPeriod.setText(format("Orbital Period: %.1f min", tle.getOrbitalPeriod()));
            tvInclination.setText(format("Inclination: %.1f°", tle.getInclination()));
            tvApogee.setText(format("Apogee: %.1f km", tle.getApogee()));
            tvPerigee.setText(format("Perigee: %.1f km", tle.getPerigee()));
        }
    }
}