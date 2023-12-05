package com.quantumSamurais.hams.patient.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.quantumSamurais.hams.R;

public class RateDoctorFragment extends DialogFragment {

    RatingCallback callback;


    float rating;
    public interface RatingCallback {
        void rate(float Rating);
    }
    public RateDoctorFragment(RatingCallback callback) {
        super(R.layout.fragment_rate_doctor);
        this.rating = 0f;
        this.callback = callback;
//        rateBtn = this.findVIew
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        Button rateBtn = v.findViewById(R.id.sumbitRate);
        RatingBar ratingBar = v.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(this::onChange);
        rateBtn.setOnClickListener(this::rateBtnClicked);
        return v;
    }

    private void onChange(RatingBar ratingBar, float rating, boolean fromUser) {
        this.rating = rating;
    }

    public void rateBtnClicked(View v) {
        this.callback.rate(rating);
        this.dismiss();
    }

}
