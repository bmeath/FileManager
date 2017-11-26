package com.bmeath.filemanager;

/*
 * Created by bm on 24/04/17.
 *
 * A class in which I will contain helpful file-related methods
 */

import android.content.Context;
import android.text.format.Formatter;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.LinkedHashMap;

public class FileHelpers {
    private static final String[] ILLEGAL_CHARS = {"\\", "/", ":", "*", "?", "'", "<", ">", "|"};
    private static final int FILENAME_LEN_MAX = 127;
    private static final String RENAME_APPEND = "-copy"; // to prevent overwriting existing files
    private static final MimeTypeMap mime = MimeTypeMap.getSingleton();

    public static boolean isValidFilename(String s)
    {
        int len = s.length();
        if (len > FILENAME_LEN_MAX || len == 0) {
            return false;
        }

        for (int i = 0; i < ILLEGAL_CHARS.length; i ++)
        {
            if (s.contains(ILLEGAL_CHARS[i]))
            {
                return false;
            }
        }
        if (s.charAt(len - 1) == '.')
        {
            return false;
        }
        return true;
    }

    /* Copy files, and recursively copy folders.
     * paths: an array of the files/folders to copy, with the last element being the destination path.
     * returns true after all files were successfully copied, otherwise false
     */
    public static boolean copy(String[] paths)
    {
        InputStream in;
        OutputStream out;
        File src, dst;
        String srcPath, dstPath;

        try {
            dstPath = paths[paths.length - 1];
            dst = new File(dstPath);

            for (int i = 0; i < paths.length - 1; i++) {
                srcPath = paths[i];
                src = new File(srcPath);

                if (src.isDirectory()) {
                    if (!dst.exists()) {
                        dst.mkdirs();
                    }

                    String[] files = src.list();
                    // recursively copy all sub-items
                    for (int j = 0; j < files.length; j++) {
                        copy(new File(src, files[j]).getCanonicalPath(), new File(dst, files[j]).getCanonicalPath());
                    }
                } else {
                    in = new FileInputStream(srcPath);

                    if (new File(dstPath).isDirectory()) {
                        dstPath += File.separator + src.getName();
                    }

                    if (new File(dstPath).exists()) {
                        dstPath = renameCopy(dstPath);
                    }

                    out = new FileOutputStream(dstPath);

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }

                    in.close();
                    out.flush();
                    out.close();
                }
            }
            return true;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean copy(String srcPath, String dstPath) {
        return copy(new String[]{srcPath, dstPath});
    }


    /* Delete files/folders.
     * paths: an array of the files/folders to delete
     * returns true after all files/folders were successfully deleted, otherwise false
     */
    public static boolean delete(String[] paths)
    {
        for (String path: paths) {
            File f = new File(path);
            if (f.isDirectory()) {
                String[] files = f.list();
                for (int i = 0; i < files.length; i++) {
                    try {
                        delete(new File(f, files[i]).getCanonicalPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
            // all sub-items have been deleted, now delete the empty folder
            f.delete();
        }
        return true;
    }

    public static boolean delete(String path) {
        return delete(new String[] {path});
    }

    /*
     * Returns a unique file path to avoid overwriting a file of the same name
     */
    public static String renameCopy(String path) {
        String uniquePath = path;

        while (new File(uniquePath).exists()) {
            int extIndex = (uniquePath.lastIndexOf('.'));

            if (extIndex == -1) {
                uniquePath += RENAME_APPEND;
            } else {
                uniquePath = uniquePath.substring(0, extIndex) + RENAME_APPEND + uniquePath.substring(extIndex);
            }

            if (uniquePath.length() > 255) {
                return null;
            }
        }
        return uniquePath;
    }

    /*
     * returns a hashmap of properties of a file/folder
     */
    public static LinkedHashMap<String, String> getProperties(Context context, String path) {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        File f = new File(path);
        LinkedHashMap<String, String> props = new LinkedHashMap<>();
        String name = f.getName();

        props.put("Name", name);

        if (f.isDirectory()) {
            props.put("Type", "folder");
        } else {
            props.put("Type", getMimeType(name));
        }

        try {
            props.put("Location", f.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        props.put("Size", Formatter.formatShortFileSize(context, f.length()));

        props.put("Modified", dateFormat.format(f.lastModified()));

        return props;
    }

    public static String getMimeType(String name) {
        // get mimetype using extension extracted from filename
        String s = name;
        int i = s.lastIndexOf(".") + 1;
        if (i < s.length()) {
            String mimeType = mime.getMimeTypeFromExtension(s.substring(i).toLowerCase());
            return mimeType;
        } else {
            return null;
        }
    }



}
