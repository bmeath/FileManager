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
import android.widget.Toast;

import java.io.File;

/**
 * Created by bm on 23/04/17.
 */

public class RenameDialogFragment extends DialogFragment
{
    private static final String[] ILLEGAL_CHARS = {"\\", "/", ":", "*", "?", "'", "<", ">", "|"};
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
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        newName = nameInput.getText().toString();
                        File newFile = new File(f.getParent() + File.separator + newName);

                        if (isValidFilename(newName, ILLEGAL_CHARS))
                        {
                            if (!newFile.exists() && !newName.equals(oldName))
                            {
                                f.renameTo(newFile);
                                dismiss();
                            }
                        }
                        Toast.makeText(getContext(), "Invalid filename!", Toast.LENGTH_SHORT).show();
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

    public boolean isValidFilename(String s, String[] illegalChars)
    {
        for (int i = 0; i < illegalChars.length; i ++)
        {
            if (s.contains(illegalChars[i]))
            {
                return false;
            }
        }
        if (s.charAt(s.length()-1) == '.')
        {
            return false;
        }
        return true;
    }


}
