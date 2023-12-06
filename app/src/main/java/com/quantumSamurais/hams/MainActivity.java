package com.quantumSamurais.hams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    Button signUpBtn, signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
        addListeners();
        //Intent debug = new Intent(this, DoctorMain.class);
        //debug.putExtra("doctorEmailAddress", "ange6@gmail.com");
        //startActivity(debug);
    }

    public void setup() {
        signInBtn = findViewById(R.id.signInBtnMain);
        signUpBtn = findViewById(R.id.signUpBtnMain);
    }
    public void addListeners() {
        signUpBtn.setOnClickListener(this::signUpClicked);
        signInBtn.setOnClickListener(this::signInClicked);
    }

    public void signUpClicked(View view){
        Intent accountTypeSelect = new Intent(this, AccountTypeSelectionActivity.class);
        accountTypeSelect.putExtra("selectType","signUp");
        startActivity(accountTypeSelect);
    }
    public void signInClicked(View view){
        Intent accountTypeSelect = new Intent(this, AccountTypeSelectionActivity.class);
        accountTypeSelect.putExtra("selectType","signIn");
        startActivity(accountTypeSelect);
    }


}