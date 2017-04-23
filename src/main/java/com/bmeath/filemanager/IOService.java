package com.bmeath.filemanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by bm on 23/04/17.
 */

public class IOService extends Service
{
    Thread t;

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public int onStartCommand(final Intent intent, int flags, int startId)
    {
        final String[] paths = {
                intent.getStringExtra("SRC_PATH"),
                intent.getStringExtra("DST_PATH")
        };

        String mode = intent.getStringExtra("MODE");
        switch (mode)
        {
            case "COPY":
                t = new CopyThread(paths[0], paths[1], false);
                break;
            case "CUT":
                t = new CopyThread(paths[0], paths[1], true);
                break;
            case "DELETE":
                t = new DeleteThread(paths[0]);
                break;
            default:
                stopSelf();
        }
        t.start();
        return START_NOT_STICKY;
    }
}
