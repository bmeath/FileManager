package com.bmeath.filemanager;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by bm on 23/04/17.
 *
 * A service to manage I/O operations in the background on a separate thread
 */

public class IOService extends Service implements Observer
{
    private Thread t;

    // Handler for generating Toasts
    private Handler handler;

    private Context appContext;

    private int notifyId = 1;
    private NotificationManager progressNotification;
    private NotificationCompat.Builder nBuilder;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId)
    {
        appContext = getApplicationContext();
        final String[] paths = intent.getStringArrayExtra("PATHS");
        FileOp op = (FileOp) intent.getSerializableExtra("OPERATION");


        progressNotification = (NotificationManager) getSystemService(appContext.NOTIFICATION_SERVICE);
        handler = new Handler();

        nBuilder = new NotificationCompat.Builder(this, appContext.getString(R.string.IONotifChannel));
        nBuilder.setContentTitle(getResources().getString(R.string.app_name));
        nBuilder.setSmallIcon(R.drawable.io_notify_icon);
        nBuilder.setProgress(0, 0, true);


        switch (op)
        {
            case COPY:
                nBuilder.setContentText("Copy in progress");
                break;
            case CUT:
                nBuilder.setContentText("Cut in progress");
                break;
            case DELETE:
                nBuilder.setContentText("Delete in progress");
                break;
            default:
                // no mode specified
                stopSelf();
                return START_NOT_STICKY;
        }
        t =  new Thread(new IOThread(paths, op, this));
        progressNotification.notify(notifyId, nBuilder.build());
        t.start();
        return START_NOT_STICKY;
    }

    @Override
    public void update(Observable o, Object arg)
    {
        final String msg = (String) arg;
        nBuilder.setContentText(msg);
        nBuilder.setProgress(100, 100, false);
        progressNotification.notify(notifyId, nBuilder.build());

        handler.post(new Runnable()
        {
            public void run()
            {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
