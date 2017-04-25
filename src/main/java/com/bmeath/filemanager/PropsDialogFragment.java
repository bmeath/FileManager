package com.bmeath.filemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

/**
 * Created by bm on 25/04/17.
 *
 * For viewing properties of a file/folder
 */

public class PropsDialogFragment extends DialogFragment
{
    private File f;

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle args = getArguments();

        f = (File) args.getSerializable("file");

        View v = View.inflate(getActivity(), R.layout.file_props, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Properties");

        String msg = "Name: " + f.getName() + "\n";
        if (f.isDirectory())
        {
            msg += "Type: folder\n";
        }
        else
        {
            msg += "Type: file\n";
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
