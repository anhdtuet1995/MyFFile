package work.uet.anhdt.ftpstorageofficial.tasks.download.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by anansaj on 11/20/2017.
 */

@Table(name = "DownloadFile")
public class DownloadFile extends Model {
    @Column(name = "name")
    public String name;

    @Column(name = "type")
    public String type;

    @Column(name = "size")
    public long size;

    @Column(name="path")
    public String path;

    @Column(name = "DownloadList")
    public DownloadList downloadList;


    public DownloadFile() {
        super();
    }

    public DownloadFile(String name, String type, long size, String path, DownloadList downloadList) {
        super();
        this.type = type;
        this.name = name;
        this.size = size;
        this.path = path;
        this.downloadList = downloadList;
    }
}
