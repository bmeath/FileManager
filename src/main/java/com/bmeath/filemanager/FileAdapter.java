package com.bmeath.filemanager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bm on 21/04/17.
 */

public class FileAdapter extends BaseAdapter
{
    private static final String LOG_TAG = FileAdapter.class.getSimpleName();

    private Context context_;
    private ArrayList<String> files;

    public FileAdapter(Context context, ArrayList<String> files) {
        this.context_ = context;
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
            LayoutInflater mInflater = (LayoutInflater)
                    context_.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.list_file, null);
        }

        TextView fileText = (TextView) convertView.findViewById(R.id.fileText);
        ImageView fileIcon = (ImageView) convertView.findViewById(R.id.fileIcon);

        String title = files.get(position);

        //Log.d(LOG_TAG,"File: " + file + " fileIcon: " + icon);

        fileText.setText(title);
        fileIcon.setImageURI();

        return convertView;
    }
}
