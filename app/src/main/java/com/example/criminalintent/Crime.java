package com.example.criminalintent;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class Crime {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private Time mTime;
    private boolean mSolved;
    private boolean mRequiresPolice = false;
    private String mSuspect;
    private static int id = 0;
    private final int position = id++;

    public Crime() {
        this(UUID.randomUUID());
        mId = UUID.randomUUID();
        mDate = new Date();
        mTime = new Time(mDate.getMinutes(),mDate.getHours());
    }
    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }
    /* =====================Getter And Setter methods===================== */
    public boolean ismRequiresPolice() {
        return mRequiresPolice;
    }
    public void setRequiresPolice(boolean mRequiresPolice) {
        this.mRequiresPolice = mRequiresPolice;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }
    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }
    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getSuspect() {
        return mSuspect;
    }
    public void setSuspect(String mSuspect) {
        this.mSuspect = mSuspect;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public int getPosition() {
        return position;
    }
    /* =================================================================== */


    @NonNull
    @Override
    public String toString() {
        return mTitle + "\n" + mDate;
    }
}
