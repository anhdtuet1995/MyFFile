package work.uet.anhdt.ftpstorageofficial.tasks.upload;

/**
 * Created by anansaj on 11/20/2017.
 */

public enum UploadStatus {

    /**
     * The file upload process has not initialized yet.
     */
    NEW,

    /**
     * The upload is ready to begin. Can be set once.
     */
    READY,

    /**
     * The file upload is in progress.
     */
    UPLOADING,

    /**
     * All block of the file has been uploaded and merged successfully.
     */
    COMPLETED,

    /**
     * Download is paused explicitly.
     */
    PAUSED,

    /**
     * Some error occurred while attempting to upload file.
     */
    ERROR
    
}
