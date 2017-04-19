package com.bmeath.filemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by bm on 15/04/17.
 */

public class MainActivity extends AppCompatActivity
{

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListFiles();
    }

    public void ListFiles()
    {
        Intent intent = new Intent(this, FileListActivity.class);
        startActivity(intent);
    }
}