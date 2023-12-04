package com.quantumSamurais.hams.patient.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    int year;
    int month;
    int day;

    DateCallback toCall;

    public DatePickerFragment(DateCallback callback) {
        this.toCall = callback;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceSate) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(requireContext(), this, year, month, day);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        toCall.onDateSelected(LocalDate.of(year, month+1, day).toString());
    }

    public interface DateCallback {
        void onDateSelected(String date);
    }

}
