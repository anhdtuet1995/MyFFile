package work.uet.anhdt.ftpstorageofficial.tasks.upload;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Observable;

/**
 * Created by anansaj on 11/20/2017.
 */

public class UploadManager extends Observable implements Runnable {

    private final String TAG_UPLOAD = UploadManager.class.getSimpleName();
    private final static int MAX_WORKER	= 8;
    private final static int MIN_WORKER_UPLOADD_SIZE = 1024 * 1024; // 1MB

    private long uploadId;

    private UploadWorker[] workers		= null;
    private Thread[] 		 workerThreads	= null;
    private int 			 workersAtWork	= 0;

    private String 	uploadURL			= null;
    private String 	phonePath			= "";

    private long 	uploadSize		= 0l;
    private Long 	uploadCompleted	= 0l;
    private float	uploadSpeed		= 0.0f;

    private UploadStatus 	 status		= null;
    private UploadMetadata metadata	= null;
    private List<UploadPartsMetadata> partsMetaList = null;

    /**
     * Use this constructor for new upload with default upload url.
     * @param phonePath phone path to upload.
     */
    public UploadManager(String phonePath) {
        this(phonePath, UploadConfiguration.DEFAULT_UPLOAD_URL);
    }

    /**
     * Use this constructor for new upload.
     * @param uploadURL URL to upload.
     */
    public UploadManager(String phonePath, String uploadURL) {
        workers 		= new UploadWorker[MAX_WORKER];
        workerThreads 	= new Thread[MAX_WORKER];

        this.uploadURL	= uploadURL;
        status			= UploadStatus.NEW;
        this.phonePath		= phonePath;

        metadata = new UploadMetadata(phonePath, uploadURL);

        uploadId = metadata.getId();
    }

    public UploadManager(UploadMetadata metadata, List<UploadPartsMetadata> partsMeta) {
        Log.d(TAG_UPLOAD, "init uploadmanager with metadata="+ metadata.getId() + ", partsMeta=" + partsMeta.size());
        workers		= new UploadWorker[MAX_WORKER];
        workerThreads	= new Thread[MAX_WORKER];

        this.metadata		= metadata;
        uploadURL	= metadata.getUrl();

        partsMetaList = new ArrayList<>(partsMeta);

        uploadId 	= metadata.getId();
        uploadSize	= metadata.getFileSize();
        status 		= metadata.getStatus();

        workersAtWork	= partsMeta.size();

        uploadCompleted	= metadata.getCompleted();
    }

    /**
     * Start a new download
     */
    @Override
    public void run() {
        try {
            if (status != UploadStatus.PAUSED) {
                uploadFileMetaInformation();
                updateUploadStatus();
            }

            employWorkers();

        } catch (IOException e) {
            Log.e(TAG_UPLOAD, "[ERROR] " + e.getMessage());

            status = UploadStatus.ERROR;
            updateUploadStatus();

            return;
        }

        status = UploadStatus.UPLOADING;

        // Run a new thread to compute download speed
        initDownloadSpeedCalculator();

        // Start all worker download threads
        startAllWorkers();

        // Wait for workers to end
        waitForWorkers();

        // Finally update the GUI
        updateUploadGUI();

        // Merge the file if download completed
        if(uploadCompleted == uploadSize) {
            status = UploadStatus.COMPLETED;

            waitJoinUploadedFile();
        }

        updateUploadStatus();
    }

    /**
     * Pause the upload.
     */
    public void pause() {
        status = UploadStatus.PAUSED;
    }

    /**
     * Resume the upload.
     */
    public void resume() {
        status = UploadStatus.UPLOADING;
    }

    /**
     * Returns the current upload status.
     * @return
     */
    public UploadStatus getStatus() {
        return status;
    }

    /**
     * Returns the upload metadata
     * @return
     */
    public UploadMetadata getMetadata() {
        return metadata;
    }

    /**
     * Returns the total number of bytes upload
     * @return
     */
    public long getUploadCompleted() {
        return uploadCompleted;
    }

    /**
     * @return the unique upload Id.
     */
    public long getUploadId() {
        return uploadId;
    }

    /**
     * @return the current upload speed.
     */
    public float getUploadSpeed() {
        return uploadSpeed;
    }

    /**
     * Initializes the upload speed calculator. It continuously monitors the upload speed.
     */
    private void initDownloadSpeedCalculator() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final Date startTime = new Date();
                Date endTime;

                while(status == UploadStatus.UPLOADING) {
                    endTime = new Date();
                    if(uploadCompleted != 0)
                        uploadSpeed = uploadCompleted / ((endTime.getTime() - startTime.getTime()) / 1000.0f) ;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        t.setName(uploadId + "Speed");
        t.start();
    }

    /**
     * Fetches the basic file metadata information from the upload path.
     * @throws IOException
     */
    private void uploadFileMetaInformation() throws IOException {
        metadata.getLoadFileMetadata();

        if(metadata == null)
            throw new IOException("Unable to get download file information.");

        status	= UploadStatus.READY;
        uploadCompleted	= 0l;
        uploadSize		= metadata.getFileSize();
        uploadId	= metadata.getId();

        metadata.setFilePath(phonePath);
        metadata.setStatus(status);
        metadata.setStartTime(new Date());
        metadata.setEndTime(new Date());
    }

    /**
     * Updates the upload status to DB.
     */
    private void updateUploadStatus() {
        DBUploadFactory factory = DBUploadFactory.getInstance();

        metadata.setEndTime(new Date());
        metadata.setStatus(status);
        metadata.setCompleted(uploadCompleted);
        factory.updateUploadList(metadata);

        setChanged();
        notifyObservers(true);
    }

    /**
     * Performs download split task. It initializes all worker and its individual threads.
     * @throws IOException
     */
    private void employWorkers() throws IOException {
        if (partsMetaList == null) {
            splitFileAndSaveToPartsList();
        }
        else {
            // Initialize all workers with previous part data
            int i = 0;
            for (UploadPartsMetadata partMeta : partsMetaList) {
                workers[i] = new UploadWorker(partMeta);
                i++;
            }

            workersAtWork = partsMetaList.size();
        }

        /*
		 * Initialize all worker threads.
		 */
        for(int i=0; i<workersAtWork; i++) {
            Thread t = new Thread(workers[i]);
            t.setName(metadata.getId() + "" + (i+1));

            workerThreads[i] = t;
        }
    }

    public void splitFileAndSaveToPartsList() {
        Log.d(TAG_UPLOAD,"splitFileAndSaveToPartsList()");
        try {
            partsMetaList = new ArrayList<>();

            int partCounter = 0;
            int sizeOfFiles = ((int) uploadSize / MAX_WORKER) + MAX_WORKER;
            Log.d(TAG_UPLOAD,"size of part = " + sizeOfFiles);
            Log.d(TAG_UPLOAD,"number parts = " + MAX_WORKER);
            byte[] buffer = new byte[sizeOfFiles];
            // create a buffer of bytes sized as the one chunk size

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(metadata.getFilePath()));

            int tmp = 0;
            int startRange = 0;
            int endRange = 0;

            while ((tmp = bis.read(buffer)) > 0) {
                endRange = (partCounter == MAX_WORKER) ? (int) uploadSize : startRange + sizeOfFiles;
                File newFile = new File(UploadConfiguration.TEMP_DIRECTORY, metadata.getFileName() + "." + metadata.getFileType()
                        + "." + String.format("%03d", partCounter));
                // naming files as <inputFileName>.001, <inputFileName>.002, ...
                FileOutputStream out = new FileOutputStream(newFile);
                out.write(buffer, 0, tmp);//tmp is chunk size. Need it for the last chunk,

                UploadPartsMetadata partData = new UploadPartsMetadata(metadata.getId(), partCounter, startRange, endRange, newFile.getAbsolutePath());
                partsMetaList.add(partData);

                workers[partCounter] = new UploadWorker(partData);
                Log.d(TAG_UPLOAD,"file " + partCounter + ": ");
                Log.d(TAG_UPLOAD,"startRange = " + startRange);
                Log.d(TAG_UPLOAD,"endRange = " + endRange);
                Log.d(TAG_UPLOAD,"path = " + newFile.getAbsolutePath());
                Log.d(TAG_UPLOAD,"upload_id = " + metadata.getId());
                startRange += sizeOfFiles + 1;
                partCounter++;
            }

            workersAtWork = MAX_WORKER;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * Starts all worker threads.
     */
    private void startAllWorkers()  {
        Log.d(TAG_UPLOAD,"[INFO] Starting download threads.");

        for(int i=0; i<workersAtWork; i++)
            workerThreads[i].start();
    }


    /**
     * Joins all workers threads and wait until they complete.
     */
    private void waitForWorkers() {
        Log.e(TAG_UPLOAD, "waitForWorkers()");
        for(int i=0; i<workersAtWork; i++) {
            try {
                Log.d(TAG_UPLOAD, "worker join!");
                workerThreads[i].join();
            } catch (InterruptedException e) {
                Log.e(TAG_UPLOAD, "[WARN] Download worker thread was interrupted.");
            }
        }
    }

    /**
     * Updates the GUI with new upload status
     */
    private synchronized void updateUploadGUI() {
        setChanged();
        notifyObservers();
    }


    /**
     * Wait the all part joined on server.
     */
    private void waitJoinUploadedFile() {
        Log.d(TAG_UPLOAD, "[Successful] All files are uploaded...");
        Log.d(TAG_UPLOAD, "[WAIT] All file joining...");

        // Sort all parts before merging
        Collections.sort(partsMetaList, new Comparator<UploadPartsMetadata>(){
            @Override
            public int compare(UploadPartsMetadata o1, UploadPartsMetadata o2) {
                if(o1.getId() > o2.getId())
                    return 1;
                else if(o1.getId() == o2.getId())
                    return 0;
                else
                    return -1;
            }
        });

		/*
		 * Remove the temporary part files
		 */
        for (UploadPartsMetadata partsMetadata : partsMetaList) {
            File file = new File(partsMetadata.getPath());

            if(file.exists())
                file.delete();
        }
    }

    /**
     * Updates the download part file status to XML.
     * @param uploadWorker
     */
    private synchronized void updatePartDownloadStatus(UploadWorker uploadWorker) {
        DBUploadFactory factory = DBUploadFactory.getInstance();

        UploadPartsMetadata meta = uploadWorker.getPartMeta();

        long startRange		= meta.getStart();
        long totalUpload= uploadWorker.getCompleted();
        long uploadSize 	= uploadWorker.getUploadSize();

        boolean completed = (totalUpload == uploadSize);

        // Update the start range of current part
        meta.setStart(startRange + totalUpload);

        if (completed)
            factory.removeSavedUploadParts(meta);
        else
            factory.updateSavedUploadParts(meta);

        setChanged();
        notifyObservers();
    }

    /**
     * Manages individual block of a file.
     * @author Pankaj Prakash
     *
     */
    private class UploadWorker implements Runnable {
        private final static int BUFFER_SIZE = 1024 * 1024;
        private final String TAG_WORKER = UploadWorker.class.getSimpleName();

        private String TEMP_PATH;

        private UploadPartsMetadata partMeta;

        private DataOutputStream outputStream = null;

        @SuppressWarnings("unused")
        private int part;
        private long startRange, endRange;
        private long uploadSize;
        private long completed;

        private byte[] buffer;

        /**
         * Create new instance of Download worker with file part metadata information.
         * @param partMeta
         * @throws IOException
         */
        public UploadWorker(UploadPartsMetadata partMeta) throws IOException {
            Log.d(TAG_WORKER, "init UploadWorker");
            this.partMeta	= partMeta;

            startRange = partMeta.getStart();
            endRange	= partMeta.getEnd();
            uploadSize = endRange - startRange;

            completed	= 0l;
            buffer 	= new byte[BUFFER_SIZE];
            part		= partMeta.getId();

            TEMP_PATH = partMeta.getPath();
        }


        /**
         * Starts the download worker. It starts downloading all parts of the file parallely.
         */
        @Override
        public void run() {
            Log.d(TAG_WORKER, "run UploadWorker");
            // Update the changes to XML file
            updatePartDownloadStatus(this);

            try {
                FileInputStream fileInputStream = new FileInputStream(new File(
                        partMeta.getPath()));
                int bytesRead, bytesAvailable, bufferSize;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";

                Log.d(TAG_WORKER, "run from " + uploadURL);
				/* Create an open an HTTP connection */
                URL link = new URL(uploadURL);
                HttpURLConnection conn = (HttpURLConnection) link.openConnection();

				/* Set connection properties */
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("uploaded_file", partMeta.getPath());
                // Enable POST method
                conn.setRequestMethod("POST");
                // Allow Inputs & Outputs
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setChunkedStreamingMode(1024);


                outputStream = new DataOutputStream(conn.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);

                String connstr = null;
                connstr = "Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + partMeta.getPath() + "\"" + lineEnd;
                Log.i("Connstr", connstr);

                outputStream.writeBytes(connstr);
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, BUFFER_SIZE);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                Log.e("Image length", bytesAvailable + "");
                try {
                    while (bytesRead > 0) {
                        try {
                            outputStream.write(buffer, 0, bufferSize);
                            outputStream.flush();

                            completed += bytesRead;
                            synchronized (uploadCompleted) {
                                uploadCompleted += bytesRead;
                            }

                            updateUploadGUI();

                            // Stop the download if download manager is paused
                            if(status == UploadStatus.PAUSED || status == UploadStatus.ERROR)
                                break;
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                            Log.e(TAG_WORKER, "Out of memory");
                        }
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, BUFFER_SIZE);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG_WORKER, "Error connect to server");
                }
                // Stop the download if download manager is paused
                if(status != UploadStatus.PAUSED && status != UploadStatus.ERROR) {
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"original_id\"" + lineEnd);
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(partMeta.getUploadId() + "");
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens
                            + lineEnd);

                    // Responses from the server (code and message)
                    int serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();
                    Log.i(TAG_WORKER, "Server Response Code " + serverResponseCode);

                    switch (serverResponseCode) {
                        case 200:
                            Log.i(TAG_WORKER, "Response Failed");
                            break;
                        case 201:
                            Log.i(TAG_WORKER, "Response Successful");
                            break;
                        case 500:
                            Log.i(TAG_WORKER, serverResponseMessage);
                            break;
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    Log.i(TAG_WORKER, sb.toString());

                    String CDate = null;
                    Date serverTime = new Date(conn.getDate());
                    try {
                        DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
                        CDate = df.format(serverTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Date Exception", e.getMessage() + " Parse Exception");
                    }
                    Log.i(TAG_WORKER, "Server Response Time" + CDate + "");

                    Log.i("File Name in Server : ", metadata.getFileName());

                    fileInputStream.close();
                    outputStream.flush();
                    outputStream.close();
                    outputStream = null;
                }


            } catch (MalformedURLException e) {
                Log.d(TAG_WORKER, "[ERROR] Invalid upload URL. " + e.getMessage());

                Log.d(TAG_WORKER, "Invalid upload URL. " + e.getMessage() + " Error uploading");

            } catch (IOException e) {
                status = UploadStatus.ERROR;

                Log.d(TAG_WORKER,"[ERROR] Unable to upload part. " + e.getMessage());

                Log.d(TAG_WORKER, "Invalid upload URL. " + e.getMessage() + " Error uploading");
            }

			/*
			 * Update the download part file XML
			 */
            Log.d(TAG_WORKER, "update part upload status");
            updatePartDownloadStatus(this);
        }

        /**
         * @return the bytes downloaded
         */
        public long getCompleted() {
            return completed;
        }

        /**
         * @return the file part metadata information
         */
        public UploadPartsMetadata getPartMeta() {
            return partMeta;
        }

        /**
         * @return the download size
         */
        public long getUploadSize() {
            return uploadSize;
        }
    }

}
