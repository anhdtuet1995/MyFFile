package work.uet.anhdt.ftpstorageofficial.tasks.upload.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by anansaj on 11/20/2017.
 */

@Table(name = "UploadPart")
public class UploadPart extends Model {

    @Column(name = "part_id")
    public int part_id;

    @Column(name = "start")
    public long startt;

    @Column(name = "end")
    public long endt;

    @Column(name = "path")
    public String path;

    @Column(name = "UploadList")
    public UploadList uploadList;

    public UploadPart() {
        super();
    }

    public UploadPart(int id, long start, long endt, String path, UploadList uploadList) {
        super();
        this.part_id = id;
        this.uploadList = uploadList;
        this.startt = start;
        this.endt = endt;
        this.path = path;
    }

}
