package com.example.criminalintent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.criminalintent.databinding.ActivityCrimeLabBinding;

public class CrimeLabActivity extends AppCompatActivity implements Callbacks {
    ActivityCrimeLabBinding binding;
    FragmentTransaction transaction;
    Fragment fragment;

    private static final int REQUEST_CRIME = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCrimeLabBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_masterdetail);

        if(savedInstanceState == null) {
            fragment = new CrimeListFragment();
            FragmentManager fm = getSupportFragmentManager();
            transaction = fm.beginTransaction();
            transaction.add(R.id.fragment_container, fragment).commit();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onCrimeUpdated();
    }

    @Override
    public void onCrimeSelected(Crime crime, Context context) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(context, crime.getId());
            intent.putExtra("Position",CrimeLab.getInstance(context).getCrime(crime.getId()).getPosition());
            startActivityForResult(intent,REQUEST_CRIME);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

    public void onCrimeUpdated() {
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

}