package com.example.satfinder.Activities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.satfinder.R;

public class LocateActivity extends AppCompatActivity implements SensorEventListener {

    private Sensor rvSensor;
    private SensorManager sensorManager;

    private final float[] rMat = new float[9]; // Rotation matrix
    private final float[] orientationVector = new float[3]; // Orientation vector

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

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rvSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Use the recommended method
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

            // 0 is azimuth, 1 is pitch, 2 is roll
            // TODO: implement graphical representation of orientation
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
}