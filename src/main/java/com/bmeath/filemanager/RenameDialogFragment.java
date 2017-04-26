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
 * Created by bm on 23/04/17.
 */

public class RenameDialogFragment extends DialogFragment
{

    private String newName;
    private EditText nameInput;
    private File f;

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle args = getArguments();

        f = new File(args.getString("path"));
        final String oldName = f.getName();

        View v = View.inflate(getActivity(), R.layout.file_namer, null);
        nameInput = (EditText) v.findViewById(R.id.renameEditText);
        nameInput.setText(oldName);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Rename");
        builder.setMessage("Enter new name for " + oldName);
        builder.setView(v);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        newName = nameInput.getText().toString();
                        File newFile = new File(f.getParent() + File.separator + newName);

                        if (FileHelpers.isValidFilename(newName))
                        {
                            if (!newFile.exists() && !newName.equals(oldName) && newName.length() < 256)
                            {
                                if (f.renameTo(newFile))
                                {
                                    Toast.makeText(getContext(), "Renamed file", Toast.LENGTH_SHORT).show();
                                }
                                dismiss();
                            }
                            else
                            {
                                Toast.makeText(getContext(), "Failed to rename file!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Invalid filename!", Toast.LENGTH_SHORT).show();
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
}
