package com.quantumSamurais.hams.doctor.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.databinding.NavigationDrawerMainBinding;
import com.quantumSamurais.hams.doctor.Doctor;

//<>
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.Navigation;
import androidx.navigation.NavController;


import com.google.android.material.navigation.NavigationView;
import com.quantumSamurais.hams.ui.settings.SettingFragment;

//<> may be used; will delete later
//TODO potentially delete these items.
public class DoctorMain extends AppCompatActivity {
    private Doctor myDoctor;

    private AppBarConfiguration myAppBarConfig;

    private NavigationDrawerMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getSerializableExtra("doctor") == null){
            Log.d("doctorMain", "somehow the intent wasn't passed properly.");
            Toast.makeText(this, "An error occurred, this session will be terminated, try again", Toast.LENGTH_SHORT).show();
            finish();
        }


        myDoctor = (Doctor) getIntent().getSerializableExtra("doctor");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_doctor_view);

        boolean acceptApptDefault = myDoctor.getAcceptAppointmentsByDefault();
        SettingFragment fragment = SettingFragment.newInstance(acceptApptDefault);





        binding = NavigationDrawerMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navigationView;

        setSupportActionBar(binding.appBarMain.toolbar);
        myAppBarConfig = new AppBarConfiguration.Builder(R.id.viewSettings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, myAppBarConfig);
        NavigationUI.setupWithNavController(navigationView, navController);

    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, myAppBarConfig) || super.onSupportNavigateUp();
    }

    private void setup() {
        //Sets the info for the Tab Layout
        TextView firstName = findViewById(R.id.firstNameDoc);
        firstName.setText(myDoctor.getFirstName());

    }
}
