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

    private SensorManager sensorManager;
    private ImageView ivCompass;
    private ImageView ivNeedle;
    private ImageView ivAngle;
    private TextView tvAngleValue;
    private Button btnReturn;

    private final float[] rMat = new float[9]; // Rotation matrix
    // 0 is azimuth, 1 is pitch, 2 is roll
    private final float[] orientationVector = new float[3]; // Orientation vector

    private float satAzimuth;
    private float satPitch;

    private void setupUI() {
        ivCompass = findViewById(R.id.iv_compass);
        ivNeedle = findViewById(R.id.iv_compass_needle);
        ivAngle = findViewById(R.id.iv_angle);
        tvAngleValue = findViewById(R.id.tv_angle_value);
        btnReturn = findViewById(R.id.button_return);
        btnReturn.setOnClickListener(v -> {
            startActivity(new Intent(LocateActivity.this, BrowserActivity.class));
            finish();
        });
    }

    private void setupSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor rvSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (rvSensor != null) {
            sensorManager.registerListener(this, rvSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
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
            return insets;
        });

        setupUI();
        setupSensors();
        satAzimuth = getIntent().getFloatExtra("sat_azimuth", 0);
        satPitch = getIntent().getFloatExtra("sat_pitch", 0);

        Log.d("SAT", "SAT AZ': " + satAzimuth);
        Log.d("SAT", "SAT PI': " + satPitch);
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
            updateValues(azimuthInDegrees, pitchInDegrees + 90);
            Log.d("TAG", "onSensorChanged: " + SatUtils.getDirectionFromAzimuth(azimuthInDegrees));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // TODO: Handle this
        // Do i have to?
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Sensor cleanup
        sensorManager.unregisterListener(this);
    }

    public void updateValues(float azimuth, float pitch) {
        float azOffset = satAzimuth - azimuth;
        float piOffset = satPitch - pitch;

        // Keep compass the same, rotate needle to show where user is looking
        ivNeedle.setRotation(azimuth);
        tvAngleValue.setText("Angle: " + String.format("%.2f", pitch) + "°");

        if (Math.abs(piOffset) < 10) {
            ivAngle.setColorFilter(getResources().getColor(R.color.green, this.getTheme()));
        } else {
            if (ivAngle.getColorFilter() != null) {
                ivAngle.clearColorFilter();
            }

            if (Math.abs(azOffset) < 10) {
                ivCompass.setColorFilter(getResources().getColor(R.color.green, this.getTheme()));
            } else {
                if (ivCompass.getColorFilter() != null) {
                    ivCompass.clearColorFilter();
                }
            }
        }
    }
}