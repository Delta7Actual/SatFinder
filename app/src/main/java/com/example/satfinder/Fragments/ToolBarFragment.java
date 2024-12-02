package com.example.satfinder.Fragments;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.satfinder.Activities.LoginActivity;
import com.example.satfinder.Activities.ProfileActivity;
import com.example.satfinder.Activities.SettingsActivity;
import com.example.satfinder.R;
import com.google.firebase.auth.FirebaseAuth;

public class ToolBarFragment extends Fragment {

    public ToolBarFragment() {
        super(R.layout.fragment_tool_bar);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable android.os.Bundle savedInstanceState) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);

        toolbar.setOnMenuItemClickListener(item -> handleMenuItemClick(item, getContext()));
    }

    private boolean handleMenuItemClick(MenuItem item, Context context) {
        if (context == null) return false;

        int itemId = item.getItemId();
        if (itemId == R.id.action_more) {
            // Handle "More" action
            // TODO: Implement MoreActivity
            return true;
        } else if (itemId == R.id.action_options) {
            if (isNotCurrentActivity(context, SettingsActivity.class)) {
                context.startActivity(new Intent(context, SettingsActivity.class));
            }
            return true;
        } else if (itemId == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            context.startActivity(new Intent(context, LoginActivity.class));
            return true;
        } else if (itemId == R.id.action_profile) {
            if (isNotCurrentActivity(context, ProfileActivity.class)) {
                context.startActivity(new Intent(context, ProfileActivity.class));
            }
            return true;
        }

        return false;
    }

    private boolean isNotCurrentActivity(Context context, Class<?> targetActivity) {
        // Check if the current activity is of the target type
        return !context.getClass().equals(targetActivity);
    }
}
