package com.example.satfinder.Adapters;

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

    private List<String> satelliteIds;

    public SavedSatelliteAdapter(List<String> satelliteIds) {
        this.satelliteIds = satelliteIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_satellite_id, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String satelliteId = satelliteIds.get(position);
        holder.satelliteIdText.setText(satelliteId);

        holder.btnRemove.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                String idToRemove = satelliteIds.get(adapterPosition);

                try {
                    int id = Integer.parseInt(idToRemove);

                    // Remove from storage via StorageManager
                    StorageManager.getInstance(null).removeFavouriteSatelliteId(id, new IStorageCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            satelliteIds.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(holder.itemView.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (NumberFormatException e) {
                    // We really shouldn't arrive here
                    Toast.makeText(holder.itemView.getContext(), "Invalid satellite ID format", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.btnNotify.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                SatelliteManager manager = SatelliteManager.getInstance();
                ObserverLocation observerLocation = StorageManager.getInstance(holder.itemView.getContext()).spGetUserLocation();
                manager.fetchSatelliteVisualPasses(Integer.parseInt(satelliteIds.get(adapterPosition))
                        , observerLocation.getLatitude()
                        , observerLocation.getLongitude()
                        , observerLocation.getAltitude()
                        , 7
                        , 60
                        , new IN2YOCallback() {
                            @Override
                            public void onSuccess(ISatelliteResponse response) {
                                SatelliteVisualPassesResponse svpResponse = (SatelliteVisualPassesResponse) response;
                                if (svpResponse != null) {
                                    if (svpResponse.getPasses() == null) {
                                        Toast.makeText(holder.itemView.getContext(), "No passes in the next 7 days!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (svpResponse.getPasses().isEmpty()) {
                                        Toast.makeText(holder.itemView.getContext(), "Couldn't fetch pass time!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    AlarmScheduler.scheduleNotification(holder.itemView.getContext()
                                            , svpResponse.getPasses().get(0).getStartUTC());
                                    Toast.makeText(holder.itemView.getContext(), "Setting notification for " + satelliteIds.get(adapterPosition), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(holder.itemView.getContext(), "Couldn't fetch pass time!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(holder.itemView.getContext(), "Couldn't fetch pass time!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
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