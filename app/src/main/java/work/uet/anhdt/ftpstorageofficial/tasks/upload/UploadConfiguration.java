package work.uet.anhdt.ftpstorageofficial.tasks.upload;

import work.uet.anhdt.ftpstorageofficial.application.MyApplication;

/**
 * Created by anansaj on 11/20/2017.
 */

public class UploadConfiguration {

    public final static String TEMP_DIRECTORY = MyApplication.getAppContext().getCacheDir().getAbsolutePath();
    public final static String DEFAULT_UPLOAD_URL = "http://192.168.4.100/test_ftp/file";

}
