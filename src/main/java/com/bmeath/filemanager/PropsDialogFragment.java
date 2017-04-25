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

public class PropsDialogFragment extends DialogFragment
{
    private HashMap<String, String> props;
    private String msg = "";

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle args = getArguments();

        View v = View.inflate(getActivity(), R.layout.file_props, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Properties");

        props = FileHelpers.getProperties(getContext(), args.getString("path"));

        for (LinkedHashMap.Entry<String, String> entry : props.entrySet())
        {
           msg += entry.getKey() + ": " + entry.getValue() + "\n";
        }

        builder.setMessage(msg);
        builder.setView(v);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dismiss();
            }
        });
        return builder.create();
    }

}