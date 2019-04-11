package com.babach.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.babach.criminalintent.database.CrimeBaseHelper;
import com.babach.criminalintent.database.CrimeCursorWrapper;
import com.babach.criminalintent.database.CrimeDbSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.babach.criminalintent.database.CrimeDbSchema.*;

/**
 * Created by sbaba on 09-Jan-19.
 */

public class CrimeLab
{

    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab getCrimeLab(Context context)
    {
        if (sCrimeLab == null)
        {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }


    private CrimeLab(Context context)
    {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }


    public List<Crime> getCrimes()
    {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }
        finally
        {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id)
    {
        CrimeCursorWrapper cursorWrapper = queryCrimes(CrimeTable.Cols.UUID + "=?", new
                String[]{id.toString()});
        try
        {
            if (cursorWrapper.getCount() == 0)
            {
                return null;
            }
            cursorWrapper.moveToFirst();
            return cursorWrapper.getCrime();
        }
        finally
        {
            cursorWrapper.close();
        }
    }

    public void addCrime(Crime crime)
    {
        ContentValues values = getContentValues(crime);

        mDatabase.insert(CrimeTable.NAME, null, values);
    }


    public void deleteCrime(Crime crime)
    {
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + "=?", new String[]{crime.getId()
                .toString()});
    }


    public File getPhotoFile(Crime crime)
    {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, crime.getPhotoFilename());
    }

    public void updateCrime(Crime crime)
    {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + "= ?", new
                String[]{uuidString});
    }


    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs)
    {
        Cursor cursor = mDatabase.query(CrimeTable.NAME, null, whereClause, whereArgs, null, null,
                null);

        return new CrimeCursorWrapper(cursor);
    }


    public static ContentValues getContentValues(Crime crime)
    {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved());
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }

}
