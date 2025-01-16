package com.example.satfinder.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.satfinder.Fragments.SearchFragment;
import com.example.satfinder.R;

public class BrowserActivity extends AppCompatActivity {

    private Button btnLocate;

    private void setupUI() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.browser_fragment_container, new SearchFragment())
                .addToBackStack(null) // Add the transaction to the backstack
                .commit();
        btnLocate = findViewById(R.id.btn_locate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browser);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();
        btnLocate.setOnClickListener(v -> {
            startActivity(new Intent(BrowserActivity.this, LocateActivity.class));
            finish();
        });
    }
}
