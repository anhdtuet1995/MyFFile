package work.uet.anhdt.ftpstorageofficial.models;

/**
 * Created by anansaj on 11/18/2017.
 */

public class FileInfo {

    private int file_id;

    public int getFileId() { return this.file_id; }

    public void setFileId(int file_id) { this.file_id = file_id; }

    private String file_name;

    public String getFileName() { return this.file_name; }

    public void setFileName(String file_name) { this.file_name = file_name; }

    private String file_path;

    public String getFilePath() { return this.file_path; }

    public void setFilePath(String file_path) { this.file_path = file_path; }

    private String createdAt;

    public String getCreatedAt() { return this.createdAt; }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

}
