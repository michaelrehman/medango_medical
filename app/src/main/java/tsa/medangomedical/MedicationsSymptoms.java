package tsa.medangomedical;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

/** The activity that the ManageMedications and ManageSymptoms fragments reside in */
public class MedicationsSymptoms extends AppCompatActivity {

    private SectionsPageAdapter mSectionPageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medications_symptoms);

        mSectionPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        //Set up the ViewPager with the sections adapter
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager (ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ManageMedications(), "Manage");
        adapter.addFragment(new ManageSymptoms(), "Symptoms");
        viewPager.setAdapter(adapter);
    }

}