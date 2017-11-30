package work.uet.anhdt.ftpstorageofficial.tasks.upload;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import work.uet.anhdt.ftpstorageofficial.tasks.upload.models.UploadFile;
import work.uet.anhdt.ftpstorageofficial.tasks.upload.models.UploadList;
import work.uet.anhdt.ftpstorageofficial.tasks.upload.models.UploadPart;

/**
 * Created by anansaj on 11/20/2017.
 */

public class DBUploadFactory {

    private static final String TAG = DBUploadFactory.class.getSimpleName();

    private static DBUploadFactory _instance;

    public static DBUploadFactory getInstance() {
        Log.d(TAG, "getInstance()");
        if(_instance == null)
            _instance = new DBUploadFactory();

        return _instance;
    }

    /**
     * Get the list of all uploads.
     * @return List of all uploads.
     */
    public synchronized List<UploadMetadata> getUploadList() {
        Log.d(TAG, "getUploadList()");
        List<UploadMetadata> uploadMetadataList = new ArrayList<>();
        List<UploadList> uploadLists = new Select().from(UploadList.class).execute();

        for (UploadList uploadList : uploadLists) {
            UploadFile uploadFile = new Select().from(UploadFile.class).where("UploadList = ?", uploadList.getId()).executeSingle();

            long id = uploadList.upload_id;
            boolean range = uploadList.range;
            Date starttime = new Date();
            starttime.setTime(uploadList.starttime);
            Date endtime = new Date();
            endtime.setTime(uploadList.endtime);
            UploadStatus status = UploadStatus.valueOf(uploadList.status);
            long completed = uploadList.completed;
            String url = uploadList.url;
            String filename = uploadFile.name;
            String filepath = uploadFile.path;
            String type = uploadFile.type;
            long size = uploadFile.size;

            UploadMetadata uploadMetadata = new UploadMetadata(id, url, starttime, endtime, filepath
                    , status, completed, filename, type, size, range);
            uploadMetadataList.add(uploadMetadata);
        }
        return uploadMetadataList;
    }

    public synchronized UploadMetadata getUploadMetadata(long uploadId) {
        Log.d(TAG, "getUploadMetadata(" + uploadId + ")");
        UploadList uploadList = new Select().from(UploadList.class).where("upload_id = ?", uploadId).executeSingle();
        UploadFile uploadFile = new Select().from(UploadFile.class).where("UploadList = ?", uploadList.getId()).executeSingle();

        long id = uploadList.upload_id;
        boolean range = uploadList.range;
        Date starttime = new Date();
        starttime.setTime(uploadList.starttime);
        Date endtime = new Date();
        endtime.setTime(uploadList.endtime);
        UploadStatus status = UploadStatus.valueOf(uploadList.status);
        long completed = uploadList.completed;
        String url = uploadList.url;
        String filename = uploadFile.name;
        String filepath = uploadFile.path;
        String type = uploadFile.type;
        long size = uploadFile.size;

        return new UploadMetadata(id, url, starttime, endtime, filepath
                , status, completed, filename, type, size, range);
    }

    /**
     * Create or update new upload details to XML file.
     * @param metadata UploadMetaData to update.
     */
    public synchronized void updateUploadList(UploadMetadata metadata) {
        Log.d(TAG, "updateUploadList(): " + metadata.getFileName());
        boolean isCreateFile = false;
        UploadList fileNeedUpdate = new Select().from(UploadList.class).where("upload_id = ?", metadata.getId()).executeSingle();
        if (fileNeedUpdate == null) {
            fileNeedUpdate = new UploadList();
            isCreateFile = true;
            fileNeedUpdate.upload_id = metadata.getId();
            fileNeedUpdate.starttime = metadata.getStartTime().getTime();
            fileNeedUpdate.endtime = metadata.getEndTime().getTime();
            fileNeedUpdate.status = metadata.getStatus().name();
            fileNeedUpdate.completed = metadata.getCompleted();
            fileNeedUpdate.status = metadata.getStatus().name();
            fileNeedUpdate.range = metadata.isRangeAllowed();
            fileNeedUpdate.url = metadata.getUrl();
            fileNeedUpdate.save();

            fileNeedUpdate = new Select().from(UploadList.class).where("upload_id = ?", metadata.getId()).executeSingle();
            UploadFile uploadFile = new UploadFile(metadata.getFileName(), metadata.getFileType(), metadata.getFileSize(), metadata.getFilePath(), fileNeedUpdate);
            uploadFile.save();
        }
        else {
            fileNeedUpdate.upload_id = metadata.getId();
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

    public synchronized void clearUploadList() {
        Log.d(TAG, "clearUploadList()");
        new Delete().from(UploadFile.class).execute();
        new Delete().from(UploadList.class).execute();
    }

    /**
     * Gets, the list of all saved upload parts.
     * @param uploadId Unique upload id to be fetched
     * @return List of all upload parts.
     */
    public synchronized List<UploadPartsMetadata> getUploadPartsList(long uploadId) {
        Log.d(TAG, "updateUploadList(): " + uploadId);
        UploadList uploadList = new Select().from(UploadList.class).where("upload_id = ?", uploadId).executeSingle();
        List<UploadPartsMetadata> list = new ArrayList<>();
        List<UploadPart> uploadPartList = new Select().from(UploadPart.class).where("UploadList = ?", uploadList.getId()).execute();
        Log.d(TAG, "updateUploadList(): " + uploadPartList.size());
        for (UploadPart uploadPart: uploadPartList) {
            long start = uploadPart.startt;
            long end = uploadPart.endt;
            String path = uploadPart.path;
            int id = uploadPart.part_id;
            UploadPartsMetadata parts = new UploadPartsMetadata(uploadId, id, start, end, path);
            list.add(parts);
        }

        return list;
    }

    /**
     * Stop upload.
     * @param uploadId
     */
    public synchronized void stopUploadMetadata(long uploadId) {
        Log.d(TAG, "getUploadMetadata(" + uploadId + ")");
        UploadList uploadList = new Select().from(UploadList.class).where("upload_id = ?", uploadId).executeSingle();
        uploadList.status = UploadStatus.ERROR.name();
        uploadList.save();
    }


    /**
     * Update, the saved upload parts.
     * @param metadata
     */
    public synchronized void updateSavedUploadParts(UploadPartsMetadata metadata) {
        Log.d(TAG, "updateSavedUploadParts(): " + metadata.getPath());
        UploadList fileNeedUpdate = new Select().from(UploadList.class).where("upload_id = ?", metadata.getUploadId()).executeSingle();

        List<UploadPart> uploadPartList = new Select().from(UploadPart.class).where("UploadList = ?", fileNeedUpdate.getId()).execute();

        UploadPart uploadPart = null;
        for (UploadPart dP: uploadPartList) {
            if (dP.part_id == metadata.getId()) {
                uploadPart = dP;
                break;
            }
        }

        if (uploadPart == null) {
            uploadPart = new UploadPart(metadata.getId(), metadata.getStart(), metadata.getEnd(), metadata.getPath(), fileNeedUpdate);
        }
        else {
            uploadPart.startt = metadata.getStart();
        }
        uploadPart.save();
    }

    /**
     * Deletes, the saved upload parts if the part is uploaded.
     * @param metadata
     */
    public synchronized void removeSavedUploadParts(UploadPartsMetadata metadata) {
        Log.d(TAG, "removeSavedUploadParts(): " + metadata.getUploadId() + ", upload_id = " + metadata.getId());
        UploadList fileNeedUpdate = new Select().from(UploadList.class).where("upload_id = ?", metadata.getUploadId()).executeSingle();
        new Delete().from(UploadPart.class).where("UploadList = ?", fileNeedUpdate.getId()).where("part_id = ?", metadata.getId()).execute();

    }


}
