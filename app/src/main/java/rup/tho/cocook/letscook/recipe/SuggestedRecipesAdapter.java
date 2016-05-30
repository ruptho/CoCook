package rup.tho.cocook.letscook.recipe;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import rup.tho.cocook.R;
import rup.tho.cocook.data.Recipe;
import rup.tho.cocook.util.db.CoCookDatabaseHelper;

/**
 * Created by thorsten on 5/29/16.
 */
public class SuggestedRecipesAdapter extends CursorAdapter {

    private static final String TAG = SuggestedRecipesAdapter.class.getSimpleName();
    private final Context context;
    private final CoCookDatabaseHelper dbHelper;

    public SuggestedRecipesAdapter(CoCookDatabaseHelper helper, Context context) {
        super(context, helper.getAllRecipes(), 0);
        this.dbHelper = helper;
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_suggested_recipes, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.tvRecipe)).setText(
                cursor.getString(cursor.getColumnIndex(Recipe.RECIPE_NAME_FIELD_NAME)));
        ((TextView) view.findViewById(R.id.tvTime)).setText(String.format("%02d:%02dh",
                cursor.getInt(cursor.getColumnIndex(Recipe.RECIPE_HOURS_FIELD_NAME)),
                cursor.getInt(cursor.getColumnIndex(Recipe.RECIPE_MINUTES_FIELD_NAME))));

        ImageView ivDiff = (ImageView) view.findViewById(R.id.ivDifficulty);
        TextView tvDiff = (TextView) view.findViewById(R.id.tvDifficulty);
        switch(Recipe.Difficulty.values()[
                cursor.getInt(cursor.getColumnIndex(Recipe.RECIPE_DIFFICULTY_FIELD_NAME))]){
            case EASY:
                tvDiff.setText(context.getString(R.string.recipe_easy));
                ivDiff.setImageResource(R.drawable.ic_sentiment_very_satisfied_black_24dp);
                break;
            case MEDIUM:
                tvDiff.setText(context.getString(R.string.recipe_medium));
                ivDiff.setImageResource(R.drawable.ic_sentiment_satisfied_black_24dp);
                break;
            case HARD:
                // already set in layout!
                break;
            default:
                Log.e(TAG, "invalid value for difficulty enum!");
        }
        view.setOnClickListener(new OnSuggestedRecipeItemClickListener(
                cursor.getLong(cursor.getColumnIndex(Recipe.RECIPE_ID_FIELD_NAME))));
    }

    private class OnSuggestedRecipeItemClickListener implements View.OnClickListener {
        private long recipeId;

        public OnSuggestedRecipeItemClickListener(long recipeId) {
            this.recipeId = recipeId;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, RecipeActivity.class);
            intent.putExtra(Recipe.RECIPE_ID_FIELD_NAME, recipeId);
            context.startActivity(intent);
        }
    }
}
