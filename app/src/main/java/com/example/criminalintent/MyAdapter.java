package com.example.criminalintent;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<Crime> mDataSet;

    private final int Violent_Crimes = 1;
    private final int Light_Crimes = 2;
    private static final int REQUEST_CRIME = 1;

    private final Context context;
    private Callbacks mCallbacks;

    private int position;



    public class ViolentCrimesHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public Crime mCrime;
        public View view;
        public TextView title, date;
        public ImageView solved;
        public TextView requiresPolice;
        private int position;

        public ViolentCrimesHolder(View v) {
            super(v);
            view = v;
            title = v.findViewById(R.id.tv_crime_title);
            date = v.findViewById(R.id.tv_crime_date);
            requiresPolice = v.findViewById(R.id.tv_requiresPolice);

            solved = v.findViewById(R.id.im_solved);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        public void bind(Crime crime, int position) {
            mCrime = crime;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            /*
            Intent intent = new Intent(context, CrimePagerActivity.class);
            intent.putExtra("Crime",mCrime.getId());
            intent.putExtra("Position",position);
            ((Activity) context).startActivityForResult(intent,REQUEST_CRIME);
             */
            mCallbacks.onCrimeSelected(mCrime,context);
        }

        @Override
        public boolean onLongClick(View view) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Alert message to be shown");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDataSet.remove(position);
                            // removeViewAt(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, mDataSet.size());
                            CrimeLab.getInstance(context).deleteCrime(mCrime.getId());
                        }
                    });
            alertDialog.show();
            return true;
        }

    }
    public class LightCrimesHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public Crime mCrime;
        public View view;
        public TextView title, date;
        public ImageView solved;
        private int position;
        public LightCrimesHolder(View v) {
            super(v);
            view = v;
            title = v.findViewById(R.id.tv_crime_title);
            date = v.findViewById(R.id.tv_crime_date);
            solved = v.findViewById(R.id.im_solved);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }


        public void bind(Crime crime, final int position) {
            mCrime = crime;
            this.position = position;

        }

        @Override
        public void onClick(View v) {
            /*
            Intent intent = new Intent(context, CrimePagerActivity.class);
            intent.putExtra("Crime", mCrime.getId());
            intent.putExtra("Position",position);
            ((Activity) context).startActivityForResult(intent,REQUEST_CRIME);
             */
            mCallbacks.onCrimeSelected(mCrime,context);

        }

        @Override
        public boolean onLongClick(View view) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Alert message to be shown");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDataSet.remove(position);
                            // removeViewAt(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, mDataSet.size());
                            CrimeLab.getInstance(context).deleteCrime(mCrime.getId());
                        }
                    });
            alertDialog.show();
            return true;
        }
    }

    public MyAdapter(List<Crime> mDataSet, Context context, Callbacks callbacks) {
        this.mDataSet = mDataSet;
        this.context = context;
        this.mCallbacks = callbacks;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v_violent = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item_violent, parent, false);
        View v_light = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item_light, parent, false);

        switch (viewType) {
            case Violent_Crimes:
                return new ViolentCrimesHolder(v_violent);
            case Light_Crimes:
                return new LightCrimesHolder(v_light);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViolentCrimesHolder) {
            ((ViolentCrimesHolder) holder).title.setText(mDataSet.get(position).getTitle());
            SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMM dd, yyyy 'at' hh:mm:ss", Locale.US);
            ((ViolentCrimesHolder) holder).date.setText(dateFormatter.format(mDataSet.get(position).getDate()));
            if(!mDataSet.get(position).isSolved()) {
                ((ViolentCrimesHolder) holder).solved.setVisibility(View.INVISIBLE);
            } else {
                ((ViolentCrimesHolder) holder).solved.setVisibility(View.VISIBLE);
            }
            ((ViolentCrimesHolder) holder).bind(mDataSet.get(position), position);
        } else if(holder instanceof LightCrimesHolder) {
            ((LightCrimesHolder) holder).title.setText(mDataSet.get(position).getTitle());
            SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMM dd, yyyy 'at' hh:mm:ss", Locale.US);
            ((LightCrimesHolder) holder).date.setText(dateFormatter.format(mDataSet.get(position).getDate()));
            if(!mDataSet.get(position).isSolved()) {
                ((LightCrimesHolder) holder).solved.setVisibility(View.INVISIBLE);
            } else {
                ((LightCrimesHolder) holder).solved.setVisibility(View.VISIBLE);
            }
            ((LightCrimesHolder) holder).bind(mDataSet.get(position), position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mDataSet.get(position).ismRequiresPolice()) {
            return Violent_Crimes;
        } else {
            return Light_Crimes;
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void setCrimes(List<Crime> crimes) {
        mDataSet = crimes;
    }
}