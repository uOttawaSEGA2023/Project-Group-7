package com.quantumSamurais.hams.admin.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.database.Request;

public class ShowMoreActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_more);

            // Retrieve data passed
            Intent intent = getIntent();
            Request selectedRequest = intent.getParcelableExtra("selectedRequest");


        }
    }

