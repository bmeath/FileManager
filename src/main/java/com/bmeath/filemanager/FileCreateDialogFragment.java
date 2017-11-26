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

public class FileCreateDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private EditText nameInput;
    private String newName;
    private String path;

    private FileType newFileType;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();

        path = args.getString("path");

        newFileType = (FileType) args.getSerializable("mode");

        View v = View.inflate(getActivity(), R.layout.file_rename, null);
        nameInput = (EditText) v.findViewById(R.id.renameEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (newFileType) {
            case REG:
                builder.setTitle("New file");
                builder.setMessage("File will be created in " + path);
                break;
            case DIR:
                builder.setTitle("New folder");
                builder.setMessage("Folder will be created in " + path);
                break;
        }
        builder.setView(v);
        builder.setPositiveButton("OK", this);
        builder.setNegativeButton("CANCEL", this);
        return builder.create();
    }

    private void errorToast() {
        Toast.makeText(getContext(), "Failed to create new file/folder", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i)
        {
            case android.content.DialogInterface.BUTTON_POSITIVE:
                newName = nameInput.getText().toString();
                if (FileHelpers.isValidFilename(newName)) {
                    File f = new File(path + File.separator + newName);

                    switch (newFileType) {
                        case REG:
                            try {
                                if (f.createNewFile()) {
                                    Toast.makeText(getContext(), "New file created", Toast.LENGTH_SHORT).show();
                                } else {
                                    errorToast();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case DIR:
                            if (f.mkdir()) {
                                Toast.makeText(getContext(), "New folder created", Toast.LENGTH_SHORT).show();
                            } else {
                                errorToast();
                            }
                            break;
                    }
                    dismiss();
                } else {
                    errorToast();
                }
                break;
        }
    }
}
