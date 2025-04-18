package com.example.satfinder.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.satfinder.Activities.BrowserActivity;
import com.example.satfinder.Activities.MainActivity;
import com.example.satfinder.Activities.ProfileActivity;
import com.example.satfinder.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationFragment extends Fragment {

    public BottomNavigationFragment() {
        super(R.layout.fragment_bottom_navigation);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);

        // Set the selected item based on the activity
        if (requireActivity() instanceof MainActivity) {
            bottomNavigationView.setSelectedItemId(R.id.action_home);
        } else if (requireActivity() instanceof BrowserActivity) {
            bottomNavigationView.setSelectedItemId(R.id.action_browse);
        } else if (requireActivity() instanceof ProfileActivity) {
            bottomNavigationView.setSelectedItemId(R.id.action_profile);
        }

        // Handle navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> handleMenuItemClick(item, getContext()));
    }

    private boolean handleMenuItemClick(MenuItem item, Context context) {
        if (context == null) return false;

        int itemId = item.getItemId();
        if (itemId == R.id.action_home && isNotCurrentActivity(context, MainActivity.class)) {
            startActivity(new Intent(context, MainActivity.class));
            return true;
        } else if (itemId == R.id.action_browse && isNotCurrentActivity(context, BrowserActivity.class)) {
            startActivity(new Intent(context, BrowserActivity.class));
            return true;
        } else if (itemId == R.id.action_profile && isNotCurrentActivity(context, ProfileActivity.class)) {
            startActivity(new Intent(getContext(), ProfileActivity.class));
            return true;
        }
        return true;
    }

    private boolean isNotCurrentActivity(Context context, Class<?> targetActivity) {
        // Make sure the context is not the target activity
        return !context.getClass().equals(targetActivity);
    }
}