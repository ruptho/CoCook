package rup.tho.cocook.letscook.recipe;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import rup.tho.cocook.R;
import rup.tho.cocook.data.Ingredient;
import rup.tho.cocook.data.IngredientRecipe;
import rup.tho.cocook.data.Recipe;
import rup.tho.cocook.util.db.CoCookDatabaseHelper;

/**
 * Created by thorsten on 5/29/16.
 */
public class RecipeIngredientsAdapter extends CursorAdapter {
    private CoCookDatabaseHelper dbHelper;
    public RecipeIngredientsAdapter(Context context, CoCookDatabaseHelper helper, Recipe recipe) {
        super(context, helper.getIngredientsForRecipe(recipe), 0);
        this.dbHelper = helper;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_recipe_ing, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.tvIngredient)).setText(
                dbHelper.getIngredientById(cursor.getLong(
                cursor.getColumnIndex(IngredientRecipe.INGRECIPE_ING_FIELD_NAME))).getName());
        ((TextView) view.findViewById(R.id.tvAmount)).setText(String.valueOf(cursor.getInt(
                cursor.getColumnIndex(IngredientRecipe.INGRECIPE_AMOUNT_FIELD_NAME))));
        ((TextView) view.findViewById(R.id.tvUnit)).setText(dbHelper.getUnitForId(cursor.getLong(
                cursor.getColumnIndex(IngredientRecipe.INGRECIPE_UNIT_FIELD_NAME))).getShortName());
    }
}
