package com.bmeath.filemanager;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by bm on 23/04/17.
 */

public class IOService extends Service implements Observer
{
    private class ResultToast implements Runnable
    {
        String msg;

        public ResultToast(String msg) {
            this.msg = msg;
        }

        public void run()
        {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private Handler handler;
    IBinder binder;
    Thread t;

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public int onStartCommand(final Intent intent, int flags, int startId)
    {
        handler = new Handler();
        final String[] paths = {
                intent.getStringExtra("SRC_PATH"),
                intent.getStringExtra("DST_PATH")
        };

        String mode = intent.getStringExtra("MODE");
        switch (mode)
        {
            case "COPY":
                t = new Thread(new CopyThread(paths[0], paths[1], false, this));
                break;
            case "CUT":
                t = new Thread(new CopyThread(paths[0], paths[1], true, this));
                break;
            case "DELETE":
                t =  new Thread(new DeleteThread(paths[0], this));
                break;
            default:
                // no mode specified
                stopSelf();
                return START_NOT_STICKY;
        }
        t.start();
        return START_NOT_STICKY;
    }

    public void onDestroy()
    {
        handler.post(new ResultToast("Service is being destroyed"));
        super.onDestroy();
    }

    public void update(Observable o, Object arg)
    {
        handler.post(new ResultToast((String) arg));
    }

}
