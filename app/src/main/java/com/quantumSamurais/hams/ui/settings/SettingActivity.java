package com.quantumSamurais.hams.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quantumSamurais.hams.R;

public class SettingActivity extends AppCompatActivity {

    private RadioButton acceptApptDefaultRB;
    private Intent intent;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settingspage);
        setup();
        acceptApptDefaultRB.setOnClickListener(this::onRBClicked);
    }
    private void setup() {
        acceptApptDefaultRB = findViewById(R.id.acceptRB);

        intent = getIntent();
        boolean val = intent.getBooleanExtra("acceptsByDefault", false);

        if (val) {
            acceptApptDefaultRB.toggle();
        }

    }

    private void onRBClicked(View view) {

        boolean state = acceptApptDefaultRB.isChecked();

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
}
