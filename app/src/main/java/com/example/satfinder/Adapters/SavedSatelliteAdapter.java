package com.example.satfinder.Adapters;

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
import java.util.Objects;

public class SavedSatelliteAdapter extends RecyclerView.Adapter<SavedSatelliteAdapter.ViewHolder> {

    private List<SatelliteTLEResponse> satelliteList;

    public SavedSatelliteAdapter(List<SatelliteTLEResponse> satelliteList) {
        this.satelliteList = satelliteList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_satellite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SatelliteTLEResponse satelliteResponse = satelliteList.get(position);
        SatelliteTLE satelliteTLE = new SatelliteTLE(satelliteResponse.getTle());
        if (Objects.equals(satelliteTLE.getLine1(), "") || Objects.equals(satelliteTLE.getLine2(), "")) {
            holder.tvSatelliteName.setText("Invalid Satellite");
            holder.tvSatelliteId.setText("Make sure you");
            holder.tvOrbitalPeriod.setText("have entered");
            holder.tvInclination.setText("the right");
            holder.tvApogee.setText("satellite");
            holder.tvPerigee.setText("ID");
        }
        else {
            holder.tvSatelliteName.setText(satelliteResponse.getInfo().getSatname());
            holder.tvSatelliteId.setText(String.format("ID: %d", satelliteResponse.getInfo().getSatid()));
            holder.tvOrbitalPeriod.setText(String.format("Orbital Period: %.3f min", satelliteTLE.getOrbitalPeriod()));
            holder.tvInclination.setText(String.format("Inclination: %.3f°", satelliteTLE.getInclination()));
            holder.tvApogee.setText(String.format("Apogee: %.3f km", satelliteTLE.getApogee()));
            holder.tvPerigee.setText(String.format("Perigee: %.3f km", satelliteTLE.getPerigee()));
        }
    }

    @Override
    public int getItemCount() {
        return satelliteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSatelliteName, tvSatelliteId, tvOrbitalPeriod, tvInclination, tvApogee, tvPerigee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSatelliteName = itemView.findViewById(R.id.tv_satellite_name);
            tvSatelliteId = itemView.findViewById(R.id.tv_satellite_id);
            tvOrbitalPeriod = itemView.findViewById(R.id.tv_orbital_period);
            tvInclination = itemView.findViewById(R.id.tv_inclination);
            tvApogee = itemView.findViewById(R.id.tv_apogee);
            tvPerigee = itemView.findViewById(R.id.tv_perigee);
        }
    }
}