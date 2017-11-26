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

public class FileRenameDialogFragment extends DialogFragment implements DialogInterface.OnClickListener
{

    private String newName;
    private EditText nameInput;
    private File f;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle args = getArguments();

        f = new File(args.getString("path"));

        View v = View.inflate(getActivity(), R.layout.file_rename, null);

        nameInput = (EditText) v.findViewById(R.id.renameEditText);
        nameInput.setText(f.getName());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Rename");
        builder.setMessage("Enter new name:");
        builder.setView(v);
        builder.setPositiveButton("OK", this);
        builder.setNegativeButton("CANCEL", this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i) {
            case android.content.DialogInterface.BUTTON_POSITIVE:
                newName = nameInput.getText().toString();
                File newFile = new File(f.getParent() + File.separator + newName);

                if (FileHelpers.isValidFilename(newName)) {
                    if (!newFile.exists()) {
                        if (!f.renameTo(newFile)) {
                            Toast.makeText(getContext(), "Failed to rename file!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Invalid filename!", Toast.LENGTH_SHORT).show();
                }
        }
        dismiss();
    }
}
