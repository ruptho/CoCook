package rup.tho.cocook.fridge.ingtofridge;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import rup.tho.cocook.R;

/**
 * Created by thorsten on 5/29/16.
 */
public class NewIngDialogFragment extends DialogFragment {

    private Listener mListener;
    public static final String ING_ARG = "ing_name";
    public void setListener(Listener listener) {
        mListener = listener;
    }

    interface Listener {
        void createNewIngredient();
    }

    public NewIngDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity act = getActivity();
        String ingName = getArguments().getString(ING_ARG);

        return new AlertDialog.Builder(act)
                .setTitle(act.getString(R.string.dlg_new_ing_title))
                .setMessage(act.getString(R.string.dlg_new_ing_question, ingName))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.createNewIngredient();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
