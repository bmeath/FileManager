package com.bmeath.filemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by bm on 25/04/17.
 *
 * For viewing properties of a file/folder
 */

public class FilePropsDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        HashMap<String, String> props;
        Bundle args = getArguments();
        View v;
        AlertDialog.Builder builder;
        String msg;

        v = View.inflate(getActivity(), R.layout.file_props, null);

        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Properties");

        props = FileHelpers.getProperties(getContext(), args.getString("path"));

        msg = "";
        for (LinkedHashMap.Entry<String, String> entry : props.entrySet()) {
           msg += entry.getKey() + ": \t" + entry.getValue() + "\n";
        }

        builder.setMessage(msg);
        builder.setView(v);
        builder.setPositiveButton("OK", this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dismiss();
    }
}
