package com.quantumSamurais.hams.ui.settings;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.quantumSamurais.hams.MainActivity;
import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.doctor.activities.DoctorMain;

public class SettingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private CheckBox acceptApptDefaultCB;
    private Intent intent;

    DrawerLayout drawerLayout;

    ActionBarDrawerToggle actionBarDrawerToggle;

    NavigationView navView;

    private boolean acceptByDefault;
    private Doctor doctor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        setup();
        acceptApptDefaultCB.setOnClickListener(this::onCBClicked);


        drawerLayout = findViewById(R.id.drawer_layout);


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        navView = findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    private void setup() {
        acceptApptDefaultCB = findViewById(R.id.acceptCB);
        intent = getIntent();

        acceptByDefault = intent.getBooleanExtra("acceptByDefault", false);
        if (acceptByDefault) {
            acceptApptDefaultCB.toggle();
        }


    }

    private void onCBClicked(View view) {
        boolean state = acceptApptDefaultCB.isChecked();
        Intent newIntent = new Intent();
        newIntent.putExtras(intent);
        if(state) { // was clicked; meaning that the option was not done before.
            newIntent.putExtra("acceptsByDefault", true);
            Toast.makeText(SettingActivity.this, "Appointments will be accepted by Default."
                    , Toast.LENGTH_SHORT).show();
        } else {
            newIntent.putExtra("acceptsByDefault", false);
            Toast.makeText(SettingActivity.this, "Appointments will not be accepted by Default."
                    , Toast.LENGTH_SHORT).show();
        }
        setIntent(newIntent);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent newIntent;

        if(item.getItemId() == R.id.nav_home) {
            if (getWindow().getDecorView().findViewById(android.R.id.content).getId() !=
                    R.id.drawer_layout || getWindow().getDecorView().getRootView().getId() == R.id.nav_home) {
                finish();
            }
        } else if (item.getItemId() == R.id.nav_settings) {
            newIntent = new Intent(this, SettingActivity.class);
            startActivity(newIntent);

        } else if (item.getItemId() == R.id.nav_appointments ) {
            newIntent = new Intent(this, MainActivity.class);
            startActivity(newIntent);
        }

        return true;
    }
}
