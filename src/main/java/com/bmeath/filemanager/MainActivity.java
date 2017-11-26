package com.bmeath.filemanager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {

    /* keeps track of what files are marked for cut/copy/delete */
    private class Clipboard {
        ArrayList<String> paths = new ArrayList<String>();
        FileOp operation;

        void add(String path) {
            paths.add(path);
        }

        String get(int index) {
            return paths.get(index);
        }

        void clear() {
            paths.clear();
        }

        boolean isEmpty() {
            return paths.isEmpty();
        }
    }

    private static final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private Settings settings = Settings.getInstance();

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView lView;
    private FileAdapter fileAdapter;

    private File currentDir;
    private String parent;

    // used to launch file viewing activities
    private Intent fileViewIntent = new Intent(Intent.ACTION_VIEW).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    private Clipboard clipboard = new Clipboard(); // keep track of file(s) selected for cut/copy/delete

    // options menu
    private MenuItem pasteOption;
    private MenuItem showHiddenOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings.init(getPreferences(Context.MODE_PRIVATE));
        settings.load();

        setContentView(R.layout.activity_main);

        Toolbar tBar = (Toolbar) findViewById(R.id.tBar);
        setSupportActionBar(tBar);

        // check if storage access permissions need to be requested
        getPermission();

        // notifications must be posted to a channel for Android O onwards
        createNotificationChannels();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        lView = (ListView) findViewById(R.id.lView);
        lView.setOnItemClickListener(this);
        lView.setOnItemLongClickListener(this);
        lView.setOnCreateContextMenuListener(this);

        // set current directory to external storage and list contents
        changeDir(Environment.getExternalStorageDirectory().getAbsolutePath());
        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        settings.load();
    }

    @Override
    public void onPause() {
        super.onPause();
        settings.save();
    }

    // request permissions that are needed
    private void getPermission() {
        // check if run-time permission requesting should be done
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            for (String permission : permissions) {
                int havePermission = ContextCompat.checkSelfPermission(this, permission);
                if (havePermission == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
                }
            }
        }
    }

    // create notification channels to post notifications on
    public void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notifChannel = new NotificationChannel(getString(R.string.IONotifChannel), "IO Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notifChannel.setDescription("Receive notifications when moving files.");
            if (notifManager != null) {
                notifManager.createNotificationChannel(notifChannel);
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu m) {
        if (clipboard.isEmpty()) {
            pasteOption.setVisible(false);
        }
        else
        {
            pasteOption.setVisible(true);
        }
        showHiddenOption.setChecked(settings.showHidden);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.menu_main, m);
        pasteOption = m.findItem(R.id.paste);
        showHiddenOption = m.findItem(R.id.showhidden);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        switch (id) {
            case R.id.paste:
                try {
                    String src = clipboard.get(0);
                    startIOService(src, currentDir.getCanonicalPath() + File.separator + new File(src).getName(), clipboard.operation);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clipboard.clear();
                invalidateOptionsMenu();
                refresh();
                break;
            case R.id.newfile:
                createFile(FileType.REG);
                refresh();
                break;
            case R.id.newfolder:
                createFile(FileType.DIR);
                refresh();
                break;
            case R.id.refresh:
                refresh();
                break;
            case R.id.showhidden:
                settings.showHidden ^= true;
                refresh();
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
        File f = (File) fileAdapter.getItem(position);
        if (f.exists()) {
            openFile(f);
        } else {
            Toast.makeText(this, "Error: this file/folder no longer exists!", Toast.LENGTH_SHORT).show();
            refresh();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        lView.setTag(position); // allows position of file in list to be known in onContextItemSelected
        registerForContextMenu(lView);
        openContextMenu(lView);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lView) {
            String[] options = getResources().getStringArray(R.array.long_click_menu);
            for (int i = 0; i < options.length; i++) {
                menu.add(Menu.NONE, i, i, options[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem option) {
        String path;

        File f = (File) fileAdapter.getItem((int) lView.getTag());
        try {
            path = f.getCanonicalPath();
            switch (option.getItemId()) {
                case 0: // open
                    openFile(f);
                    break;
                case 1: // cut
                    clipboard.clear();
                    clipboard.add(path);
                    clipboard.operation = FileOp.CUT;
                    invalidateOptionsMenu();
                    break;
                case 2: // copy
                    clipboard.clear();
                    clipboard.add(path);
                    clipboard.operation = FileOp.COPY;
                    invalidateOptionsMenu();
                    break;
                case 3: // delete
                    startIOService(path, FileOp.DELETE);
                    Toast.makeText(this, "Deleting...", Toast.LENGTH_SHORT).show();
                    refresh();
                    break;
                case 4: // rename
                    renameFile(path);
                    refresh();
                    break;
                case 5: // properties
                    showProperties(path);
                    break;
                default:
            }
        } catch (IOException e) {
            Toast.makeText(this, "Failed to open file/folder", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return true;
    }

    private void openFile(File f) {
        if (f.isDirectory()) {
            if (f.getName().equals("..")) {
                changeDir(parent);
                refresh();
            } else {
                changeDir(f.getAbsolutePath());
                refresh();
            }
        } else {
            String mimeType = FileHelpers.getMimeType(f.getAbsolutePath());

            if (mimeType != null) {
                fileViewIntent.setDataAndType(Uri.fromFile(f), mimeType);

                try {
                    startActivity(fileViewIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "No applications were found for this type of file.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Can't open a file of unknown type", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // set current directory
    private void changeDir(String newPath) {
        if (newPath.equals("../")) {
            currentDir = currentDir.getParentFile();
        } else {
            currentDir = new File(newPath);
        }
        parent = currentDir.getParent();
        setTitle(newPath);
    }

    private void refresh() {
        // get names of current directory contents
        ArrayList<File> contents = new ArrayList<>();
        ArrayList<File> contentsFiles = new ArrayList<>();

        if (currentDir.canRead()) {
            File[] currentDirList = currentDir.listFiles();

            // convert string array to arraylist
            if (currentDirList != null) {
                // exclude hidden items
                for (File aCurrentDirList : currentDirList) {
                    if (!aCurrentDirList.isHidden() || settings.showHidden) {
                        // keep files separate from folders until later for sorting purposes
                        if (aCurrentDirList.isDirectory()) {
                            contents.add(aCurrentDirList);
                        } else {
                            contentsFiles.add(aCurrentDirList);
                        }
                    }
                }
            }

            // sort alphabetically
            Collections.sort(contents);
            Collections.sort(contentsFiles);
            // now append files to folders, so that folders are at top of list
            contents.addAll(contentsFiles);

            if (parent != null) {
                contents.add(0, new File("../"));
            }
        } else {
            contents.add(0, new File("../"));
        }

        // link file names to ListView using FileAdapter
        fileAdapter = new FileAdapter(this, contents);
        lView.setAdapter(fileAdapter);
        registerForContextMenu(lView);
    }

    // start a service to do IO tasks in a separate thread
    private void startIOService(String[] paths, FileOp op) {
        Intent i = new Intent(this, IOService.class);
        i.putExtra("PATHS", paths);
        i.putExtra("OPERATION", op);

        startService(i);
    }

    // intended for cut/copy of a single file, but also possible to call this for deletion of two files
    private void startIOService(String src, String dst, FileOp op) {
        startIOService(new String[] {src, dst}, op);
    }

    // only intended for use when deleting a file (since cut/copy require at least two paths)
    private void startIOService(String s, FileOp op) {
        startIOService(new String[] {s}, op);
    }

    private void createFile(FileType ftype)
    {
        Bundle args = new Bundle();
        try {
            args.putString("path", currentDir.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        args.putSerializable("mode", ftype);
        DialogFragment mkFileFragment = new FileCreateDialogFragment();
        mkFileFragment.setArguments(args);
        mkFileFragment.show(getSupportFragmentManager(), "newitem");
    }

    public void renameFile(String path) {
        Bundle args = new Bundle();
        args.putString("path", path);
        DialogFragment renameFragment = new FileRenameDialogFragment();
        renameFragment.setArguments(args);
        renameFragment.show(getSupportFragmentManager(), "rename");
    }

    // show file/folder properties
    private void showProperties(String path) {
        Bundle args = new Bundle();
        args.putString("path", path);
        DialogFragment propsFragment = new FilePropsDialogFragment();
        propsFragment.setArguments(args);
        propsFragment.show(getSupportFragmentManager(), "props");
    }

    // called after swiping to refresh
    @Override
    public void onRefresh() {
        refresh();
        swipeRefreshLayout.setRefreshing(false);
    }
}