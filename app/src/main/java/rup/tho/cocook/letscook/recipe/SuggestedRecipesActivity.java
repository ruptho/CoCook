package rup.tho.cocook.letscook.recipe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import rup.tho.cocook.R;
import rup.tho.cocook.data.Recipe;
import rup.tho.cocook.util.db.CoCookDatabaseHelper;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private CoCookDatabaseHelper dbHelper;
    private ListView mListView;
    private SuggestedRecipesAdapter adapter;
    private RadioGroup mGroupColumn;
    private RadioGroup mGroupOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        mGroupColumn = (RadioGroup) findViewById(R.id.rgColumn);
        mGroupOrder = (RadioGroup) findViewById(R.id.rgOrder);

        mListView = (ListView) findViewById(R.id.listView);
        adapter = new SuggestedRecipesAdapter(getHelper(), this);
        mListView.setAdapter(adapter);
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

    public void onOrderColumnClicked(View view) {
        sort();
    }

    public void onOrderClicked(View view) {
        sort();
    }

    private void sort() {
        boolean ascending = mGroupOrder.getCheckedRadioButtonId() == R.id.radioAscending;

        String column = "";
        switch(mGroupColumn.getCheckedRadioButtonId()) {
            case R.id.radioName:
                column = Recipe.RECIPE_NAME_FIELD_NAME;
                break;
            case R.id.radioTime:
                column = Recipe.RECIPE_HOURS_FIELD_NAME;
                break;
            case R.id.radioDifficulty:
                column = Recipe.RECIPE_DIFFICULTY_FIELD_NAME;
                break;
        }

        adapter.changeCursor(getHelper().getRecipesSorted(column, ascending));
    }
}
