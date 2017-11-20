package work.uet.anhdt.ftpstorageofficial.tasks.download.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by anansaj on 11/20/2017.
 */
@Table(name = "DownloadPart")
public class DownloadPart extends Model {

    @Column(name = "part_id")
    public int part_id;

    @Column(name = "start")
    public long startt;

    @Column(name = "end")
    public long endt;

    @Column(name = "path")
    public String path;

    @Column(name = "DownloadList")
    public DownloadList downloadList;

    public DownloadPart() {
        super();
    }

    public DownloadPart(int id, long start, long endt, String path, DownloadList downloadList) {
        super();
        this.part_id = id;
        this.downloadList = downloadList;
        this.startt = start;
        this.endt = endt;
        this.path = path;
    }

}
