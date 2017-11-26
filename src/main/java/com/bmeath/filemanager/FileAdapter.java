package com.bmeath.filemanager;

import android.app.Activity;
import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bm on 21/04/17.
 */

public class FileAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<File> files;

    public FileAdapter(Context context, ArrayList<File> files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.file_row, parent, false);
        }

        TextView fileText = (TextView) convertView.findViewById(R.id.fileText);
        ImageView fileIcon = (ImageView) convertView.findViewById(R.id.fileIcon);
        TextView fileSize = (TextView) convertView.findViewById(R.id.fileSize);

        File f = files.get(position);
        fileText.setText(f.getName());

        if (f.isFile()) {
            fileIcon.setImageResource(R.drawable.file_icon);
            fileSize.setText(Formatter.formatShortFileSize(context, f.length()));
        } else {
            fileIcon.setImageResource(R.drawable.folder_icon);
            fileSize.setText("");
        }

        return convertView;
    }
}
