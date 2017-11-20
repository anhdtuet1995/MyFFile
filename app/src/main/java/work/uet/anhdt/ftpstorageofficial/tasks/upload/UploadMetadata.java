package work.uet.anhdt.ftpstorageofficial.tasks.upload;

import com.activeandroid.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by anansaj on 11/20/2017.
 */

public class UploadMetadata {

    private final static String TAG = UploadMetadata.class.getSimpleName();

    private final long id;
    private final String uploadUrl;

    private Date startTime;
    private Date endTime;
    private String filePath;

    private UploadStatus status;
    private long completed;

    private String fileName;
    private String fileType;
    private long fileSize;
    private boolean rangeAllowed;

    public UploadMetadata(String filePath, String uploadUrl) {
        // Generate a new metadata ID
        Log.d(TAG, "init");
        id  = new Date().getTime();
        this.uploadUrl = uploadUrl;
        this.filePath = filePath;
        fileName = "";
        fileType = "";
    }

    public UploadMetadata(long id, String url, Date startTime, Date endTime, String filePath, UploadStatus status,
                          long uploaded, String fileName, String fileType, long fileSize, boolean rangeAllowed) {
        Log.d(TAG, "init with many args");
        this.id = id;
        this.uploadUrl 		= url;
        this.startTime	= startTime;
        this.endTime 	= endTime;
        this.filePath	= filePath;
        this.status		= status;
        completed = uploaded;

        this.fileName	= fileName;
        this.fileType	= fileType;
        this.fileSize	= fileSize;
        this.rangeAllowed = rangeAllowed;
    }


    public void getLoadFileMetadata() throws IOException {
        File file = new File(filePath);
        this.completed = 0l;
        this.fileSize = file.length();
        String temp = filePath.substring(filePath.lastIndexOf("/") + 1);
        this.fileName = temp.substring(0, temp.lastIndexOf("."));
        this.fileType = temp.substring(temp.lastIndexOf(".") + 1);
        this.rangeAllowed = true;
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
        return uploadUrl;
    }


    /**
     * @return the completed
     */
    public UploadStatus getStatus() {
        return status;
    }


    /**
     * @param completed the completed to set
     */
    public void setStatus(UploadStatus completed) {
        status = completed;
    }

    /**
     * @return the uploaded
     */
    public long getCompleted() {
        return completed;
    }

    /**
     * @param completed the uploaded to set
     */
    public void setCompleted(long completed) {
        this.completed = completed;
    }

}
