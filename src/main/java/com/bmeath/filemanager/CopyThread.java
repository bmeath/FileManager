package com.bmeath.filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by bm on 23/04/17.
 */

public class CopyThread extends Thread
{
    String[] paths = new String[2];
    boolean deleteSrc;

    public CopyThread(String srcPath, String dstPath, boolean deleteSrc)
    {
        paths[0] = srcPath;
        paths[1] = dstPath;
    }

    public void run()
    {
        copy(paths[0], paths[1]);
        if (deleteSrc)
        {
            DeleteThread.delete(paths[0]);
        }
    }

    public static void copy(String srcPath, String dstPath)
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
        }
        catch (FileNotFoundException fnfe)
        {
            fnfe.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
