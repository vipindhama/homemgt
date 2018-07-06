package com.homeguide.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.homeguide.fragments.ExpenseListFragment;
import com.homeguide.fragments.IncomeListFragment;

import java.util.List;

/**
 * Created by Shweta on 12/2/2016.
 */

public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;
    SparseArray<Fragment> registeredFragments = new SparseArray<>();
    List<Fragment> fragmentList;

    public TabsPagerAdapter(FragmentManager fm, int numOfTabs,List<Fragment> fragmentList) {
        super(fm);
        this.numOfTabs=numOfTabs;
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return fragmentList.get(0);
            case 1:
                return fragmentList.get(1);
            case 2:
                return fragmentList.get(2);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
