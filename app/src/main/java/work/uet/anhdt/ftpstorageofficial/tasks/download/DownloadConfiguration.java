package work.uet.anhdt.ftpstorageofficial.tasks.download;

import android.os.Environment;

/**
 * Created by anansaj on 11/20/2017.
 */

public class DownloadConfiguration {

    public final static String TEMP_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/";
    public final static String DEFAULT_DOWNLOAD_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/";


}
