package com.saxionact.ehi2vsd3.takeaway.dialogs;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.saxionact.ehi2vsd3.takeaway.R;
import com.saxionact.ehi2vsd3.takeaway.activities.AddingHoursActivity;

import java.util.Timer;

public class PauzeDialog extends DialogFragment{

    private NumberPicker npHours;
    private NumberPicker npMinutes;
    private Button btnAdd;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_pauze, container, false);

        // Do all the stuff to initialize your custom view
        npHours = v.findViewById(R.id.npHours);
        npMinutes = v.findViewById(R.id.npMinutes);
        btnAdd = v.findViewById(R.id.btnAdd);

        setDividerColor(npHours, Color.WHITE);
        setDividerColor(npMinutes, Color.WHITE);

        npHours.setMinValue(0);
        npHours.setMaxValue(24);
        npHours.setDisplayedValues(new String[]{"Hours", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"});

        npMinutes.setMinValue(0);
        npMinutes.setMaxValue(12);
        npMinutes.setDisplayedValues(new String[]{"Minutes", "0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"});

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }

    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
