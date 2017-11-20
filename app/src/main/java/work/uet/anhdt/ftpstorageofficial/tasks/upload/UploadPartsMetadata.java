package work.uet.anhdt.ftpstorageofficial.tasks.upload;

/**
 * Created by anansaj on 11/20/2017.
 */

public class UploadPartsMetadata {

    private long  start;

    private final long 		uploadId;
    private final int 		id;
    private final long 		end;
    private final String	path;

    public UploadPartsMetadata(long uploadId, int id, long start, long end, String path) {
        this.uploadId	= uploadId;
        this.id			= id;
        this.start		= start;
        this.end		= end;
        this.path		= path;
    }


    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the start
     */
    public long getStart() {
        return start;
    }

    /**
     * @return the end
     */
    public long getEnd() {
        return end;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }


    /**
     * @return the downloadId
     */
    public long getUploadId() {
        return uploadId;
    }


    /**
     * @param start the start to set
     */
    public void setStart(long start) {
        this.start = start;
    }


}
