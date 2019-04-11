package com.babach.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by sbaba on 09-Jan-19.
 */

public class CrimeListFragment extends Fragment
{

    private static final int ITEM_NO_POLICE_TYPE = 0;
    private static final int ITEM_POLICE_TYPE = 1;
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private Callbacks mCallBacks;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible)
        {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else
        {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }


    public interface Callbacks
    {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mCallBacks = (Callbacks) context;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallBacks = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.getCrimeLab(getActivity()).addCrime(crime);
                updateUI();
                mCallBacks.onCrimeSelected(crime);
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

    private void updateSubtitle()
    {
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount);
        if (!mSubtitleVisible)
        {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null)
        {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void updateUI()
    {
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null)
        {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
            return;
        } else
        {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateUI();
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        protected TextView mTitleTextView;
        protected TextView mDateTextView;
        private ImageView mSolvedImageView;
        protected Crime mCrime;


        public CrimeHolder(LayoutInflater inflater, ViewGroup parent)
        {

            super(inflater.inflate(R.layout.list_item_crime, parent, false));

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime)
        {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v)
        {
            mCallBacks.onCrimeSelected(mCrime);
        }
    }


    private class CrimePoliceHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        protected TextView mTitleTextView;
        protected TextView mDateTextView;
        protected TextView mRequiresPolice;
        protected Crime mCrime;


        public CrimePoliceHolder(LayoutInflater inflater, ViewGroup parent)
        {

            super(inflater.inflate(R.layout.list_item_crime_police, parent, false));

            mTitleTextView = itemView.findViewById(R.id.crime_police_title);
            mDateTextView = itemView.findViewById(R.id.crime_police_date);
            mRequiresPolice = itemView.findViewById(R.id.crime_requires_police);
            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime)
        {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mRequiresPolice.setText(String.valueOf(mCrime.isRequiresPolice()));
        }

        @Override
        public void onClick(View v)
        {
            Toast.makeText(getActivity(), String.format("Crime: %s created on %s was clicked! " +
                    "Call the police!", mCrime.getTitle(), mCrime.getDate()), Toast.LENGTH_SHORT)
                    .show();
        }
    }


    private class CrimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes)
        {
            this.mCrimes = crimes;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            switch (viewType)
            {
                case ITEM_POLICE_TYPE:
                {
                    return new CrimePoliceHolder(layoutInflater, parent);
                }
                default:
                {
                    return new CrimeHolder(layoutInflater, parent);
                }
            }
        }


        public void setCrimes(List<Crime> crimes)
        {
            mCrimes = crimes;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            Crime crime = mCrimes.get(position);
            switch (holder.getItemViewType())
            {
                case ITEM_POLICE_TYPE:
                {
                    CrimePoliceHolder policeHolder = (CrimePoliceHolder) holder;
                    policeHolder.bind(crime);
                    break;
                }
                default:
                case ITEM_NO_POLICE_TYPE:
                {
                    CrimeHolder crimeHolder = (CrimeHolder) holder;
                    crimeHolder.bind(crime);
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position)
        {
            Crime mCrime = mCrimes.get(position);
            return mCrime.isRequiresPolice() ? ITEM_POLICE_TYPE : ITEM_NO_POLICE_TYPE;
        }

        @Override
        public int getItemCount()
        {
            return mCrimes.size();
        }
    }

}
