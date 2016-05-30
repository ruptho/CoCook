package rup.tho.cocook.fridge.ingtofridge;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;


import com.j256.ormlite.android.apptools.OpenHelperManager;


import java.sql.SQLException;
import java.util.ArrayList;

import rup.tho.cocook.R;
import rup.tho.cocook.data.Ingredient;
import rup.tho.cocook.data.Unit;
import rup.tho.cocook.util.db.CoCookDatabaseHelper;
import rup.tho.cocook.util.db.FilterTextWatcher;

public class IngredientToFridgeActivity extends AppCompatActivity implements NewIngDialogFragment.Listener {

    private TextInputEditText mEtIngredient;
    private TextInputLayout mTiIng;
    private TextInputEditText mEtAmount;
    private TextInputLayout mTiAmount;
    private Spinner mSpinnerUnit;
    private ListView mListView;

    private FilterTextWatcher mFilterTextWatcher;
    private ArrayAdapter<Ingredient> mArrayAdapter;
    private CoCookDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_to_fridge);

        mEtIngredient = (TextInputEditText) findViewById(R.id.etIngredient);
        mEtAmount = (TextInputEditText) findViewById(R.id.etAmount);
        mSpinnerUnit = (Spinner) findViewById(R.id.spinnerUnit);
        mListView = (ListView) findViewById(R.id.listIng);
        mFilterTextWatcher = new FilterTextWatcher(mListView);
        mTiIng = (TextInputLayout) findViewById(R.id.tiIng);
        mTiAmount = (TextInputLayout) findViewById(R.id.tiAmount);

        ArrayAdapter<Unit> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(getHelper().getUnits()));
        mSpinnerUnit.setAdapter(adapter);
        initializeSearchBar();
        initializeListView();

        // check if editing mode
        Intent intent = getIntent();
        String ingName = intent.getStringExtra(Ingredient.INGREDIENT_NAME_FIELD_NAME);
        int amount = intent.getIntExtra(Ingredient.INGREDIENT_AMOUNT_FIELD_NAME, -1);
        long unitId = intent.getLongExtra(Ingredient.INGREDIENT_UNIT_FIELD_NAME, -1);

        if(ingName == null || amount == -1 || unitId == -1) {
            return; // no extras
        }

        mEtIngredient.setText(ingName);
        mEtIngredient.setEnabled(false);
        mEtAmount.setText(String.valueOf(amount));
        mSpinnerUnit.setSelection(adapter.getPosition(getHelper().getUnitForId(unitId)));
    }

    private void initializeListView() {
        try {
            mArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,
                    new ArrayList<Ingredient>(getHelper().getIngredientsNotInFridgeSorted()));
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        mListView.setAdapter(mArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ingredient ing = (Ingredient) mListView.getItemAtPosition(position);
                mEtIngredient.setText(ing.getName());
                mEtIngredient.setSelection(mEtIngredient.getText().length());
                mListView.setVisibility(View.GONE);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Ingredient ing = (Ingredient) mListView.getItemAtPosition(position);
                Snackbar.make(parent, getString(R.string.dos_donts_delete_ing_question), Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.dos_donts_delete_answer), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getHelper().deleteIngredientFromPref(ing.getId());
                                mArrayAdapter.remove(ing);
                                mFilterTextWatcher.onTextChanged(mEtIngredient.getText().toString(), 0,
                                        mEtIngredient.getText().length(), mEtIngredient.getText().length());
                            }
                        }).setActionTextColor(ContextCompat.getColor(IngredientToFridgeActivity.this, R.color.bt_pressed_color)).show();
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_to_fridge, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else {
            String ingName = mEtIngredient.getText().toString().trim();
            String textAmount = mEtAmount.getText().toString().trim();
            if (validateInput(ingName, textAmount)) {
                try {
                    Ingredient ing = dbHelper.getIngredientForName(ingName);
                    if (ing == null) {
                        NewIngDialogFragment newIngDlg = new NewIngDialogFragment();
                        Bundle args = new Bundle();
                        args.putString(NewIngDialogFragment.ING_ARG, ingName);
                        newIngDlg.setArguments(args);
                        newIngDlg.setListener(this);
                        newIngDlg.show(getFragmentManager(), "NewIngDlg");
                    } else {
                        storeInFridge(ing);
                        NavUtils.navigateUpFromSameTask(this);
                        return true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void storeInFridge(Ingredient ing) throws SQLException {
        ing.setUnit((Unit) mSpinnerUnit.getSelectedItem());
        ing.setAmount(Integer.valueOf(mEtAmount.getText().toString()));
        getHelper().updateIngredient(ing);
    }

    private boolean validateInput(String textIng, String textAmount) {
        boolean isOkay = true;
        // so that the error message doesnt disappear instantly (because of the textInput holding focus)
        // let the layout get the focus here
        (findViewById(R.id.mainLayout)).requestFocus();

        if (textIng.length() <= 0) {
            mTiIng.setErrorEnabled(true);
            mTiIng.setError(getString(R.string.add_to_fridge_ing_empty));
            isOkay = false;
        } else {
            mTiIng.setErrorEnabled(false);

        }
        if (textAmount.length() <= 0) {
            mTiIng.setErrorEnabled(true);
            mTiAmount.setError(getString(R.string.add_to_fridge_amount_empty));
            isOkay = false;
        } else {
            mTiAmount.setErrorEnabled(false);
        }
        return isOkay;
    }


    private void initializeSearchBar() {
        mFilterTextWatcher = new FilterTextWatcher(mListView);
        mEtIngredient.addTextChangedListener(mFilterTextWatcher);

        mEtIngredient.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mListView.setVisibility(View.VISIBLE);
                } else {
                    mListView.setVisibility(View.GONE);
                }
            }
        });
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
    public void createNewIngredient() {
        Ingredient ing = new Ingredient(mEtIngredient.getText().toString(),
                Integer.valueOf(mEtAmount.getText().toString()),
                (Unit) mSpinnerUnit.getSelectedItem());
        getHelper().getIngDao().create(ing);
        NavUtils.navigateUpFromSameTask(this);
    }
}
