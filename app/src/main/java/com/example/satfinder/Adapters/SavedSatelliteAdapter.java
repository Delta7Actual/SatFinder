package com.example.satfinder.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.satfinder.Managers.SatelliteManager;
import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.Misc.AlarmScheduler;
import com.example.satfinder.Objects.Interfaces.IN2YOCallback;
import com.example.satfinder.Objects.Interfaces.ISatelliteResponse;
import com.example.satfinder.Objects.Interfaces.IStorageCallback;
import com.example.satfinder.Objects.ObserverLocation;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;
import com.example.satfinder.R;

import java.util.List;

public class SavedSatelliteAdapter extends RecyclerView.Adapter<SavedSatelliteAdapter.ViewHolder> {

    private final String TAG = "SatSavedAdapter";
    private final List<String> satelliteIds;

    public SavedSatelliteAdapter(List<String> satelliteIds) {
        this.satelliteIds = satelliteIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "Creating new ViewHolder...");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_satellite_id, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String satelliteId = satelliteIds.get(position);
        Log.d(TAG, "Binding satellite ID: " + satelliteId + " at position " + position);
        holder.satelliteIdText.setText(satelliteId);

        holder.btnRemove.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                String idToRemove = satelliteIds.get(adapterPosition);
                Log.i(TAG, "Attempting to remove satellite ID: " + idToRemove);

                try {
                    int id = Integer.parseInt(idToRemove);

                    StorageManager.getInstance(null).removeFavouriteSatelliteId(id, new IStorageCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Log.i(TAG, "Successfully removed satellite ID: " + id);
                            satelliteIds.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e(TAG, "Failed to remove satellite ID: " + id + " - " + errorMessage);
                            Toast.makeText(holder.itemView.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid satellite ID format: " + idToRemove);
                    Toast.makeText(holder.itemView.getContext(), "Invalid satellite ID format", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w(TAG, "btnRemove clicked but adapter position was NO_POSITION");
            }
        });

        holder.btnNotify.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                Log.w(TAG, "btnNotify clicked but adapter position was NO_POSITION");
                return;
            }

            String satId = satelliteIds.get(adapterPosition);
            Log.i(TAG, "Scheduling notification for satellite ID: " + satId);

            SatelliteManager manager = SatelliteManager.getInstance();
            ObserverLocation observerLocation = StorageManager.getInstance(holder.itemView.getContext()).spGetUserLocation();

            Log.d(TAG, "Fetching passes for ID: " + satId + ", Lat: " + observerLocation.getLatitude() +
                    ", Lon: " + observerLocation.getLongitude() + ", Alt: " + observerLocation.getAltitude());

            manager.fetchSatelliteVisualPasses(
                    Integer.parseInt(satId),
                    observerLocation,
                    7,
                    60,
                    new IN2YOCallback() {
                        @Override
                        public void onSuccess(ISatelliteResponse response) {
                            SatelliteVisualPassesResponse svpResponse = (SatelliteVisualPassesResponse) response;
                            if (svpResponse != null && !svpResponse.getPasses().isEmpty()) {
                                long alarmTime = svpResponse.getPasses().get(0).getStartUTC() * 1000L;
                                int requestCode = satId.hashCode();

                                Log.i(TAG, "Pass found! Scheduling alarm at " + alarmTime + " for satellite: " + satId);
                                StorageManager.getInstance(v.getContext()).spSaveAndUpdateSatelliteData(SatelliteManager.getInstance(),
                                        () -> AlarmScheduler.scheduleNotification(holder.itemView.getContext(),
                                                alarmTime,
                                                requestCode,
                                                Integer.parseInt(satId)));
                                Toast.makeText(holder.itemView.getContext(), "Alarm set for " + satId, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.w(TAG, "No upcoming passes found for satellite: " + satId);
                                Toast.makeText(holder.itemView.getContext(), "No upcoming passes.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "Error fetching passes for satellite " + satId + ": " + errorMessage);
                            Toast.makeText(holder.itemView.getContext(), "Error fetching pass times.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });
    }

    @Override
    public int getItemCount() {
        Log.v(TAG, "Total satellite items: " + satelliteIds.size());
        return satelliteIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView satelliteIdText;
        Button btnRemove;
        ImageButton btnNotify;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            satelliteIdText = itemView.findViewById(R.id.tv_satellite_id);
            btnRemove = itemView.findViewById(R.id.btn_remove_satellite_item);
            btnNotify = itemView.findViewById(R.id.btn_notification);
        }
    }
}