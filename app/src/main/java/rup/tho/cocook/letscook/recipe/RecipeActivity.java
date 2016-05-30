package rup.tho.cocook.letscook.recipe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import rup.tho.cocook.R;
import rup.tho.cocook.data.Recipe;
import rup.tho.cocook.letscook.cook.CookStepActivity;
import rup.tho.cocook.util.db.CoCookDatabaseHelper;

public class RecipeActivity extends AppCompatActivity {

    private static final String TAG = RecipeActivity.class.getSimpleName();
    private Recipe mRecipe;
    private CoCookDatabaseHelper dbHelper;
    private ListView mListView;
    private RecipeIngredientsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);


        mRecipe = getHelper().getRecipeDao().queryForId(
                getIntent().getLongExtra(Recipe.RECIPE_ID_FIELD_NAME, -1));
        if (mRecipe == null) {
            Log.e(TAG, "ERROR: Recipe was null");
            this.finish();
        }

        setTitle(String.format("%s - %s", mRecipe.getName(), getString(R.string.recipe_overview)));

        TextView mTvDescription = (TextView) findViewById(R.id.tvDescription);
        mTvDescription.setMovementMethod(new ScrollingMovementMethod());
        String description = mRecipe.getDescription();
        if (description != null && description.length() != 0) { // else default text in layout
            mTvDescription.setText(mRecipe.getDescription());
        }

        mListView = (ListView) findViewById(R.id.listIngredients);
        mAdapter = new RecipeIngredientsAdapter(this, getHelper(), mRecipe);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.lets_cook) {
            Intent intent = new Intent(this, CookStepActivity.class);
            intent.putExtra(Recipe.RECIPE_ID_FIELD_NAME, mRecipe.getId());
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
