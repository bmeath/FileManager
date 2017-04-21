package com.bmeath.filemanager;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    private String path = "/";
    String[] currentDirList;
    ArrayList contents;
    Boolean showHidden = false;
    ArrayAdapter<String> arrayAdapter;
    FileAdapter fileAdapter;
    private ListView lView;
    //private Toolbar tBar;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lView = (ListView) findViewById(R.id.lView);

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

        // instantiate custom adapter
        fileAdapter = new FileAdapter(this, contents);
        lView.setAdapter(fileAdapter);
        lView.setOnItemClickListener(MainActivity.this);
    }

    public void onItemClick(AdapterView<?> adapterView, View v, int position, long id)
    {
        String fname = (String) fileAdapter.getItem(position);
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