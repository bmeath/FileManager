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
import java.text.DateFormat;
import java.util.ArrayList;

/**
 * Created by bm on 21/04/17.
 */

public class FileAdapter extends BaseAdapter
{
    private static int[] icons = {R.drawable.folder_icon, R.drawable.file_icon};
    private Context context;
    private ArrayList<File> files;

    public FileAdapter(Context context, ArrayList<File> files) {
        this.context = context;
        this.files = files;
    }

    public int getCount()
    {
        return files.size();
    }

    public Object getItem(int position)
    {
        return files.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.file_list, null);
        }

        TextView fileText = (TextView) convertView.findViewById(R.id.fileText);
        ImageView fileIcon = (ImageView) convertView.findViewById(R.id.fileIcon);
        TextView fileSize = (TextView) convertView.findViewById(R.id.fileSize);

        File f = files.get(position);
        fileText.setText(f.getName());

        if (f.isFile())
        {
            fileIcon.setImageResource(icons[1]);
            fileSize.setText(Formatter.formatShortFileSize(context, f.length()));
        }
        else
        {
            fileIcon.setImageResource(icons[0]);
            fileSize.setText("");
        }

        return convertView;
    }
}
