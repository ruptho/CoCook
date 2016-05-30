package rup.tho.cocook.util.db;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by thorsten on 5/29/16.
 */
public class FilterTextWatcher implements TextWatcher {
    private ListView listView;

    public FilterTextWatcher(ListView listView) {
        this.listView = listView;
    }

    public void afterTextChanged(Editable s) {
    }

    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before,
                              int count) {
        ((ArrayAdapter<String>) listView.getAdapter()).getFilter().filter(s);
    }

};