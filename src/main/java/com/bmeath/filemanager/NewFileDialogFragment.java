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
    private String newName;
    private String mode;


    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle args = getArguments();

        final String path = args.getString("path");

        // taking a mode parameter instead of duplicating this class for folder creation
        mode = args.getString("mode");

        // check that either file creation or folder creation has been specified
        if (!isValidMode(mode))
        {
            return null;
        }

        View v = View.inflate(getActivity(), R.layout.file_namer, null);
        nameInput = (EditText) v.findViewById(R.id.renameEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New " + mode);
        builder.setMessage(mode + " will be created in " + path);
        builder.setView(v);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                newName = nameInput.getText().toString();
                if (FileHelpers.isValidFilename(newName))
                {
                    File f = new File(path + File.separator + newName);
                    if (mode == "file")
                        {
                            try
                            {
                                if (f.createNewFile())
                                {
                                    Toast.makeText(getContext(), "New " + mode + " created", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                                else
                                {
                                    errorToast();
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
                                Toast.makeText(getContext(), "New " + mode + " created", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                            else
                            {
                                errorToast();
                            }
                        }
                }
                else
                {
                    errorToast();
                }
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

    private void errorToast()
    {
        Toast.makeText(getContext(), "Failed to create " + mode, Toast.LENGTH_SHORT).show();
    }
}
