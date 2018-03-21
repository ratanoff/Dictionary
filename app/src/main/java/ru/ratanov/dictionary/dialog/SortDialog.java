package ru.ratanov.dictionary.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import ru.ratanov.dictionary.MainActivity;
import ru.ratanov.dictionary.R;
import ru.ratanov.dictionary.db.WordDbSchema.WordTable.Cols;
import ru.ratanov.dictionary.util.QueryPreferences;

public class SortDialog extends DialogFragment {

    private RadioGroup mSortRadioGroup;
    private RadioGroup mOrderRadioGroup;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dilaog_sort, null);
        mSortRadioGroup = view.findViewById(R.id.sort_radioGroup);
        mOrderRadioGroup = view.findViewById(R.id.order_radioGroup);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updatePreferences();
                ((MainActivity) getActivity()).updateUI();
            }
        });

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        initRadioGroups();
    }

    private void initRadioGroups() {
        String filter = QueryPreferences.getStoredQuery(getContext(), "sort", Cols.TITLE);
        String order = QueryPreferences.getStoredQuery(getContext(), "order", " ASC");

        if (filter.equals(Cols.TITLE)) {
            mSortRadioGroup.check(R.id.option_by_word);
        } else if (filter.equals(Cols.TRANSLATE)) {
            mSortRadioGroup.check(R.id.option_by_translate);
        }

        if (order.equals(" ASC")) {
            mOrderRadioGroup.check(R.id.option_asc);
        } else if (order.equals(" DESC")) {
            mOrderRadioGroup.check(R.id.option_desc);
        }
    }

    private void updatePreferences() {

        switch (mSortRadioGroup.getCheckedRadioButtonId()) {
            case R.id.option_by_word:
                QueryPreferences.setStoredQuery(getContext(), "sort", Cols.TITLE);
                break;
            case R.id.option_by_translate:
                QueryPreferences.setStoredQuery(getContext(), "sort", Cols.TRANSLATE);
                break;
        }

        switch (mOrderRadioGroup.getCheckedRadioButtonId()) {
            case R.id.option_asc:
                QueryPreferences.setStoredQuery(getContext(), "order", " ASC");
                break;
            case R.id.option_desc:
                QueryPreferences.setStoredQuery(getContext(), "order", " DESC");
                break;
        }
    }
}
