package com.example.satfinder.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.Objects.Interfaces.IStorageCallback;
import com.example.satfinder.R;

import java.util.List;

public class SatelliteIdAdapter extends RecyclerView.Adapter<SatelliteIdAdapter.ViewHolder> {

    private List<String> satelliteIds;

    public SatelliteIdAdapter(List<String> satelliteIds) {
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

        holder.removeButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                String idToRemove = satelliteIds.get(adapterPosition);

                try {
                    int id = Integer.parseInt(idToRemove);

                    // Remove from storage via StorageManager
                    StorageManager.getInstance().removeFavouriteSatelliteId(id, new IStorageCallback<Void>() {
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
    }

    @Override
    public int getItemCount() {
        return satelliteIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView satelliteIdText;
        Button removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            satelliteIdText = itemView.findViewById(R.id.tv_satellite_id);
            removeButton = itemView.findViewById(R.id.btn_remove_satellite_item);
        }
    }
}