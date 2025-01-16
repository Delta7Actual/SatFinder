package com.example.satfinder.Activities;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.satfinder.R;

public class LocateActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private ImageView ivAzimuth;
    private Button btnReturn;

    private final float[] rMat = new float[9]; // Rotation matrix
    // 0 is azimuth, 1 is pitch, 2 is roll
    private final float[] orientationVector = new float[3]; // Orientation vector
    private float neededAzimuth;

    private void setupUI() {
        ivAzimuth = findViewById(R.id.iv_azimuth);
        btnReturn = findViewById(R.id.button_return);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_locate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();
        btnReturn.setOnClickListener(v -> {
            startActivity(new Intent(LocateActivity.this, BrowserActivity.class));
            finish();
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor rvSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (rvSensor != null) {
            sensorManager.registerListener(this, rvSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Sensors unavailable! Cannot triangulate orientation!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // Extract the orientation from the rotation vector
            SensorManager.getRotationMatrixFromVector(rMat, sensorEvent.values);
            SensorManager.getOrientation(rMat, orientationVector);

            float azimuthInDegrees = (float) Math.toDegrees(orientationVector[0]);
            // Normalize azimuth to 0-360 degrees
            if (azimuthInDegrees < 0) {
                azimuthInDegrees += 360;
            }
            updateCompassDirection(azimuthInDegrees);
            Log.d("TAG", "onSensorChanged: " + getDirectionFromAzimuth(azimuthInDegrees));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Handle later...
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Sensor cleanup
        sensorManager.unregisterListener(this);
    }

    private void updateCompassDirection(float azimuth) {
        float offset = neededAzimuth - azimuth;
        ivAzimuth.setRotation(offset);
        if (offset < 10 && offset > -10) {
            ivAzimuth.setColorFilter(R.color.primary);
        }
        else {
            if (ivAzimuth.getColorFilter() != null) {
                ivAzimuth.clearColorFilter();
            }
        }
    }

    // Put in utils class?
    // Helper function to get the direction based on azimuth
    private String getDirectionFromAzimuth(float azimuth) {
        if (azimuth >= 348.75 || azimuth < 11.25) {
            return "N";
        } else if (azimuth >= 11.25 && azimuth < 33.75) {
            return "NNE";
        } else if (azimuth >= 33.75 && azimuth < 56.25) {
            return "NE";
        } else if (azimuth >= 56.25 && azimuth < 78.75) {
            return "ENE";
        } else if (azimuth >= 78.75 && azimuth < 101.25) {
            return "E";
        } else if (azimuth >= 101.25 && azimuth < 123.75) {
            return "ESE";
        } else if (azimuth >= 123.75 && azimuth < 146.25) {
            return "SE";
        } else if (azimuth >= 146.25 && azimuth < 168.75) {
            return "SSE";
        } else if (azimuth >= 168.75 && azimuth < 191.25) {
            return "S";
        } else if (azimuth >= 191.25 && azimuth < 213.75) {
            return "SSW";
        } else if (azimuth >= 213.75 && azimuth < 236.25) {
            return "SW";
        } else if (azimuth >= 236.25 && azimuth < 258.75) {
            return "WSW";
        } else if (azimuth >= 258.75 && azimuth < 281.25) {
            return "W";
        } else if (azimuth >= 281.25 && azimuth < 303.75) {
            return "WNW";
        } else if (azimuth >= 303.75 && azimuth < 326.25) {
            return "NW";
        } else if (azimuth >= 326.25 && azimuth < 348.75) {
            return "NNW";
        } else {
            return "Unknown";
        }
    }
}