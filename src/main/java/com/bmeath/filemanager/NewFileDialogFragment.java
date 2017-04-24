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
import java.io.IOException;

/**
 * Created by bm on 24/04/17.
 *
 * A dialog which takes input and creates a new file/folder
 *
 */

public class NewFileDialogFragment extends DialogFragment
{

    private static final String[] MODES = {"file", "folder"};
    private EditText nameInput;
    private File f;
    private String newName;
    private String mode;


    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle args = getArguments();

        f = new File(args.getString("path"));

        // taking a mode parameter instead of duplicating this code for folder creation
        mode = args.getString("mode");

        // check that either file creation or folder creation has been specified
        if (!isValidMode(mode))
        {
            return null;
        }

        View v = View.inflate(getActivity(), R.layout.file_rename, null);
        nameInput = (EditText) v.findViewById(R.id.renameEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter new " + mode + " name:");
        builder.setView(v);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                newName = nameInput.getText().toString();
                File newFile = new File(f.getParent() + File.separator + newName);

                if (FileHelpers.isValidFilename(newName))
                {
                        if (mode == "file")
                        {
                            try
                            {
                                if (f.createNewFile())
                                {
                                    dismiss();
                                }
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else // folder
                        {
                            if (f.mkdir())
                            {
                                dismiss();
                            }
                        }
                }
                Toast.makeText(getContext(), "Failed to create " + mode, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dismiss();
            }
        });
        return builder.create();
    }

    private boolean isValidMode(String m)
    {
        for (int i = 0; i < MODES.length; i++)
        {
            if (m == MODES[i])
            {
                return true;
            }
        }
        return false;
    }
}
