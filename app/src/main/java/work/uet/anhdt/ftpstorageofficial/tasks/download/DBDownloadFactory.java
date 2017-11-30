package work.uet.anhdt.ftpstorageofficial.tasks.download;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import work.uet.anhdt.ftpstorageofficial.tasks.download.models.DownloadFile;
import work.uet.anhdt.ftpstorageofficial.tasks.download.models.DownloadList;
import work.uet.anhdt.ftpstorageofficial.tasks.download.models.DownloadPart;

/**
 * Created by anansaj on 11/20/2017.
 */

public class DBDownloadFactory {

    private static final String TAG = DBDownloadFactory.class.getSimpleName();

    private static DBDownloadFactory _instance;

    public static DBDownloadFactory getInstance() {
        Log.d(TAG, "getInstance()");
        if(_instance == null)
            _instance = new DBDownloadFactory();

        return _instance;
    }

    /**
     * Get the list of all downloads.
     * @return List of all downloads.
     */
    public synchronized List<DownloadMetadata> getDownloadList() {
        Log.d(TAG, "getDownloadList()");
        List<DownloadMetadata> downloadMetadataList = new ArrayList<>();
        List<DownloadList> downloadLists = new Select().from(DownloadList.class).execute();

        for (DownloadList downloadList : downloadLists) {
            DownloadFile downloadFile = new Select().from(DownloadFile.class).where("DownloadList = ?", downloadList.getId()).executeSingle();

            long id = downloadList.dowload_id;
            boolean range = downloadList.range;
            Date starttime = new Date();
            starttime.setTime(downloadList.starttime);
            Date endtime = new Date();
            endtime.setTime(downloadList.endtime);
            DownloadStatus status = DownloadStatus.valueOf(downloadList.status);
            long completed = downloadList.completed;
            String url = downloadList.url;
            String filename = downloadFile.name;
            String filepath = downloadFile.path;
            String type = downloadFile.type;
            long size = downloadFile.size;

            DownloadMetadata downloadMetadata = new DownloadMetadata(id, url, starttime, endtime, filepath
                    , status, completed, filename, type, size, range);
            downloadMetadataList.add(downloadMetadata);
        }
        return downloadMetadataList;
    }

    public synchronized DownloadMetadata getDownloadMetadata(long downloadId) {
        Log.d(TAG, "getDownloadMetadata(" + downloadId + ")");
        DownloadList downloadList = new Select().from(DownloadList.class).where("download_id = ?", downloadId).executeSingle();
        DownloadFile downloadFile = new Select().from(DownloadFile.class).where("DownloadList = ?", downloadList.getId()).executeSingle();

        long id = downloadList.dowload_id;
        boolean range = downloadList.range;
        Date starttime = new Date();
        starttime.setTime(downloadList.starttime);
        Date endtime = new Date();
        endtime.setTime(downloadList.endtime);
        DownloadStatus status = DownloadStatus.valueOf(downloadList.status);
        long completed = downloadList.completed;
        String url = downloadList.url;
        String filename = downloadFile.name;
        String filepath = downloadFile.path;
        String type = downloadFile.type;
        long size = downloadFile.size;

        return new DownloadMetadata(id, url, starttime, endtime, filepath
                , status, completed, filename, type, size, range);
    }

    /**
     * Create or update new download details to XML file.
     * @param metadata DownloadMetaData to update.
     */
    public synchronized void updateDownloadList(DownloadMetadata metadata) {
        Log.d(TAG, "updateDownloadList(): " + metadata.getFileName());
        boolean isCreateFile = false;
        DownloadList fileNeedUpdate = new Select().from(DownloadList.class).where("download_id = ?", metadata.getId()).executeSingle();
        if (fileNeedUpdate == null) {
            fileNeedUpdate = new DownloadList();
            isCreateFile = true;
            fileNeedUpdate.dowload_id = metadata.getId();
            fileNeedUpdate.starttime = metadata.getStartTime().getTime();
            fileNeedUpdate.endtime = metadata.getEndTime().getTime();
            fileNeedUpdate.status = metadata.getStatus().name();
            fileNeedUpdate.completed = metadata.getCompleted();
            fileNeedUpdate.status = metadata.getStatus().name();
            fileNeedUpdate.range = metadata.isRangeAllowed();
            fileNeedUpdate.url = metadata.getUrl();
            fileNeedUpdate.save();

            fileNeedUpdate = new Select().from(DownloadList.class).where("download_id = ?", metadata.getId()).executeSingle();
            DownloadFile downloadFile = new DownloadFile(metadata.getFileName(), metadata.getFileType(), metadata.getFileSize(), metadata.getFilePath(), fileNeedUpdate);
            downloadFile.save();
        }
        else {
            fileNeedUpdate.dowload_id = metadata.getId();
            fileNeedUpdate.starttime = metadata.getStartTime().getTime();
            fileNeedUpdate.endtime = metadata.getEndTime().getTime();
            fileNeedUpdate.status = metadata.getStatus().name();
            fileNeedUpdate.completed = metadata.getCompleted();
            fileNeedUpdate.status = metadata.getStatus().name();
            fileNeedUpdate.range = metadata.isRangeAllowed();
            fileNeedUpdate.url = metadata.getUrl();
            fileNeedUpdate.save();
        }
    }

    public synchronized void clearDownloadList() {
        Log.d(TAG, "clearDownloadList()");
        new Delete().from(DownloadFile.class).execute();
        new Delete().from(DownloadList.class).execute();
    }

    /**
     * Gets, the list of all saved download parts.
     * @param downloadId Unique download id to be fetched
     * @return List of all download parts.
     */
    public synchronized List<DownloadPartsMetadata> getDownloadPartsList(long downloadId) {
        Log.d(TAG, "updateDownloadList(): " + downloadId);
        DownloadList downloadList = new Select().from(DownloadList.class).where("download_id = ?", downloadId).executeSingle();
        List<DownloadPartsMetadata> list = new ArrayList<>();
        List<DownloadPart> downloadPartList = new Select().from(DownloadPart.class).where("DownloadList = ?", downloadList.getId()).execute();
        for (DownloadPart downloadPart: downloadPartList) {
            long start = downloadPart.startt;
            long end = downloadPart.endt;
            String path = downloadPart.path;
            int id = downloadPart.part_id;
            DownloadPartsMetadata parts = new DownloadPartsMetadata(downloadId, id, start, end, path);
            list.add(parts);
        }

        return list;
    }

    /**
     * Update, the saved download parts.
     * @param metadata
     */
    public synchronized void updateSavedDownloadParts(DownloadPartsMetadata metadata) {
        Log.d(TAG, "updateSavedDownloadParts(): " + metadata.getPath());
        DownloadList fileNeedUpdate = new Select().from(DownloadList.class).where("download_id = ?", metadata.getDownloadId()).executeSingle();

        List<DownloadPart> downloadPartList = new Select().from(DownloadPart.class).where("DownloadList = ?", fileNeedUpdate.getId()).execute();

        DownloadPart downloadPart = null;
        for (DownloadPart dP: downloadPartList) {
            if (dP.part_id == metadata.getId()) {
                downloadPart = dP;
                break;
            }
        }

        if (downloadPart == null) {
            downloadPart = new DownloadPart(metadata.getId(), metadata.getStart(), metadata.getEnd(), metadata.getPath(), fileNeedUpdate);
        }
        else {
            downloadPart.startt = metadata.getStart();
        }
        downloadPart.save();
    }

    /**
     * Deletes, the saved download parts if the part is downloaded.
     * @param metadata
     */
    public synchronized void removeSavedDownloadParts(DownloadPartsMetadata metadata) {
        Log.d(TAG, "removeSavedDownloadParts(): " + metadata.getDownloadId() + ", download_id = " + metadata.getId());
        DownloadList fileNeedUpdate = new Select().from(DownloadList.class).where("download_id = ?", metadata.getDownloadId()).executeSingle();
        new Delete().from(DownloadPart.class).where("DownloadList = ?", fileNeedUpdate.getId()).execute();

    }

}
