package tsa.medangomedical;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/** Handles adding the ManageMedications and ManageSymptoms tabs to MedicationsSymptoms */
public class SectionsPageAdapter extends FragmentPagerAdapter {

    /** Keeps track of the fragments(tabs) */
    private final List<Fragment> mFragmentList = new ArrayList<>();
    /** Keeps track of the fragment names */
    private final List<String> mFragmentTitleList = new ArrayList<>();

    /** Adds the fragments to the FragmentList */
    public void addFragment (Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle (int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}