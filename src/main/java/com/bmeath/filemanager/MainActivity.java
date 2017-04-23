package com.bmeath.filemanager;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by bm on 15/04/17.
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{
    private File[] currentDirList;
    private static String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static MimeTypeMap mime = MimeTypeMap.getSingleton();

    private ArrayList contents;
    private ArrayList contentsFiles;
    private ArrayList contentsFolders;

    Boolean showHidden = true;
    FileAdapter fileAdapter;
    private ListView lView;
    File currentDir;
    String parent;
    Intent fileViewIntent = new Intent(Intent.ACTION_VIEW);

    String clipboard;
    MenuItem pasteOption;
    boolean deleteAfterPaste;
    int selectedMem;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tBar = (Toolbar) findViewById(R.id.tBar);
        setSupportActionBar(tBar);

        // check if run-time permission requesting should be done
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
        lView.setOnItemClickListener(this);
        lView.setOnItemLongClickListener(this);
        lView.setOnCreateContextMenuListener(this);



        // set title to path
        cd(Environment.getExternalStorageDirectory().getAbsolutePath());
        ls();
    }
    public boolean onPrepareOptionsMenu(Menu m)
    {
        if (clipboard == null) {
            pasteOption.setVisible(false);
        }
        else
        {
            pasteOption.setVisible(true);
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu m)
    {
        getMenuInflater().inflate(R.menu.menu_main, m);
        pasteOption = m.findItem(R.id.paste);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.paste)
        {
            String src = clipboard;
            String dst;
            clipboard = null;

            try
            {
                dst = currentDir.getCanonicalPath() + File.separator + new File(src).getName();

                if (deleteAfterPaste)
                {
                    mv(src, dst);
                    Toast.makeText(this, "Moving items...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    cp(src, dst);
                    Toast.makeText(this, "Copying items...", Toast.LENGTH_SHORT).show();
                }

                invalidateOptionsMenu();
                ls();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        return true;
    }

    public void onItemClick(AdapterView<?> adapterView, View v, int position, long id)
    {
        open((File) fileAdapter.getItem(position));
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        selectedMem = position;
        registerForContextMenu(lView);
        openContextMenu(lView);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        if (v.getId() == R.id.lView)
        {
            String[] options = getResources().getStringArray(R.array.long_click_menu);
            for (int i = 0; i < options.length; i++)
            {
                menu.add(Menu.NONE, i, i, options[i]);
            }
        }
    }

    public boolean onContextItemSelected(MenuItem option) {
        String[] options = getResources().getStringArray(R.array.long_click_menu);
        File f = (File) fileAdapter.getItem(selectedMem);

        switch(option.getItemId())
        {
            case 0: // open
                open(f);
                break;
            case 1: // cut
                try
                {
                    clipboard = f.getCanonicalPath();
                    deleteAfterPaste = true;
                    invalidateOptionsMenu();
                }
                catch (IOException e)
                {
                    Toast.makeText(this, "Failed to select file for cut operation", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2: // copy
                try
                {
                    clipboard = f.getCanonicalPath();
                    deleteAfterPaste = false;
                    invalidateOptionsMenu();
                }
                catch (IOException e)
                {
                    Toast.makeText(this, "Failed to select file for copy operation", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3: // delete
                try
                {
                    rm(f.getCanonicalPath());
                    Toast.makeText(this, "Items deleted successfully", Toast.LENGTH_SHORT).show();
                    ls();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
            case 4: // rename
                break;
            case 5: //properties
                break;
            default:

        }
        return true;
    }

    private void open(File f)
    {
        if (f.isDirectory())
        {
            if (f.getName().equals(".."))
            {
                cd(currentDir.getParent());
                ls();
            }
            else
            {
                cd(f.getAbsolutePath());
                ls();
            }
        }
        else
        {
            openFile(f);
        }
    }

    private void openFile(File f)
    {
        // get mimetype from extension extracted from filename
        String ext = f.getName();
        String mimeType = mime.getMimeTypeFromExtension(ext.substring(ext.lastIndexOf(".")).toLowerCase());

        fileViewIntent.setDataAndType(Uri.fromFile(f), mimeType);
        fileViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try
        {
            startActivity(fileViewIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(this, "No applications were found for this type of file.", Toast.LENGTH_SHORT).show();
        }
    }

    private void cd(String newPath)
    {
        if (newPath.equals("../"))
        {
            currentDir = currentDir.getParentFile();
        }
        else
        {
            currentDir = new File(newPath);
        }
        parent = currentDir.getParent();
        setTitle(newPath);
    }

    private void ls()
    {
        // get names of current directory contents
        contents = new ArrayList();
        contentsFiles = new ArrayList();


        if (currentDir.canRead())
        {
            currentDirList = currentDir.listFiles();

            // convert string array to arraylist
            if (currentDirList != null)
            {
                // exclude hidden items
                for (int i = 0; i < currentDirList.length; i++)
                {
                    if ((currentDirList[i].isHidden() && showHidden) || !currentDirList[i].isHidden())
                    {
                        // keep files separate from folders for sorting purposes
                        if (currentDirList[i].isDirectory())
                        {
                            contents.add(currentDirList[i]);
                        }
                        else
                        {
                            contentsFiles.add(currentDirList[i]);
                        }
                    }
                }
            }

            // sort alphabetically
            Collections.sort(contents);
            Collections.sort(contentsFiles);
            // now append files to folders
            contents.addAll(contentsFiles);



            if (parent != null)
            {
                contents.add(0, new File("../"));
            }
        }
        else
        {
            contents.add(0, new File("../"));
        }

        // link file names to ListView using FileAdapter
        fileAdapter = new FileAdapter(this, contents);
        lView.setAdapter(fileAdapter);
        registerForContextMenu(lView);
    }

    private boolean mkdir(String title)
    {
        return false;
    }

    private boolean mkFile()
    {
        return false;
    }

    private boolean rm(String path)
    {
        File f = new File(path);
        if (f.isDirectory())
        {
            String[] files = f.list();
            for (int i = 0; i < files.length; i++)
            {
                try
                {
                    rm(new File(f, files[i]).getCanonicalPath());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            // all sub-items have been deleted, now delete the empty folder
            f.delete();
        }
        else
        {
            f.delete();
            return true;
        }
        return false;
    }

    private boolean mv(String srcPath, String dstPath)
    {
        if (cp(srcPath, dstPath))
        {
            rm(srcPath);
            return true;
        }
        return false;
    }

    private boolean cp(String srcPath, String dstPath)
    {
        Intent copyIntent = new Intent(this, IOService.class);
        copyIntent.putExtra("SRC_PATH", srcPath);
        copyIntent.putExtra("DST_PATH", dstPath);
        copyIntent.putExtra("MODE", "COPY");
        startService(copyIntent);
        /*
         * TODO: get result of copy operation from service
         */
        return true;
    }
}