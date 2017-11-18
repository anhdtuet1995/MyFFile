package work.uet.anhdt.ftpstorageofficial.models;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by anansaj on 11/18/2017.
 */

public class GetFiles {

    private boolean error;

    public boolean getError() { return this.error; }

    public void setError(boolean error) { this.error = error; }

    private ArrayList<FileInfo> files;

    public ArrayList<FileInfo> getFiles() { return this.files; }

    public void setFiles(ArrayList<FileInfo> files) { this.files = files; }

}
