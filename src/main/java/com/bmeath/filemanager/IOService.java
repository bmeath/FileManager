package com.bmeath.filemanager;

import android.app.NotificationManager;
import android.app.Service;
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

    private int notifyId = 1;
    private NotificationManager progressNotification;
    private NotificationCompat.Builder nBuilder;

    public int onStartCommand(final Intent intent, int flags, int startId)
    {
        progressNotification = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        handler = new Handler();

        nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setContentTitle("File Manager");
        nBuilder.setSmallIcon(R.drawable.ic_stat_name);
        nBuilder.setProgress(0, 0, true);

        final String[] paths = {
                intent.getStringExtra("SRC_PATH"),
                intent.getStringExtra("DST_PATH")
        };

        String mode = intent.getStringExtra("MODE");
        switch (mode)
        {
            case "COPY":
                nBuilder.setContentText("Copy in progress");
                t = new Thread(new CopyThread(paths[0], paths[1], false, this));
                break;
            case "CUT":
                nBuilder.setContentText("Cut in progress");
                t = new Thread(new CopyThread(paths[0], paths[1], true, this));
                break;
            case "DELETE":
                nBuilder.setContentText("Delete in progress");
                t =  new Thread(new DeleteThread(paths[0], this));
                break;
            default:
                // no mode specified
                stopSelf();
                return START_NOT_STICKY;
        }
        progressNotification.notify(notifyId, nBuilder.build());
        t.start();
        return START_NOT_STICKY;
    }

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

    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
