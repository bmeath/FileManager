package com.bmeath.filemanager;

import java.util.Arrays;
import java.util.Observable;

/**
 * Created by bm on 23/04/17.
 */

public class IOThread extends Observable implements Runnable {
    private String[] paths;
    private FileOp mode;
    private boolean ret;

    public IOThread(String[] paths, FileOp mode, IOService observer) {
        this.paths = paths;
        this.mode = mode;
        if (observer != null) {
            addObserver(observer);
        }
    }

    @Override
    public void run()
    {
        switch (mode) {
            case CUT:
                ret = FileHelpers.copy(paths);
                if (ret) { // only proceed to delete the orginal if the copy was successful
                    ret = FileHelpers.delete(Arrays.copyOf(paths, paths.length - 1));
                }
                setChanged();
                if (ret) {
                    notifyObservers("Cut succeeded");
                } else {
                    notifyObservers("Error during cut");
                }
                break;
            case COPY:
                ret = FileHelpers.copy(paths);
                setChanged();
                if (ret) {
                    notifyObservers("Copy succeeded");
                } else {
                    notifyObservers("Error during copy");
                }
                break;
            case DELETE:
                ret = FileHelpers.delete(paths);
                setChanged();
                if (ret) {
                    notifyObservers("Deletion succeeded");
                } else {
                    notifyObservers("Error during deletion");
                }
                break;
        }
    }
}