package com.bmeath.filemanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by bm on 23/04/17.
 */

public class CopyService extends Service
{
    Thread copyThread;

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        String src = intent.getStringExtra("SRC_PATH");
        String dst = intent.getStringExtra("DST_PATH");

        copyThread = new CopyThread(src, dst);
        copyThread.start();
        return START_NOT_STICKY;
    }
}
