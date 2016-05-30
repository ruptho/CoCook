package rup.tho.cocook.fridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import rup.tho.cocook.R;
import rup.tho.cocook.fridge.ingtofridge.IngredientToFridgeActivity;
import rup.tho.cocook.util.db.CoCookDatabaseHelper;

public class FridgeActivity extends AppCompatActivity {
    CoCookDatabaseHelper dbHelper;
    FridgeAdapter mAdapter;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = getHelper();

        mAdapter = new FridgeAdapter(dbHelper, this);
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);

        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator), getResources().getString(R.string.fridge_intro), Snackbar.LENGTH_INDEFINITE).
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
        getMenuInflater().inflate(R.menu.menu_fridge, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else {
            Intent intent = new Intent(this, IngredientToFridgeActivity.class);
            startActivity(intent);
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

}
