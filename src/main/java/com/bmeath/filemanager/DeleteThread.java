package com.bmeath.filemanager;

import java.io.File;
import java.io.IOException;

/**
 * Created by bm on 23/04/17.
 */

public class DeleteThread extends Thread {

    private String path;

    public DeleteThread(String path)
    {
        this.path = path;
    }

    public static void delete(String path)
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
                }
            }
            // all sub-items have been deleted, now delete the empty folder
            f.delete();
        }
        else
        {
            f.delete();
        }
    }

}
