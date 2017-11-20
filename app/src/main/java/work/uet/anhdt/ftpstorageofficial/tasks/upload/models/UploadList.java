package work.uet.anhdt.ftpstorageofficial.tasks.upload.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by anansaj on 11/20/2017.
 */

@Table(name = "UploadList")
public class UploadList extends Model {

    @Column(name = "upload_id")
    public long upload_id;

    @Column(name = "range")
    public boolean range;

    @Column(name = "starttime")
    public long starttime;

    @Column(name = "endtime")
    public long endtime;

    @Column(name = "status")
    public String status;

    @Column(name = "completed")
    public long completed;

    @Column(name = "url")
    public String url;

    public UploadList() {
        super();
    }

}
