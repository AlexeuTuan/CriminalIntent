package com.example.criminalintent;

import android.content.Context;

public interface Callbacks {
    void onCrimeSelected(Crime crime, Context context);
    void onCrimeUpdated(Crime crime);
}
