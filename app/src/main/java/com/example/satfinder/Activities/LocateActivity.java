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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.satfinder.Misc.Utility.SatUtils;
import com.example.satfinder.R;

public class LocateActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "SatLocate"; // Updated TAG for logging

    private SensorManager sensorManager;
    private ImageView ivCompass;
    private ImageView ivNeedle;
    private TextView tvAngleValue;
    private Button btnReturn;

    private final float[] rMat = new float[9]; // Rotation matrix
    private final float[] orientationVector = new float[3]; // Orientation vector

    private float satAzimuth;

    private void setupUI() {
        ivCompass = findViewById(R.id.iv_compass);
        ivNeedle = findViewById(R.id.iv_compass_needle);
        tvAngleValue = findViewById(R.id.tv_angle_value);
        btnReturn = findViewById(R.id.button_return);
        btnReturn.setOnClickListener(v -> {
            Log.d(TAG, "Return button clicked, redirecting to BrowserActivity...");
            startActivity(new Intent(LocateActivity.this, BrowserActivity.class));
            finish();
        });
    }

    private void setupSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor rvSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (rvSensor != null) {
            Log.d(TAG, "Rotation vector sensor found, registering listener...");
            sensorManager.registerListener(this, rvSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.e(TAG, "Rotation vector sensor unavailable! Cannot triangulate orientation.");
            Toast.makeText(this, "Sensors unavailable! Cannot triangulate orientation!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_locate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Log.d(TAG, "Applied system bar insets: " + systemBars.toString());
            return insets;
        });

        setupUI();
        setupSensors();

        // Fetch satellite azimuth and pitch from intent
        satAzimuth = getIntent().getFloatExtra("sat_azimuth", 0);
        float satPitch = getIntent().getFloatExtra("sat_pitch", 0);
        tvAngleValue.setText("Satellite Elevation: " + satPitch + "°");

        Log.d(TAG, "Satellite Azimuth: " + satAzimuth);
        Log.d(TAG, "Satellite Pitch: " + satPitch);
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

            float pitchInDegrees = (float) Math.toDegrees(orientationVector[1]);

            Log.d(TAG, "Sensor azimuth: " + azimuthInDegrees + "°, pitch: " + pitchInDegrees + "°");
            updateValues(azimuthInDegrees);

            Log.d(TAG, "Direction from azimuth: " + SatUtils.getDirectionFromAzimuth(azimuthInDegrees));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Placeholder for handling sensor accuracy changes
        Log.d(TAG, "Sensor accuracy changed: " + accuracy);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Sensor cleanup
        if (sensorManager != null) {
            Log.d(TAG, "Unregistering sensor listener...");
            sensorManager.unregisterListener(this);
        }
    }

    public void updateValues(float azimuth) {
        float azOffset = getAngleDifference(satAzimuth, azimuth);

        // Keep compass the same, rotate needle to show where user is looking
        ivNeedle.setRotation(azimuth);
        Log.d(TAG, "Azimuth offset: " + azOffset + "°");

        if (Math.abs(azOffset) < 10) {
            ivCompass.setColorFilter(getResources().getColor(R.color.green, this.getTheme()));
            Log.d(TAG, "Azimuth aligned with satellite, compass in range.");
        } else {
            ivCompass.clearColorFilter();
        }
    }

    private float getAngleDifference(float target, float current) {
        return (target - current + 180 + 360) % 360 - 180;
    }

}