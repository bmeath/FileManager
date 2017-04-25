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
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by bm on 15/04/17.
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{
    private static final int REQUEST_CONSTANT = 1;
    private static String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static MimeTypeMap mime = MimeTypeMap.getSingleton();

    private ListView lView;

    private FileAdapter fileAdapter;
    private File currentDir;
    private String parent;
    private Boolean showHidden = true;

    // used to launch file viewing activities
    private Intent fileViewIntent = new Intent(Intent.ACTION_VIEW).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    private String clipboard;
    private MenuItem pasteOption;
    private boolean deleteAfterPaste;
    private int selectedMem;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar tBar = (Toolbar) findViewById(R.id.tBar);
        setSupportActionBar(tBar);

        getPermission();

        lView = (ListView) findViewById(R.id.lView);
        lView.setOnItemClickListener(this);
        lView.setOnItemLongClickListener(this);
        lView.setOnCreateContextMenuListener(this);

        // set current directory to external storage and list contents
        cd(Environment.getExternalStorageDirectory().getAbsolutePath());
        ls();
    }

    private void getPermission()
    {
        // check if run-time permission requesting should be done
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            for (int i = 0; i < permissions.length; i++)
            {
                int havePermission = ContextCompat.checkSelfPermission(this, permissions[i]);
                if (havePermission == PackageManager.PERMISSION_DENIED)
                {
                    ActivityCompat.requestPermissions(this, new String[]{permissions[i]}, REQUEST_CONSTANT);
                }
            }
        }
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

        if (id == R.id.newfile)
        {
            mkFile();
            ls();
        }

        if (id == R.id.newfolder)
        {
            mkdir();
            ls();
        }

        return true;
    }

    public void onItemClick(AdapterView<?> adapterView, View v, int position, long id)
    {
        File f = (File) fileAdapter.getItem(position);
        if (f.exists())
        {
            open(f);
        }
        else
        {
            fileMissingHandler();
        }
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        selectedMem = position;
        registerForContextMenu(lView);
        openContextMenu(lView);
        return true;
    }

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

    public boolean onContextItemSelected(MenuItem option)
    {
        String[] options = getResources().getStringArray(R.array.long_click_menu);

        File f = (File) fileAdapter.getItem(selectedMem);

        if (f.exists())
        {
            switch (option.getItemId())
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
                        Toast.makeText(this, "Deleting...", Toast.LENGTH_SHORT).show();
                        ls();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case 4: // rename
                    try
                    {
                        rename(f.getCanonicalPath());
                        ls();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case 5: //properties
                    break;
                default:

            }
        }
        else
        {
            fileMissingHandler();
        }
        return true;
    }

    private void open(File f)
    {
        if (f.isDirectory())
        {
            if (f.getName().equals(".."))
            {
                cd(parent);
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
        if (ext.lastIndexOf('.') != -1)
        {
            String mimeType = mime.getMimeTypeFromExtension(ext.substring(ext.lastIndexOf(".")).toLowerCase());

            fileViewIntent.setDataAndType(Uri.fromFile(f), mimeType);

            try
            {
                startActivity(fileViewIntent);
            }
            catch (ActivityNotFoundException e)
            {
                Toast.makeText(this, "No applications were found for this type of file.", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Can't open a file of unknown type", Toast.LENGTH_SHORT).show();
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
        ArrayList contents = new ArrayList();
        ArrayList contentsFiles = new ArrayList();


        if (currentDir.canRead())
        {
            File[] currentDirList = currentDir.listFiles();

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

    public boolean rename(String path)
    {
        Bundle args = new Bundle();
        args.putString("path", path);
        DialogFragment renameFragment = new RenameDialogFragment();
        renameFragment.setArguments(args);
        renameFragment.show(getSupportFragmentManager(), "rename");
        return true;
    }

    private boolean mkdir() {
        return startNewFileDialog("folder");
    }

    private boolean mkFile() {
        return startNewFileDialog("file");
    }

    private void rm(String path)
    {
        startIOService(path, null, "DELETE");
    }

    private void mv(String srcPath, String dstPath)
    {
        startIOService(srcPath, dstPath, "CUT");
    }

    private void cp(String srcPath, String dstPath)
    {
        startIOService(srcPath, dstPath, "COPY");
    }

    private void startIOService(String srcPath, String dstPath, String mode)
    {
        Intent i = new Intent(this, IOService.class);
        i.putExtra("SRC_PATH", srcPath);
        i.putExtra("DST_PATH", dstPath);
        i.putExtra("MODE", mode);
        startService(i);
    }

    private boolean startNewFileDialog(String mode)
    {
        Bundle args = new Bundle();
        try
        {
            args.putString("path", currentDir.getCanonicalPath());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        args.putString("mode", mode);
        DialogFragment mkFileFragment = new NewFileDialogFragment();
        mkFileFragment.setArguments(args);
        mkFileFragment.show(getSupportFragmentManager(), mode);
        return true;
    }

    private void fileMissingHandler()
    {
        Toast.makeText(this, "Error: this file/folder no longer exists!", Toast.LENGTH_SHORT).show();
        ls();
    }


}