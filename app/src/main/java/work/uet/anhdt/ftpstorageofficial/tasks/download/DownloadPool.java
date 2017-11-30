package work.uet.anhdt.ftpstorageofficial.tasks.download;

import com.activeandroid.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import work.uet.anhdt.ftpstorageofficial.tasks.upload.UploadManager;

/**
 * Created by anansaj on 11/20/2017.
 */

public class DownloadPool extends Thread implements Observer {

    private static final String TAG = DownloadPool.class.getSimpleName();

    private static DownloadPool pool;

    private List<DownloadManager> downloadManagers;
    private List<Thread> 		  downloadThreads;
    private List<DownloadManager> removedManagers;


    /**
     * Only single instance of the class is possible through <code>pool</code> object.
     */
    private DownloadPool() {
        Log.d(TAG, "init");
        downloadManagers = new ArrayList<>();
        removedManagers  = new ArrayList<>();
        downloadThreads  = new ArrayList<>();
    }


    /**
     * Get the reference of download pool.
     * @return
     */
    public static DownloadPool getDownloadPool() {
        Log.d(TAG, "getInstance()");
        if(pool == null)
            pool = new DownloadPool();

        return pool;
    }


    /**
     * Gets, a download manager if exists in the download pool.
     * @param downloadId
     * @return Returns reference of DownloadManager if exists in download pool, otherwise null.
     */
    public DownloadManager get(long downloadId) {
        Log.d(TAG, "get()" + downloadId);
        for(DownloadManager manager : downloadManagers) {
            if(manager.getDownloadId() == downloadId)
                return manager;
        }

        return null;
    }


    /**
     * Add a DownloadManager to the download pool. Calling this automatically starts the DownloadManager.
     * @param manager
     */
    public synchronized void add(DownloadManager manager) {
        Log.d(TAG, "add(DownloadManager) " + manager.getDownloadId());
        downloadManagers.add(manager);
        manager.addObserver(this);
        start(manager);
    }

    /**
     * Starts the DownloadManager on a new download Thread.
     * @param manager
     */
    private void start(DownloadManager manager) {
        if(manager != null) {
            Log.d(TAG, "start manager " + manager.getDownloadId());
            Thread t = new Thread(manager);
            t.setName(manager.getDownloadId() + "");

            downloadThreads.add(t);

            t.start();
        }
        else {
            Log.d(TAG, "manager is null");
        }
    }


    /**
     * Removes the DownloadManager from the download pool. Calling this will automatically pause the active download.
     * Calling this is similar to calling <code>remove(manager, true)</code>
     * @param manager
     */
    public synchronized void remove(DownloadManager manager) {
        Log.d(TAG, "remove download manager " + manager.getDownloadId());
        remove(manager, true);
    }


    /**
     * Removes the DownloadManager from the download pool.
     * @param manager
     * @param stop true if you want to pause the download if in progress, otherwise false.
     */
    public synchronized void remove(DownloadManager manager, boolean stop) {
        if(manager != null) {
            Log.d(TAG, "remove download manager with stop" + manager.getDownloadId());
            if(stop) {
                Log.d(TAG, "stop = true");
                manager.pause();
            }
            else {
                Log.d(TAG, "stop = false");
                manager.stop();
                removedManagers.add(manager);
                downloadManagers.remove(manager);
            }
        }
    }

    /**
     * Stop the UploadManager from the upload pool. Calling this will automatically pause the active upload.
     * Calling this is similar to calling <code>remove(manager, true)</code>
     * @param manager
     */
    public synchronized void stop(DownloadManager manager) {
        Log.d(TAG, "stop upload manager " + manager.getDownloadId());
        remove(manager, false);
    }

    /**
     * Removes the DownloadManager from the download pool. Calling this will automatically pause the active download.
     * Calling this is similar to calling <code>remove(downloadId)</code>
     */
    public synchronized void remove(long downloadId) {
        Log.d(TAG, "remove downloadId " + downloadId);
        DownloadManager manager = get(downloadId);
        remove(manager);
    }

    /**
     * Stop the UploadManager from the download pool. Calling this will automatically stop the active upload.
     * Calling this is similar to calling <code>remove(uploadId)</code>
     */
    public synchronized void stop(long downloadId) {
        Log.d(TAG, "remove uploadId " + downloadId);
        DownloadManager manager = get(downloadId);
        stop(manager);
    }

    /**
     * Pauses all active downloads and removes from the download pool.
     */
    public synchronized void removeAll() {
        Log.d(TAG, "remove all download manager");
        List<Long> ids = new ArrayList<>();

        for (DownloadManager manager : downloadManagers) {
            ids.add(manager.getDownloadId());
        }

        for (Long id : ids) {
            remove(id);
        }
    }


    @SuppressWarnings("unused")
    private Thread getDownloadThread(long downloadId) {
        Log.d(TAG, "get Download manager id = " + downloadId);
        for(Thread t : downloadThreads) {
            if(t.getName().equals(downloadId + ""))
                return t;
        }

        return null;
    }

    /**
     * @return Returns total active downloads at the current moment.
     */
    public int activeDownloadCount() {
        return downloadManagers.size();
    }


    /**
     * Removes the reference of a download manager if it completed download.
     */
    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof DownloadManager) {
            Log.d(TAG, "update manager");
            DownloadManager manager = (DownloadManager) o;

            DownloadStatus status = manager.getStatus();
            if(status == DownloadStatus.PAUSED || status == DownloadStatus.ERROR) {
                // If current download is paused or some error occurred
                // Then remove it from download pool

                remove(manager, false);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        Log.d(TAG, "finalize()");
        removeAll();
        super.finalize();
    }

}
