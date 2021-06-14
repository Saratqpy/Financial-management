package com.example.financial_management;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyAdapter extends FragmentPagerAdapter{

    private Context myContext;
    int totalTabs;
    EditText editText;

    public MyAdapter(Context context,FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ReportFragment reportFragment   = new ReportFragment();
                return reportFragment;
            case 1:
                SubmitFragment submitFragment = new SubmitFragment();
                return submitFragment;
            case 2:
                ShowFragment showFragment = new ShowFragment();
                return showFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
