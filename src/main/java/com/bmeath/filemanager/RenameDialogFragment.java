package com.bmeath.filemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.File;

/**
 * Created by bm on 23/04/17.
 */

public class RenameDialogFragment extends DialogFragment
{
    private EditText nameInput;
    private File f;
    private String newName;

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle args = getArguments();

        f = new File(args.getString("path"));
        final String oldName = f.getName();

        View v = View.inflate(getActivity(), R.layout.file_rename, null);
        nameInput = (EditText) v.findViewById(R.id.renameEditText);
        nameInput.setText(oldName);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Rename");
        builder.setView(v);
        builder.setPositiveButton("APPLY", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        newName = nameInput.getText().toString();
                        File newFile = new File(f.getParent() + File.separator + newName);
                        if (!newFile.exists() && newName != oldName)
                        {
                            f.renameTo(newFile);
                        }
                        dismiss();
                    }
                });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                    }
                });
        return builder.create();
    }

}
