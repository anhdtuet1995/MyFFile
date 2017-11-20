package work.uet.anhdt.ftpstorageofficial.tasks.upload;

import com.activeandroid.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by anansaj on 11/20/2017.
 */

public class UploadPool extends Thread implements Observer {

    private static final String TAG = UploadPool.class.getSimpleName();

    private static UploadPool pool;

    private List<UploadManager> uploadManagers;
    private List<Thread> 		  uploadThreads;
    private List<UploadManager> removedManagers;


    /**
     * Only single instance of the class is possible through <code>pool</code> object.
     */
    private UploadPool() {
        Log.d(TAG, "init");
        uploadManagers = new ArrayList<>();
        removedManagers  = new ArrayList<>();
        uploadThreads  = new ArrayList<>();
    }


    /**
     * Get the reference of upload pool.
     * @return
     */
    public static UploadPool getUploadPool() {
        Log.d(TAG, "getInstance()");
        if(pool == null)
            pool = new UploadPool();

        return pool;
    }


    /**
     * Gets, a upload manager if exists in the upload pool.
     * @param uploadId
     * @return Returns reference of UploadManager if exists in upload pool, otherwise null.
     */
    public UploadManager get(long uploadId) {
        Log.d(TAG, "get()" + uploadId);
        for(UploadManager manager : uploadManagers) {
            if(manager.getUploadId() == uploadId)
                return manager;
        }

        return null;
    }


    /**
     * Add a UploadManager to the upload pool. Calling this automatically starts the UploadManager.
     * @param manager
     */
    public synchronized void add(UploadManager manager) {
        Log.d(TAG, "add(UploadManager) " + manager.getUploadId());
        uploadManagers.add(manager);
        manager.addObserver(this);
        start(manager);
    }

    /**
     * Starts the UploadManager on a new upload Thread.
     * @param manager
     */
    private void start(UploadManager manager) {
        if(manager != null) {
            Log.d(TAG, "start manager " + manager.getUploadId());
            Thread t = new Thread(manager);
            t.setName(manager.getUploadId() + "");

            uploadThreads.add(t);

            t.start();
        }
        else {
            Log.d(TAG, "manager is null");
        }
    }


    /**
     * Removes the UploadManager from the upload pool. Calling this will automatically pause the active upload.
     * Calling this is similar to calling <code>remove(manager, true)</code>
     * @param manager
     */
    public synchronized void remove(UploadManager manager) {
        Log.d(TAG, "remove upload manager " + manager.getUploadId());
        remove(manager, true);
    }


    /**
     * Removes the UploadManager from the upload pool.
     * @param manager
     * @param stop true if you want to pause the upload if in progress, otherwise false.
     */
    public synchronized void remove(UploadManager manager, boolean stop) {
        if(manager != null) {
            Log.d(TAG, "remove upload manager with stop" + manager.getUploadId());
            if(stop) {
                Log.d(TAG, "stop = true");
                manager.pause();
            }
            else {
                Log.d(TAG, "stop = false");
                removedManagers.add(manager);
                uploadManagers.remove(manager);
            }
        }
    }

    /**
     * Removes the UploadManager from the upload pool. Calling this will automatically pause the active upload.
     * Calling this is similar to calling <code>remove(uploadId)</code>
     */
    public synchronized void remove(long uploadId) {
        Log.d(TAG, "remove uploadId " + uploadId);
        UploadManager manager = get(uploadId);
        remove(manager);
    }

    /**
     * Pauses all active uploads and removes from the upload pool.
     */
    public synchronized void removeAll() {
        Log.d(TAG, "remove all upload manager");
        List<Long> ids = new ArrayList<>();

        for (UploadManager manager : uploadManagers) {
            ids.add(manager.getUploadId());
        }

        for (Long id : ids) {
            remove(id);
        }
    }


    @SuppressWarnings("unused")
    private Thread getUploadThread(long uploadId) {
        Log.d(TAG, "get Upload manager id = " + uploadId);
        for(Thread t : uploadThreads) {
            if(t.getName().equals(uploadId + ""))
                return t;
        }

        return null;
    }

    /**
     * @return Returns total active uploads at the current moment.
     */
    public int activeUploadCount() {
        return uploadManagers.size();
    }


    /**
     * Removes the reference of a upload manager if it completed upload.
     */
    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof UploadManager) {
            Log.d(TAG, "update manager");
            UploadManager manager = (UploadManager) o;

            UploadStatus status = manager.getStatus();
            if(status == UploadStatus.PAUSED || status == UploadStatus.ERROR) {
                // If current upload is paused or some error occurred
                // Then remove it from upload pool

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
