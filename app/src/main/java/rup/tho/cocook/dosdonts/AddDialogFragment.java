package rup.tho.cocook.dosdonts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;

import rup.tho.cocook.R;
import rup.tho.cocook.data.Ingredient;
import rup.tho.cocook.util.db.CoCookDatabaseHelper;
import rup.tho.cocook.util.db.FilterTextWatcher;


/**
 * Created by root on 05/06/15.
 */
public class AddDialogFragment extends DialogFragment {
    public final static String TAG = "fragment_add_preference";
    private EditText filterText;
    private ListView listView;
    private TextView tvHeader;
    private CoCookDatabaseHelper dbHelper;
    private Listener mListener;
    private FilterTextWatcher filterTextWatcher;
    private ArrayAdapter<Ingredient> adapter;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    interface Listener {
        void returnFromDlg();
    }

    public AddDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity act = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final boolean isDo = getArguments().getBoolean("is_do");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View parent = inflater.inflate(R.layout.dlg_add_dos_donts, null);
        builder.setView(parent)
                // Add action buttons
                .setNeutralButton(R.string.dos_donts_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cancelDlg();
                    }
                });
        filterText = (EditText) parent.findViewById(R.id.search_box);
        listView = (ListView) parent.findViewById(R.id.listIng);
        tvHeader = (TextView) parent.findViewById(R.id.tvHeader);

        tvHeader.setText(isDo ?
                act.getResources().getString(R.string.menu_dos_donts_add_do)
                : act.getResources().getString(R.string.menu_dos_donts_add_dont));

        initializeSearchComponents(parent, isDo);

        return builder.create();
    }

    private void initializeSearchComponents(View parent, boolean isDo) {
        initializeListView(isDo);
        initializeSearchBar(parent);
    }

    private void initializeSearchBar(final View parent) {
        filterTextWatcher = new FilterTextWatcher(listView);
        filterText.addTextChangedListener(filterTextWatcher);


        parent.findViewById(R.id.btNewIng).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String ingText = filterText.getText().toString();
                    String text;
                    if (ingText != null && ingText.length() > 0) {
                        Ingredient ing = new Ingredient(ingText);
                        ing = getHelper().addIngredient(ing);

                        if (ing != null) {
                            text = getActivity().getString(R.string.dos_donts_ing_added);
                            adapter.add(ing);
                            filterTextWatcher.onTextChanged(filterText.getText().toString(), 0, filterText.getText().length(), filterText.getText().length());
                        } else {
                            text = getActivity().getString(R.string.dos_donts_ing_existing);
                        }
                    } else {
                        text = getActivity().getString(R.string.dos_donts_empty_ing);
                    }

                    Snackbar.make(parent, text, Snackbar.LENGTH_SHORT)
                            .setAction(getActivity().getResources().getString(R.string.dos_donts_dismiss), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // auto dismiss
                                }
                            }).setActionTextColor(ContextCompat.getColor(getActivity(), R.color.bt_pressed_color)).show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initializeListView(final boolean isDo) {
        adapter = new ArrayAdapter<Ingredient>(this.getActivity(),
                android.R.layout.simple_list_item_1,
                new ArrayList<Ingredient>(getHelper().getNotPreferencedIngredients()));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ingredient.PrefVal pref = isDo ? Ingredient.PrefVal.POS : Ingredient.PrefVal.NEG;
                Ingredient ing = (Ingredient) listView.getItemAtPosition(position);
                ing.setPreference(pref);
                try {
                    getHelper().addIngredientToPref(ing);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (mListener != null) {
                    mListener.returnFromDlg();
                }
                cancelDlg();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Ingredient ing = (Ingredient) listView.getItemAtPosition(position);

                Snackbar.make(parent, getActivity().getResources().getString(R.string.dos_donts_delete_ing_question), Snackbar.LENGTH_SHORT)
                        .setAction(getActivity().getResources().getString(R.string.dos_donts_delete_answer), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getHelper().deleteIngredientFromPref(ing.getId());
                                adapter.remove(ing);
                                filterTextWatcher.onTextChanged(filterText.getText().toString(), 0,
                                        filterText.getText().length(), filterText.getText().length());
                            }
                        }).setActionTextColor(ContextCompat.getColor(getActivity(), R.color.bt_pressed_color)).show();
                return true;
            }
        });
    }


    protected CoCookDatabaseHelper getHelper() {
        if (dbHelper == null) {
            dbHelper = OpenHelperManager.getHelper(getActivity(), CoCookDatabaseHelper.class);
        }
        return dbHelper;
    }

    private void cancelDlg() {
        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
        AddDialogFragment.this.getDialog().cancel();
    }

}
