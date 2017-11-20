package work.uet.anhdt.ftpstorageofficial.tasks.download;

import android.os.Build;

import com.activeandroid.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

/**
 * Created by anansaj on 11/20/2017.
 */

public class DownloadMetadata {

    private final static String TAG = DownloadMetadata.class.getSimpleName();

    private final long id;
    private final String url;

    private Date startTime;
    private Date endTime;
    private String filePath;

    private DownloadStatus status;
    private long completed;

    private String fileName;
    private String fileType;
    private long fileSize;
    private boolean rangeAllowed;

    public DownloadMetadata(String link) {
        // Generate a new metadata ID
        Log.d(TAG, "init");
        id  = new Date().getTime();
        url = link;
        fileName = "";
        fileType = "";
    }

    public DownloadMetadata(long id, String url, Date startTime, Date endTime, String filePath, DownloadStatus status,
                            long downloaded, String fileName, String fileType, long fileSize, boolean rangeAllowed) {
        Log.d(TAG, "init with many args");
        this.id = id;
        this.url 		= url;
        this.startTime	= startTime;
        this.endTime 	= endTime;
        this.filePath	= filePath;
        this.status		= status;
        completed = downloaded;

        this.fileName	= fileName;
        this.fileType	= fileType;
        this.fileSize	= fileSize;
        this.rangeAllowed = rangeAllowed;
    }


    public void getLoadFileMetadata() throws IOException {
        Log.d(TAG, "getLoadFileMetadata");
        URL link = new URL(url);

        HttpURLConnection conn = (HttpURLConnection) link.openConnection();

		/* Set connection properties */
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        final int responseCode = conn.getResponseCode();

		/* If response is OK then get file metadata */
        if(responseCode == HttpURLConnection.HTTP_OK) {
            Log.d(TAG, "[SUCCESS] Connected to server. Gathering file info. ");

            // Get length of file
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fileSize = conn.getContentLengthLong();
            }
            else {
                fileSize = (long) conn.getContentLength();
            }

            completed = 0l;

			/*
			 * Get file name and type
			 */
            String contentDisposition = conn.getHeaderField("Content-Disposition");

            if(contentDisposition == null || contentDisposition.isEmpty()) {
				/*
				 * If no Content-Disposition header field is set then get
				 * file name and type details from URL.
				 */

                try {
                    Log.d(TAG, "If no Content-Disposition header field is set then get file name and type details from URL.");
                    URI uri 	= link.toURI();
                    String path = uri.getPath();

                    path = path.substring(path.lastIndexOf("/") + 1);

                    String fname = path.substring(0, path.lastIndexOf("."));
                    String ftype = path.substring(path.lastIndexOf(".") + 1);

                    fileName = fname;
                    fileType = ftype;
                } catch (URISyntaxException e) {
                    Log.e(TAG, "[ERROR] Unable to get file info.");

                    throw new IOException(e);
                } // End of try catch

            } else {
				/*
				 * Get details from header fields
				 */
                Log.d(TAG, "Get details from header fields");
                String[] details = contentDisposition.split(";");

                for(String detail : details) {
                    if(detail.contains("filename")) {
                        String fileDetails = detail.split("=")[1];

                        String fname = fileDetails.substring(1, fileDetails.lastIndexOf("."));
                        String ftype = fileDetails.substring(fileDetails.lastIndexOf(".") + 1, fileDetails.length() - 1);

                        fileName = fname;
                        fileType = ftype;
                    }
                }
            } // End of get file name through header field


			/*
			 * Get file range acceptability details
			 */
            String range = conn.getHeaderField("Accept-Ranges");
            Log.d(TAG, "Get range from header fields");

            if(range == null || range.isEmpty()) {
                rangeAllowed = false;
            } else {
                rangeAllowed = true;
            }
        }
    }


    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }


    /**
     * @return the fileType
     */
    public String getFileType() {
        return fileType;
    }


    /**
     * @return the fileSize
     */
    public long getFileSize() {
        return fileSize;
    }


    /**
     * @return the acceptRange
     */
    public boolean isRangeAllowed() {
        return rangeAllowed;
    }


    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }


    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }


    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }


    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }


    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    /**
     * @return the id
     */
    public long getId() {
        return id;
    }


    /**
     * @return the URL
     */
    public String getUrl() {
        return url;
    }


    /**
     * @return the completed
     */
    public DownloadStatus getStatus() {
        return status;
    }


    /**
     * @param completed the completed to set
     */
    public void setStatus(DownloadStatus completed) {
        status = completed;
    }

    /**
     * @return the downloaded
     */
    public long getCompleted() {
        return completed;
    }

    /**
     * @param completed the downloaded to set
     */
    public void setCompleted(long completed) {
        this.completed = completed;
    }

}
