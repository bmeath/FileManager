package com.bmeath.filemanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by bm on 15/04/17.
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    private String path;
    String[] currentDirList;
    private static String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    ArrayList contents;
    Boolean showHidden = false;
    FileAdapter fileAdapter;
    private ListView lView;
    File currentDir;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            for (int i = 0; i < permissions.length; i++)
            {
                int havePermission = ContextCompat.checkSelfPermission(this, permissions[i]);
                if (havePermission == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, new String[]{permissions[i]}, 0);
                }
            }

        }

        lView = (ListView) findViewById(R.id.lView);

        // set title to path
        path = Environment.getExternalStorageDirectory().getAbsolutePath();

        if (getIntent().hasExtra("path"))
        {
            path = getIntent().getStringExtra("path");
        }

        setTitle(path);

        // get names of current directory contents
        currentDir = new File(path);

        if (currentDir.canRead())
        {
            currentDirList = currentDir.list();

            // convert string array to arraylist
            if (currentDirList != null) {
                if (showHidden) {
                    contents = new ArrayList(Arrays.asList(currentDirList));
                } else {
                    contents = new ArrayList();
                    // exclude hidden items
                    for (int i = 0; i < currentDirList.length; i++) {
                        if (!currentDirList[i].startsWith(".")) {
                            contents.add(currentDirList[i]);
                        }
                    }
                }
            }

            // sort alphabetically
            Collections.sort(contents);

            // link to file names to ListView using FileAdapter
            fileAdapter = new FileAdapter(this, contents, path);
            lView.setAdapter(fileAdapter);
            lView.setOnItemClickListener(MainActivity.this);
        }
        else
        {
            setTitle(path + " (unreachable)");
        }
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