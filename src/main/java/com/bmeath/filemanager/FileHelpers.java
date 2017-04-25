package com.bmeath.filemanager;

/*
 * Created by bm on 24/04/17.
 *
 * A class in which I will contain helpful file-related methods
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHelpers
{
    private static final String[] ILLEGAL_CHARS = {"\\", "/", ":", "*", "?", "'", "<", ">", "|"};
    private static final String RENAME_APPEND = "-copy";

    public static boolean isValidFilename(String s)
    {
        for (int i = 0; i < ILLEGAL_CHARS.length; i ++)
        {
            if (s.contains(ILLEGAL_CHARS[i]))
            {
                return false;
            }
        }
        if (s.charAt(s.length()-1) == '.')
        {
            return false;
        }
        return true;
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

                if (new File(dstPath).exists())
                {
                    dstPath = renameCopy(dstPath);
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
        }
        // all sub-items have been deleted, now delete the empty folder
        f.delete();
        return true;
    }


    /*
     * Returns a unique file path to avoid overwriting a file of the same name
     */
    public static String renameCopy(String path)
    {
        String uniquePath = path;

        while (new File(uniquePath).exists())
        {
            int extIndex = (uniquePath.lastIndexOf('.'));

            if (extIndex == -1)
            {
                uniquePath += RENAME_APPEND;
            }
            else
            {
                uniquePath = uniquePath.substring(0, extIndex) + RENAME_APPEND + uniquePath.substring(extIndex);
            }

            if (uniquePath.length() > 255)
            {
                return null;
            }
        }
        return uniquePath;
    }

}
