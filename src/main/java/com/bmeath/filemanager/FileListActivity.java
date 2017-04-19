package com.bmeath.filemanager;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.io.File;

/**
 * Created by bm on 15/04/17.
 */

public class FileListActivity extends ListActivity {

    private String path;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);

        // set title to path
        path = "/";
        if (getIntent().hasExtra("path"))
        {
            path = getIntent().getStringExtra("path");
        }
        setTitle(path);

    }
}
