package com.bmeath.filemanager;

import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;

/**
 * Created by bm on 23/04/17.
 */

public class CopyThread extends Observable implements Runnable
{
    String[] paths = new String[2];
    boolean deleteSrc;
    boolean finished;

    public CopyThread(String srcPath, String dstPath, boolean deleteSrc, IOService observer)
    {
        paths[0] = srcPath;
        paths[1] = dstPath;
        addObserver(observer);
    }

    public void run()
    {
        finished = FileHelpers.copy(paths[0], paths[1]);
        if (deleteSrc)
        {
            finished = false;
            finished = FileHelpers.delete(paths[0]);
        }
        setChanged();
        if (finished) {
            notifyObservers("Copy succeeded");
        }
        else
        {
            notifyObservers("Copy finished with errors");
        }
    }
}
