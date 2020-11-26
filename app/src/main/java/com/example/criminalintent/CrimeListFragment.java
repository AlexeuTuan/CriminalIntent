package com.example.criminalintent;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.criminalintent.databinding.FragmentCrimeLabBinding;

import java.util.List;

public class CrimeListFragment extends Fragment {

    private FragmentCrimeLabBinding binding;

    private static final int REQUEST_CRIME = 1;
    private MyAdapter mAdapter;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;

    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean("subtitle");
        }
        setHasOptionsMenu(true);
        updateSubtitle();
    }

    @Override
    public View onCreateView (LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        binding = FragmentCrimeLabBinding.inflate(inflater, container, false);
        final View view = binding.getRoot();

        ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(viewHolder instanceof MyAdapter.LightCrimesHolder) {
                    MyAdapter.LightCrimesHolder holder = (MyAdapter.LightCrimesHolder) viewHolder;
                    mAdapter.mDataSet.remove(position);
                    // removeViewAt(position);
                    mAdapter.notifyItemRemoved(position);
                    mAdapter.notifyItemRangeChanged(position, mAdapter.mDataSet.size());
                    CrimeLab.getInstance(getActivity()).deleteCrime(holder.mCrime.getId());
                } else if(viewHolder instanceof MyAdapter.ViolentCrimesHolder) {
                    MyAdapter.ViolentCrimesHolder holder = (MyAdapter.ViolentCrimesHolder) viewHolder;
                    mAdapter.mDataSet.remove(position);
                    // removeViewAt(position);
                    mAdapter.notifyItemRemoved(position);
                    mAdapter.notifyItemRangeChanged(position, mAdapter.mDataSet.size());
                    CrimeLab.getInstance(getActivity()).deleteCrime(holder.mCrime.getId());
                }
            }
        };

        binding.rvCrimeList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        binding.rvCrimeList.hasFixedSize();
        mAdapter = new MyAdapter(CrimeLab.getInstance(getActivity()).getCrimes(), getActivity(), mCallbacks);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvCrimeList);
        binding.rvCrimeList.setAdapter(mAdapter);

        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        updateUI();
        updateSubtitle();
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }





    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            mAdapter = new MyAdapter(crimes,getActivity(), mCallbacks);
            binding.rvCrimeList.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyItemChanged(position);
            Log.d("UI", CrimeLab.getInstance(getActivity()).getCrimes().toString());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CRIME) {
            if(resultCode == Activity.RESULT_OK) {
                position = data.getIntExtra("Position",-1);
                updateUI();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.getInstance(getActivity()).addCrime(crime);
                updateUI();
                mCallbacks.onCrimeSelected(crime, getActivity());
                Log.d("Crime","New Crime");
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("subtitle",mSubtitleVisible);
    }
}