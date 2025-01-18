package com.rodrigo.colecione.util;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public class CustomSpinnerAdapter<T> extends ArrayAdapter<T> {

    public CustomSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        return super.getCount() - 1;
    }
}
