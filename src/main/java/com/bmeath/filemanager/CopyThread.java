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
        finished = copy(paths[0], paths[1]);
        if (deleteSrc)
        {
            finished = false;
            finished = DeleteThread.delete(paths[0]);
        }
        setChanged();
        if (finished) {
            notifyObservers("Copy finished successfully");
        }
        else
        {
            notifyObservers("Copy finished with errors");
        }
    }

    public static boolean copy(String srcPath, String dstPath)
    {
        InputStream in = null;
        OutputStream out = null;
        try
        {
            File src = new File(srcPath);
            File dst = new File(dstPath);

            if (src.isDirectory())
            {
                if (!dst.exists())
                {
                    dst.mkdirs();
                }

                String[] files = src.list();

                for (int i = 0; i < files.length; i++)
                {
                    String newSrc = new File(src, files[i]).getCanonicalPath();
                    String newDst = new File(dst, files[i]).getCanonicalPath();
                    // recursively copy all sub-items
                    copy(newSrc, newDst);
                }
            }
            else
            {
                in = new FileInputStream(srcPath);
                if (new File(dstPath).isDirectory())
                {
                    dstPath += File.separator + src.getName();
                }
                out = new FileOutputStream(dstPath);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;

                // write the output file
                out.flush();
                out.close();
                out = null;
            }
            return true;
        }
        catch (FileNotFoundException fnfe)
        {
            fnfe.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
