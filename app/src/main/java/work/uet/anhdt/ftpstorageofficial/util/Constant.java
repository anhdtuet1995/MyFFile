package work.uet.anhdt.ftpstorageofficial.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by anansaj on 11/18/2017.
 */

public class Constant {

    //fragments in main activity
    public static final String FILE_TAB = "My FTP Storage";
    public static final String UPLOAD_TAB = "Uploads";
    public static final String DOWNLOAD_TAB = "Downloads";


    public static final String SERVER = "http://192.168.4.100/test_ftp/";
    public static final String UPLOAD_PATH = "/uploads";
    public static final File DOWNLOAD_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    public static final String GET_FILES = "files";

    public static final String POST_FILE = "file";
    public static final String POST_FILE_PARAM_FILENAME = "file_name";
    public static final String POST_FILE_PARAM_FILE = "uploaded_file";

    public static final String BROADCAST_DOWNLOAD = "work.uet.anhdt.ftpstorageofficial.broadcast_download";
    public static final String BROADCAST_UPDATE_STATUS_DOWNLOAD = "work.uet.anhdt.ftpstorageofficial.broadcast_update_status_download";

    public static final String BROADCASR_UPLOAD = "work.uet.anhdt.ftpstorageofficial.broadcast_upload";
    public static final String BROADCAST_UPDATE_STATUS_UPLOAD = "work.uet.anhdt.ftpstorageofficial.broadcast_update_status_upload";

}
