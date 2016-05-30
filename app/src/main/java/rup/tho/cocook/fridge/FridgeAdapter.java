package rup.tho.cocook.fridge;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import rup.tho.cocook.R;
import rup.tho.cocook.data.Ingredient;
import rup.tho.cocook.data.Unit;
import rup.tho.cocook.fridge.ingtofridge.IngredientToFridgeActivity;
import rup.tho.cocook.util.db.CoCookDatabaseHelper;

public class FridgeAdapter extends CursorAdapter{
    private static final String TAG = FridgeAdapter.class.getSimpleName();
    private CoCookDatabaseHelper dbHelper;
    private Context context;
    public FridgeAdapter(CoCookDatabaseHelper helper, Context context) {
        super(context, helper.getIngredientsInFridgeSorted(), 0);
        this.dbHelper = helper;
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_fridge, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(Ingredient.INGREDIENT_ID_FIELD_NAME));

        // register listeners
        view.setOnClickListener(new FridgeItemClickListener(id));
        ((TextView)view.findViewById(R.id.tvIngredient))
                .setText(cursor.getString(
                        cursor.getColumnIndexOrThrow(Ingredient.INGREDIENT_NAME_FIELD_NAME)));


        View.OnClickListener listener = new FridgeItemDeleteClickListener(id);
        view.findViewById(R.id.btDelete).setOnClickListener(listener);

        // TextView text
        ((TextView)view.findViewById(R.id.tvAmount)).setText(String.format("%d %s",
                cursor.getInt(cursor.getColumnIndexOrThrow(Ingredient.INGREDIENT_AMOUNT_FIELD_NAME)),
                dbHelper.getUnitForId(cursor.getLong(cursor.getColumnIndexOrThrow(Ingredient.INGREDIENT_UNIT_FIELD_NAME))).getShortName()));
    }


    public void updateCursor()
    {
        FridgeAdapter.this.changeCursor(dbHelper.getIngredientsInFridgeSorted());
    }

    private class FridgeItemDeleteClickListener implements View.OnClickListener {
        private long id;

        FridgeItemDeleteClickListener(long id) {
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btDelete:
                    Log.d(TAG, "delete"+id);
                    Snackbar.make(v, FridgeAdapter.this.context.getResources().getString(R.string.fridge_delete_question), Snackbar.LENGTH_LONG)
                            .setAction(context.getResources().getString(R.string.dos_donts_delete_answer), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dbHelper.deleteIngredientFromFridge(id);
                                    updateCursor();
                                }
                            }).setActionTextColor(ContextCompat.getColor(context, R.color.bt_pressed_color)).show();
                    break;
                default:
                    Log.d(TAG, "no matching button"+id);
            }
            updateCursor();
        }
    }

    private class FridgeItemClickListener implements View.OnClickListener {

        private long id;
        public FridgeItemClickListener(long id) {
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(FridgeAdapter.this.context, IngredientToFridgeActivity.class);
            Ingredient ing = FridgeAdapter.this.dbHelper.getIngDao().queryForId(id);
            intent.putExtra(Ingredient.INGREDIENT_NAME_FIELD_NAME, ing.getName());
            intent.putExtra(Ingredient.INGREDIENT_AMOUNT_FIELD_NAME, ing.getAmount());
            intent.putExtra(Ingredient.INGREDIENT_UNIT_FIELD_NAME, ing.getUnit().getId());
            FridgeAdapter.this.context.startActivity(intent);
        }
    }
}
