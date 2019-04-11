package com.babach.criminalintent;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    public static final String EXTRA_CRIME = "org.babach.crime-id";

    @Override
    protected Fragment createFragment() {
        return CrimeFragment.newInstance(UUID.fromString(this.getIntent().getSerializableExtra(EXTRA_CRIME).toString()));
    }

    public static Intent newIntent(Context context, UUID crimeId)
    {
        Intent intent = new Intent(context, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME, crimeId);
        return intent;
    }

}
