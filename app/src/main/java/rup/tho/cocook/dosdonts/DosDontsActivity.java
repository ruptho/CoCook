package rup.tho.cocook.dosdonts;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import rup.tho.cocook.R;
import rup.tho.cocook.util.db.CoCookDatabaseHelper;

public class DosDontsActivity extends AppCompatActivity implements AddDialogFragment.Listener {

    private CoCookDatabaseHelper dbHelper;
    private PreferenceAdapter mAdapter;
    private ListView mListView;
    private final static String TAG = DosDontsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dos_donts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = getHelper();
//        dbHelper.createNewDatabase();

        mAdapter = new PreferenceAdapter(dbHelper, this);
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);

        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator), getResources().getString(R.string.do_dont_intro), Snackbar.LENGTH_INDEFINITE).
                setAction(getResources().getString(R.string.dos_donts_okay), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // dismiss
                    }
                }).setActionTextColor(ContextCompat.getColor(this, R.color.bt_pressed_color));
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(5);  // show multiple line
        snackbar.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dos_donts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else {
            AddDialogFragment dialog = new AddDialogFragment();
            Bundle args = new Bundle();
            args.putBoolean("is_do", item.getItemId() == R.id.add_do);
            dialog.setArguments(args);
            dialog.setListener(this);
            dialog.show(getFragmentManager(), "AddDialogFragment");
        }
        return super.onOptionsItemSelected(item);
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
    public void returnFromDlg() {
        Log.d(TAG, "update called");
        mAdapter.updateCursor();
        mListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });
    }
}
