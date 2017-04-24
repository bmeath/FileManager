package com.bmeath.filemanager;

import android.os.Looper;

import java.io.File;
import java.io.IOException;
import java.util.Observable;

/**
 * Created by bm on 23/04/17.
 */

public class DeleteThread extends Observable implements Runnable
{

    private String path;
    boolean finished;

    public DeleteThread(String path, IOService observer)
    {
        this.path = path;
        if (observer != null)
        {
            addObserver(observer);
        }
    }

    public void run()
    {
        finished = delete(path);
        setChanged();
        if (finished) {
            notifyObservers("Delete finished successfully");
        }
        else
        {
            notifyObservers("Delete finished with errors");
        }
    }


    public static boolean delete(String path)
    {
        File f = new File(path);
        if (f.isDirectory())
        {
            String[] files = f.list();
            for (int i = 0; i < files.length; i++)
            {
                try
                {
                    delete(new File(f, files[i]).getCanonicalPath());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    return false;
                }
            }
            // all sub-items have been deleted, now delete the empty folder
            f.delete();
            return true;
        }
        else
        {
            f.delete();
            return true;
        }
    }

}
