package com.example.satfinder.Activities;

import android.annotation.SuppressLint;
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

import com.example.satfinder.R;

public class LocateActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "SatLocate";

    private SensorManager sensorManager;
    private ImageView ivCompass;
    private ImageView ivNeedle;
    private TextView tvAngleValue;

    // Sensor data
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    // Unlike pitch we need the azimuth globally
    private float satAzimuth;

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
        setupSensors();
        loadIntentData();
    }

    private void setupUI() {
        Log.d(TAG, "setupUI: Initializing UI components");
        
        ivCompass = findViewById(R.id.iv_compass);
        ivNeedle = findViewById(R.id.iv_compass_needle);
        tvAngleValue = findViewById(R.id.tv_angle_value);

        Button btnReturn = findViewById(R.id.button_return);
        btnReturn.setOnClickListener(v -> {
            Log.d(TAG, "Return button clicked");
            startActivity(new Intent(this, BrowserActivity.class));
            finish();
        });
    }

    private void setupSensors() {
        Log.d(TAG, "Setting up sensors...");
        
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Log.e(TAG, "Rotation vector sensor unavailable.");
            Toast.makeText(this, "Sensor unavailable! Cannot detect orientation.", Toast.LENGTH_SHORT).show();
        }
    }

    // Get the data provided with the Intent from BrowserActivity
    @SuppressLint("SetTextI18n")
    private void loadIntentData() {
        satAzimuth = getIntent().getFloatExtra("sat_azimuth", 0f);
        float satElevation = getIntent().getFloatExtra("sat_pitch", 0f);

        tvAngleValue.setText("Satellite Elevation: " + satElevation + "°");

        Log.d(TAG, "Azimuth from intent: " + satAzimuth);
        Log.d(TAG, "Pitch from intent: " + satElevation);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            float azimuth = (float) Math.toDegrees(orientationAngles[0]);
            float pitch = (float) Math.toDegrees(orientationAngles[1]);

            // Normalize azimuth
            if (azimuth < 0) azimuth += 360;

            Log.d(TAG, String.format("Device Azimuth: %.1f°, Pitch: %.1f°", azimuth, pitch));
            updateCompass(azimuth);
        }
    }

    private void updateCompass(float currentAzimuth) {
        float offset = calculateAzimuthOffset(currentAzimuth, satAzimuth);

        ivNeedle.setRotation(currentAzimuth);

        if (Math.abs(offset) < 10f) {
            ivCompass.setColorFilter(getResources().getColor(R.color.green, getTheme()));
        } else {
            ivCompass.clearColorFilter();
        }

        Log.d(TAG, "Azimuth offset from satellite: " + offset + "°");
    }

    private float calculateAzimuthOffset(float current, float target) {
        return (target - current + 540) % 360 - 180;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Sensor accuracy changed: " + accuracy);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            Log.d(TAG, "Sensor listener unregistered");
        }
    }
}