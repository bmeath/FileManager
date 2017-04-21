package com.bmeath.filemanager;

import android.app.Activity;
import android.content.Context;
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

public class FileAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<String> files;
    public static int[] icons = {R.drawable.folder_icon, R.drawable.file_icon};
    private String path;

    public FileAdapter(Context context, ArrayList<String> files, String path) {
        this.context = context;
        this.files = files;
        this.path = path;
    }

    @Override
    public int getCount()
    {
        return files.size();
    }

    @Override
    public Object getItem(int position)
    {
        return files.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_file, null);
        }

        TextView fileText = (TextView) convertView.findViewById(R.id.fileText);
        ImageView fileIcon = (ImageView) convertView.findViewById(R.id.fileIcon);

        String title = files.get(position);
        fileText.setText(title);

        if (new File(path + "/" + title).isDirectory())
        {
            fileIcon.setImageResource(icons[0]);
        }
        else
        {
            fileIcon.setImageResource(icons[1]);
        }

        return convertView;
    }
}
