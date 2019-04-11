package com.babach.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by sbaba on 24-Dec-18.
 */

public class Crime
{
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private String mSuspect;

    public boolean isRequiresPolice()
    {
        return mRequiresPolice;
    }

    public void setRequiresPolice(boolean requiresPolice)
    {
        mRequiresPolice = requiresPolice;
    }

    private boolean mRequiresPolice;
    private boolean mSolved;

    public Crime()
    {
        mId = UUID.randomUUID();
        mDate = new Date();
    }


    public Crime(UUID id)
    {
        mId = id;
        mDate = new Date();
    }

    public UUID getId()
    {
        return mId;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String title)
    {
        mTitle = title;
    }

    public Date getDate()
    {
        return mDate;
    }

    public String getSuspect()
    {
        return mSuspect;
    }

    public void setDate(Date date)
    {
        mDate = date;
    }

    public boolean isSolved()
    {
        return mSolved;
    }

    public void setSolved(boolean solved)
    {
        mSolved = solved;
    }

    public void setSuspect(String suspect)
    {
        this.mSuspect = suspect;
    }

    public String getPhotoFilename()
    {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
