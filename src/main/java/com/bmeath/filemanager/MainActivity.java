package com.bmeath.filemanager;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by bm on 15/04/17.
 */

public class MainActivity extends AppCompatActivity
{
    private String path = "/";
    String[] currentDirList;
    ArrayList contents;
    Boolean showHidden = false;
    ArrayAdapter<String> arrayAdapter;
    private ListView lView;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lView = (ListView) findViewById(R.id.list);

        // set title to path
        path = "/";
        if (getIntent().hasExtra("path"))
        {
            path = getIntent().getStringExtra("path");
        }
        setTitle(path);

        // get names of current directory contents
        currentDirList = new File(path).list();

        // convert string array to arraylist
        if (currentDirList != null)
        {
            if (showHidden)
            {
                contents = new ArrayList(Arrays.asList(currentDirList));
            }
            else
            {
                contents = new ArrayList();
                // exclude hidden items
                for (int i = 0; i < currentDirList.length; i++)
                {
                    if (!currentDirList[i].startsWith("."))
                    {
                        contents.add(currentDirList[i]);
                    }
                }
            }
        }

        // sort alphabetically
        Collections.sort(contents);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, contents);
        setListAdapter(arrayAdapter);
    }

    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        String fname = (String) getListAdapter().getItem(position);
        if (path.endsWith(File.separator))
        {
            fname = path + fname;
        }
        else
        {
            fname = path + File.separator + fname;
        }
        
        if (new File(fname).isDirectory())
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("path", fname);
            startActivity(intent);
        }
    }
}