package com.bmeath.filemanager;

import java.util.Observable;

/**
 * Created by bm on 23/04/17.
 */

public class DeleteThread extends Observable implements Runnable
{
    private String path;
    boolean finished;

    public DeleteThread(String path, IOService observer)
    {
        this.path = path;
        if (observer != null)
        {
            addObserver(observer);
        }
    }

    public void run()
    {
        finished = FileHelpers.delete(path);
        setChanged();
        if (finished)
        {
            notifyObservers("Deletion succeeded");
        }
        else
        {
            notifyObservers("Delete finished with errors");
        }
    }
}
