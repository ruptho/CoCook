package rup.tho.cocook.letscook.cook;

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
public class RemoveFromFridgeDialogFragment extends DialogFragment {

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    interface Listener {
        void removeFromFridge();
    }

    public RemoveFromFridgeDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity act = getActivity();

        return new AlertDialog.Builder(act)
                .setTitle(R.string.dlg_remove_from_fridge_title)
                .setMessage(R.string.dlg_remove_from_fridge_question)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.removeFromFridge();
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        act.finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
