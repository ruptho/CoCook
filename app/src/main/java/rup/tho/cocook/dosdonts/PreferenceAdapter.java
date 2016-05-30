package rup.tho.cocook.dosdonts;

import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import rup.tho.cocook.R;
import rup.tho.cocook.data.Ingredient;
import rup.tho.cocook.util.db.CoCookDatabaseHelper;

/**
 * Created by thorsten on 5/27/16.
 */
public class PreferenceAdapter extends CursorAdapter {
    private static final String TAG = PreferenceAdapter.class.getSimpleName();
    private CoCookDatabaseHelper dbHelper;
    private Context context;
    public PreferenceAdapter(CoCookDatabaseHelper helper, Context context) {
        super(context, helper.getNotPreferencedIngredientsSorted(), 0);
        this.dbHelper = helper;
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_do_dont, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // set textview texts
        TextView tvIngredient = (TextView) view.findViewById(R.id.tvIngredient);
        ((TextView)view.findViewById(R.id.tvNumber)).setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(Ingredient.INGREDIENT_RANK_FIELD_NAME))));
        tvIngredient.setText(cursor.getString(cursor.getColumnIndexOrThrow(Ingredient.INGREDIENT_NAME_FIELD_NAME)));

        //register listener
        DoDontItemClickListener listener = new DoDontItemClickListener(cursor.getInt(cursor.getColumnIndexOrThrow(Ingredient.INGREDIENT_ID_FIELD_NAME)));
        view.findViewById(R.id.btDown).setOnClickListener(listener);
        view.findViewById(R.id.btUp).setOnClickListener(listener);
        view.findViewById(R.id.btDelete).setOnClickListener(listener);
        view.findViewById(R.id.btFirst).setOnClickListener(listener);
        view.findViewById(R.id.btLast).setOnClickListener(listener);

        // set icon
        if(cursor.getInt(cursor.getColumnIndex(Ingredient.INGREDIENT_PREFERENCED_FIELD_NAME)) == Ingredient.PrefVal.POS.value) {
            ((ImageView) view.findViewById(R.id.ivIcon)).setImageResource(R.drawable.ic_thumb_up_black_24dp);
        } else {
          //  already set
        }

    }


    private class DoDontItemClickListener implements View.OnClickListener {
        private long id;

        DoDontItemClickListener(long id) {
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btDown:
                    Log.d(TAG, "down"+id);
                    dbHelper.rankDownIngredientById(id);
                    break;
                case R.id.btUp:
                    Log.d(TAG, "up"+id);
                    dbHelper.rankUpIngredientById(id);
                    break;
                case R.id.btFirst:
                    Log.d(TAG, "first"+id);
                    dbHelper.rankFirstIngredientById(id);
                    break;
                case R.id.btLast:
                    Log.d(TAG, "last"+id);
                    dbHelper.rankLastIngredientById(id);
                    break;
                case R.id.btDelete:
                    Log.d(TAG, "delete"+id);
                    Snackbar.make(v, PreferenceAdapter.this.context.getResources().getString(R.string.dos_donts_delete_question), Snackbar.LENGTH_LONG)
                        .setAction(context.getResources().getString(R.string.dos_donts_delete_answer), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dbHelper.deleteIngredientFromPref(id);
                                PreferenceAdapter.this.changeCursor(dbHelper.getNotPreferencedIngredientsSorted());
                            }
                        }).setActionTextColor(ContextCompat.getColor(context, R.color.bt_pressed_color)).show();
                    break;
                default:
                    Log.d(TAG, "no matching button"+id);
            }
            updateCursor();
        }
    }

    public void updateCursor()
    {
        PreferenceAdapter.this.changeCursor(dbHelper.getNotPreferencedIngredientsSorted());
    }


}
