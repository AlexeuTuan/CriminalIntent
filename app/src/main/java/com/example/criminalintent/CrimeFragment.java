package com.example.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telecom.Call;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.example.criminalintent.databinding.FragmentCrimeBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String ARG_CRIME_POSITION = "crime_position";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PHOTO = "DialogPhoto";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;
    private static final int REQUEST_BIG_PHOTO = 4;
    private static int reportKind;

    private FragmentCrimeBinding binding;
    private Bitmap bitmap;

    private Crime mCrime;
    private File mPhotoFile;

    private Callbacks mCallbacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);
    }

    @Override
    public View onCreateView (LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {

        final PackageManager packageManager = getActivity().getPackageManager();

        binding = FragmentCrimeBinding.inflate(inflater, container, false);
        binding.ptCrimeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO
            }
        });

        mPhotoFile = CrimeLab.getInstance(getActivity()).getPhotoFile(mCrime);


        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        binding.takePhotoButton.setEnabled(canTakePhoto);
        binding.takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.criminalintent.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                    uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });


        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            binding.suspectButton.setEnabled(false);
        }


        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.US);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm", Locale.US);

        binding.dateButton.setText(dateFormatter.format(mCrime.getDate()));
        binding.timeButton.setText(timeFormatter.format(mCrime.getDate()));

        binding.crimeSolved.setChecked(mCrime.isSolved());

        binding.photoView.setVisibility(View.VISIBLE);
        binding.photoView.setImageResource(R.drawable.new_avatar);

        binding.dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        binding.timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                Time time = new Time(mCrime.getDate().getMinutes(),mCrime.getDate().getHours());
                TimePickerFragment dialog = TimePickerFragment.newInstance(time);
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        binding.ptCrimeTitle.setText(mCrime.getTitle());

        binding.crimeSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        binding.sendReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                startActivity(Intent.createChooser(i,getString(R.string.send_report)));
                 */
                switch (reportKind) {
                    case 0:
                        // Intent callIntent = new Intent(Intent.ACTION_CALL);
                        // callIntent.setData(Uri.parse("tel")); //change the number
                        // startActivity(callIntent);
                        break;
                    case 1:
                        shareText("text/plain",getCrimeReport(),getString(R.string.crime_report_subject),REQUEST_CONTACT);
                        break;
                }
            }
        });

        binding.suspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.report_crime_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.reportSpinner.setAdapter(adapter);
        binding.reportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.getItemAtPosition(position);
                switch (position) {
                    case 0:
                        reportKind = 0;
                        break;
                    case 1:
                        reportKind = 1;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ...
            }
        });

        if (mCrime.getSuspect() != null) {
            binding.suspectButton.setText(mCrime.getSuspect());
        }

        updatePhotoView();

        if(binding.photoView != null) {
            binding.photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    FragmentManager manager = getFragmentManager();
                    PhotoFragment dialog = PhotoFragment.newInstance(byteArray);
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_BIG_PHOTO);
                    dialog.show(manager,DIALOG_PHOTO);
                }
            });
        }

        return binding.getRoot();
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getInstance(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            updateCrime();
            mCrime.setDate(date);
            SimpleDateFormat format = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.US);
            binding.dateButton.setText(format.format(mCrime.getDate()));
        } else if(requestCode == REQUEST_TIME) {
            Time time = (Time) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.getDate().setMinutes(time.getMinute());
            mCrime.getDate().setHours(time.getHour());
            SimpleDateFormat format = new SimpleDateFormat("hh:mm",Locale.US);
            updateCrime();
            binding.timeButton.setText(format.format(mCrime.getDate()));
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Определение полей, значения которых должны быть
            // возвращены запросом.

            /*
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };
             */

            // Выполнение запроса - contactUri здесь выполняет функции
            // условия "where"
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, null, null, null, null);

            try {
                // Проверка получения результатов
                if (c.getCount() == 0) {
                    return;
                }
                // Извлечение первого столбца данных - имени подозреваемого.
                c.moveToFirst();
                String suspect = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                updateCrime();
                mCrime.setSuspect(suspect);
                binding.suspectButton.setText(suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.criminalintent.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateCrime();
            updatePhotoView();
        }
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    public void shareText(String mimeType, String extraText, String extraSubject, int requestCode) {

        Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                .setType(mimeType)
                .setText(extraText)
                .getIntent();

        shareIntent.putExtra(Intent.EXTRA_TEXT, extraText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, extraSubject);

        if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivity(Intent.createChooser(shareIntent, getString(R.string.send_report)));
        }
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            binding.photoView.setImageDrawable(null);
            binding.photoView.setContentDescription(getString(R.string.crime_photo_no_image_description));
        } else {
            bitmap = PictureUtils.RotateBitmap(PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity()),0);
            binding.photoView.setImageBitmap(bitmap);
            binding.photoView.setContentDescription(getString(R.string.crime_photo_image_description));
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void updateCrime() {
        CrimeLab.getInstance(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }
}