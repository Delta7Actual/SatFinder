package com.example.satfinder.Activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.satfinder.Managers.SatelliteManager;
import com.example.satfinder.Managers.StorageManager;
import com.example.satfinder.Objects.ISatelliteResponse;
import com.example.satfinder.Objects.Interfaces.IN2YOCallback;
import com.example.satfinder.Objects.ObserverLocation;
import com.example.satfinder.Objects.SatelliteVisualPassesResponse;
import com.example.satfinder.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    // private RecyclerView recyclerSatelliteList;
    // private SatelliteIdAdapter adapter;
    private TextView tvGreeting;
    private TextView tvISSPassDetails;

    private void setupUI() {
        tvGreeting = findViewById(R.id.tv_greeting);
        tvGreeting.setText(FirebaseAuth
                .getInstance()
                .getCurrentUser()
                .getDisplayName());
        tvISSPassDetails = findViewById(R.id.tv_iss_pass_details);
        updateWithClosestISSPass();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();
    }


    // TODO: translate UTC to local time
    private void updateWithClosestISSPass() {
        SatelliteManager satelliteManager = SatelliteManager.getInstance();
        StorageManager storageManager = StorageManager.getInstance();

        ObserverLocation observerLocation = storageManager.spGetUserLocation(this);
        satelliteManager.fetchSatelliteVisualPasses(25544,
                observerLocation.getLatitude(),
                observerLocation.getLongitude(),
                observerLocation.getAltitude(),
                3,
                30,
                new IN2YOCallback() {
                    @Override
                    public void onCallSuccess(ISatelliteResponse response) {
                        SatelliteVisualPassesResponse svpResponse = (SatelliteVisualPassesResponse) response;
                        tvISSPassDetails.setText(String.format("Next pass is in: %d", svpResponse.getPasses().get(0).getStartUTC()));
                    }

                    @Override
                    public void onCallError(String errorMessage) {
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
