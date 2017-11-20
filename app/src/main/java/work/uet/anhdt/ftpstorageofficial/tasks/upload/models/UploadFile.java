package work.uet.anhdt.ftpstorageofficial.tasks.upload.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by anansaj on 11/20/2017.
 */

@Table(name = "UploadFile")
public class UploadFile extends Model {

    @Column(name = "name")
    public String name;

    @Column(name = "type")
    public String type;

    @Column(name = "size")
    public long size;

    @Column(name="path")
    public String path;

    @Column(name = "UploadList")
    public UploadList uploadList;


    public UploadFile() {
        super();
    }

    public UploadFile(String name, String type, long size, String path, UploadList uploadList) {
        super();
        this.type = type;
        this.name = name;
        this.size = size;
        this.path = path;
        this.uploadList = uploadList;
    }

}
