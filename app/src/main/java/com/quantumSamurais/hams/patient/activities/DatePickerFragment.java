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

    public DatePickerFragment(DateCallback callback, String date) {
        this.toCall = callback;
        LocalDate currentDate = LocalDate.parse(date);
        currentDate = currentDate.minusMonths(1);
        year = currentDate.getYear();
        month = currentDate.getMonthValue();
        day = currentDate.getDayOfMonth();
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceSate) {
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
