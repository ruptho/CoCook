package rup.tho.cocook.letscook.cook;

import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.util.ArrayList;

import rup.tho.cocook.R;
import rup.tho.cocook.data.CookStep;
import rup.tho.cocook.data.Recipe;
import rup.tho.cocook.util.db.CoCookDatabaseHelper;

public class CookStepActivity extends CoCookCameraListenerActivity implements RemoveFromFridgeDialogFragment.Listener {

    private static final String TAG = CookStepActivity.class.getSimpleName();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Recipe mRecipe;
    private ArrayList<CookStep> mStepsForRecipe;
    private CoCookDatabaseHelper dbHelper;
    private boolean mGesturesOn = false;
    private Button mBtGestures;
    private MenuItem mMiNext;
    private MenuItem mMiFinished;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cook_step);
        super.onCreate(savedInstanceState);

        mRecipe = getHelper().getRecipeDao().queryForId(
                getIntent().getLongExtra(Recipe.RECIPE_ID_FIELD_NAME, -1));
        if (mRecipe == null) {
            Log.e(TAG, "ERROR: Recipe was null");
            this.finish();
        }

        mStepsForRecipe = new ArrayList<>(mRecipe.getSteps());

        setTitle(getString(R.string.step_header, 1, mStepsForRecipe.size(), mRecipe.getName()));
        mBtGestures = (Button) findViewById(R.id.btHandGesture);
        initTabbedActivityUI();
    }

    private void initTabbedActivityUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);  // clear all scroll flags

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position < mStepsForRecipe.size()) {
                    setTitle(getString(R.string.step_header, position + 1, mStepsForRecipe.size(), mRecipe.getName()));
                    mMiNext.setVisible(true);
                    mMiFinished.setVisible(false);
                } else {
                    setTitle(mRecipe.getName());
                    mMiNext.setVisible(false);
                    mMiFinished.setVisible(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cook_step, menu);

        mMiNext = menu.findItem(R.id.action_next);
        mMiFinished = menu.findItem(R.id.action_finish);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_next:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                return true;
            case android.R.id.home: {
                int currItem = mViewPager.getCurrentItem();
                if (currItem == 0) {
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                }
            }
            return true;
            case R.id.action_finish:
                RemoveFromFridgeDialogFragment dlg = new RemoveFromFridgeDialogFragment();
                dlg.setListener(this);
                dlg.show(getFragmentManager(), "RemoveDlg");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onGestureButtonClicked(View view) {
        mGesturesOn = !mGesturesOn;
        if (mGesturesOn) {
            enableHandGestures();
        } else {
            disableHandGestures();
        }
    }

    @Override
    public void removeFromFridge() {
        dbHelper.removeIngredientsFromFridge(mRecipe);
        this.finish();
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class StepFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public StepFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static StepFragment newInstance(int sectionNumber) {
            StepFragment fragment = new StepFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int pos = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView;
            rootView = inflater.inflate(R.layout.fragment_cook_step, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            if (pos != -1) {
                textView.setText(((CookStepActivity) getActivity()).getStepForRecipe(pos).getText());
            } else {
                textView.setText(R.string.cook_step_finished);

            }
            return rootView;
        }
    }

    private CookStep getStepForRecipe(int pos) {
        return mStepsForRecipe.get(pos);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a StepFragment (defined as a static inner class below).
            position = position == mStepsForRecipe.size() ? -1 : position;
            return StepFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mStepsForRecipe.size() + 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "STEP " + (position + 1);
        }
    }

    protected CoCookDatabaseHelper getHelper() {
        if (dbHelper == null) {
            dbHelper = OpenHelperManager.getHelper(this, CoCookDatabaseHelper.class);
        }

        return dbHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
    }

    @Override
    protected void onSetUIGestureEnabled() {
        mBtGestures.setText(R.string.cook_step_disable_hand);
        mBtGestures.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_pan_tool_black_24dp, 0);
    }

    @Override
    protected void onSetUIGestureDisabled() {
        mBtGestures.setText(R.string.cook_step_enable_hand);
        mBtGestures.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_pan_tool_white_24dp, 0);
    }

    @Override
    public void onSwipeLeft() {
        int currentItem = mViewPager.getCurrentItem();
        if (currentItem > 0) {
            mViewPager.setCurrentItem(currentItem - 1);
        }
    }

    @Override
    public void onSwipeRight() {
        int currentItem = mViewPager.getCurrentItem();
        if (currentItem < mViewPager.getAdapter().getCount() - 1) {
            mViewPager.setCurrentItem(currentItem + 1);
        }
    }
}
