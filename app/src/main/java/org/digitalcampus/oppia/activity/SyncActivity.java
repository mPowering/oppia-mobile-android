package org.digitalcampus.oppia.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.opendeliver.oppia.R;
import org.digitalcampus.oppia.adapter.ActivityPagerAdapter;
import org.digitalcampus.oppia.fragments.ExportActivityFragment;
import org.digitalcampus.oppia.fragments.TransferFragment;

import java.util.ArrayList;
import java.util.List;

public class SyncActivity extends AppActivity {

    public static final String TAG = SyncActivity.class.getSimpleName();
    public static final String TAB_ACTIVE = "TAB_ACTIVE";

    public static final int TAB_TRANSFER = 0;
    public static final int TAB_ACTIVITY = 1;

    private ViewPager viewPager;
    private TabLayout tabs;
    private int currentTab = 0;
    private TransferFragment transferFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sync);
        viewPager = (ViewPager) findViewById(R.id.activity_sync_pager);
        tabs = (TabLayout) findViewById(R.id.tabs_toolbar);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            currentTab = bundle.getInt(SyncActivity.TAB_ACTIVE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Make the Toolbar back button call the back press (to close possible bluetooth connection)
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (toolbar != null){
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        transferFragment = TransferFragment.newInstance();
        fragments.add(transferFragment);
        titles.add(this.getString(R.string.tab_title_transfer));

        Fragment fExport = ExportActivityFragment.newInstance();
        fragments.add(fExport);
        titles.add(this.getString(R.string.tab_title_activity));

        ActivityPagerAdapter adapter = new ActivityPagerAdapter(this, getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
        adapter.updateTabViews(tabs);
        viewPager.setCurrentItem(currentTab);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
    }



    @Override
    public void onBackPressed() {
        transferFragment.onBackPressed();
        super.onBackPressed();
    }
}
