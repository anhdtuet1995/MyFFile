package work.uet.anhdt.ftpstorageofficial.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import work.uet.anhdt.ftpstorageofficial.R;
import work.uet.anhdt.ftpstorageofficial.fragments.DownloadFragment;
import work.uet.anhdt.ftpstorageofficial.fragments.FileFragment;
import work.uet.anhdt.ftpstorageofficial.fragments.UploadFragment;
import work.uet.anhdt.ftpstorageofficial.tasks.download.DBDownloadFactory;
import work.uet.anhdt.ftpstorageofficial.tasks.download.DownloadConfiguration;
import work.uet.anhdt.ftpstorageofficial.tasks.download.DownloadManager;
import work.uet.anhdt.ftpstorageofficial.tasks.download.DownloadMetadata;
import work.uet.anhdt.ftpstorageofficial.tasks.download.DownloadPartsMetadata;
import work.uet.anhdt.ftpstorageofficial.tasks.download.DownloadPool;
import work.uet.anhdt.ftpstorageofficial.tasks.upload.DBUploadFactory;
import work.uet.anhdt.ftpstorageofficial.tasks.upload.UploadManager;
import work.uet.anhdt.ftpstorageofficial.tasks.upload.UploadMetadata;
import work.uet.anhdt.ftpstorageofficial.tasks.upload.UploadPartsMetadata;
import work.uet.anhdt.ftpstorageofficial.tasks.upload.UploadPool;
import work.uet.anhdt.ftpstorageofficial.util.Constant;
import work.uet.anhdt.ftpstorageofficial.util.LogMsg;

import static work.uet.anhdt.ftpstorageofficial.util.Constant.SERVER;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FileFragment.OnFileFragmentInteractionListener,
        UploadFragment.OnUploadPoolChanged, DownloadFragment.OnDownloadPoolChanged, Observer {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FileFragment fileFragment;
    private UploadFragment uploadFragment;
    private DownloadFragment downloadFragment;

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private UploadPool uploadPool;
    private DownloadPool downloadPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            if (sd.canWrite()) {
                String currentDBPath = "//data//data//" + getPackageName() + "//databases//myftpstorage";
                String backupDBPath = "myftpstorage.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }


        LogMsg.d("onCreate()");
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        requestForPermission();

        initFirstFragment();

        initPoolForUpAndDown();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Constant.FILE_TAB);
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initPoolForUpAndDown() {
        downloadPool = DownloadPool.getDownloadPool();
        uploadPool = UploadPool.getUploadPool();
    }


    private void initFirstFragment() {

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // Create a new Fragment to be placed in the activity layout
            fileFragment = new FileFragment();

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fileFragment, Constant.FILE_TAB).commit();
        }

    }

    private void requestForPermission() {
        Log.d(TAG, "request permission after use app");
        LogMsg.d(TAG, "request permission after use app");
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                promptForPermissionsDialog(getString(R.string.error_request_permission), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                100);
                    }
                });

            } else {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            }
        }
    }

    private void promptForPermissionsDialog(String message, DialogInterface.OnClickListener onClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(message)
                .setPositiveButton(getString(R.string.ok), onClickListener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_file) {
            getSupportActionBar().setTitle("My FTP Storage");
            showFileFragment();
        }
        else if (id == R.id.nav_upload) {
            getSupportActionBar().setTitle("Uploads");
            showUploadFragment();
        }
        else if (id == R.id.nav_download) {
            getSupportActionBar().setTitle("Downloads");
            showDownloadFragment();
        }

        ((DrawerLayout)findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFileFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Log.d(TAG, "FileFragment is Selected");
        if (fragmentManager.findFragmentByTag(Constant.FILE_TAB) == null) {
            fileFragment = new FileFragment();
            fragmentTransaction.add(R.id.fragment_container, fileFragment, Constant.FILE_TAB);
        }
        else {
            if (uploadFragment != null) {
                fragmentTransaction.hide(uploadFragment);
            }
            if (downloadFragment != null) {
                fragmentTransaction.hide(downloadFragment);
            }
            fragmentTransaction.show(fileFragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void showUploadFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Log.d(TAG, "UploadFragment is Selected");
        if (fragmentManager.findFragmentByTag(Constant.UPLOAD_TAB) == null) {
            uploadFragment = new UploadFragment();
            fragmentTransaction.add(R.id.fragment_container, uploadFragment, Constant.UPLOAD_TAB);
        }
        else {
            if (fileFragment != null) {
                fragmentTransaction.hide(fileFragment);
            }
            if (downloadFragment != null) {
                fragmentTransaction.hide(downloadFragment);
            }
            fragmentTransaction.show(uploadFragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void showDownloadFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Log.d(TAG, "DownloadFragment is Selected");
        if (fragmentManager.findFragmentByTag(Constant.DOWNLOAD_TAB) == null) {
            downloadFragment = new DownloadFragment();
            fragmentTransaction.add(R.id.fragment_container, downloadFragment, Constant.DOWNLOAD_TAB);
        }
        else {
            if (fileFragment != null) {
                fragmentTransaction.hide(fileFragment);
            }
            if (uploadFragment != null) {
                fragmentTransaction.hide(uploadFragment);
            }
            fragmentTransaction.show(downloadFragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(requestCode == 10 && resultCode == RESULT_OK){
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            Toast.makeText(this, "Start upload file " + filePath, Toast.LENGTH_SHORT).show();
            addNewUpload(filePath);
        }
    }

    private void addNewDownload(String url) {
        DownloadManager downManager = new DownloadManager(url, DownloadConfiguration.DEFAULT_DOWNLOAD_PATH);
        downManager.addObserver(this);
        if (downloadFragment == null) {
            downloadFragment = new DownloadFragment();
        }
        downManager.addObserver(downloadFragment);
        downloadPool.add(downManager);
        update(downManager, true);
        downloadFragment.update(downManager, true);
        Intent intent = new Intent(Constant.BROADCAST_DOWNLOAD);
        sendBroadcast(intent);
    }

    private void addNewUpload(String path) {
        UploadManager uploadManager = new UploadManager(path);
        uploadManager.addObserver(this);
        if (uploadFragment == null) {
            uploadFragment = new UploadFragment();
        }
        uploadManager.addObserver(uploadFragment);
        uploadPool.add(uploadManager);
        update(uploadManager, true);
        uploadFragment.update(uploadManager, true);
    }


    @Override
    public void onFileFragmentInteraction(String url) {
        Toast.makeText(this, "Start download file " + url, Toast.LENGTH_SHORT).show();
        addNewDownload(SERVER + url.substring(1));
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof DownloadManager) {
            DownloadManager downloadManager = (DownloadManager) observable;
            Intent intent = new Intent(Constant.BROADCAST_UPDATE_STATUS_DOWNLOAD);
            intent.putExtra("download_id", downloadManager.getDownloadId());
            intent.putExtra("status",downloadManager.getStatus().ordinal());
            sendBroadcast(intent);
        }
        else if (observable instanceof UploadManager) {
            UploadManager uploadManager = (UploadManager) observable;
            Intent intent = new Intent(Constant.BROADCAST_UPDATE_STATUS_UPLOAD);
            intent.putExtra("upload_id", uploadManager.getUploadId());
            intent.putExtra("status",uploadManager.getStatus().ordinal());
            sendBroadcast(intent);
        }
    }

    @Override
    public void onPauseUploadId(long id) {
        uploadPool.remove(id);
    }

    @Override
    public void onStartUploadId(long id) {
        resumeUploadId(id);
    }

    @Override
    public void onStopUploadId(long id) {
        uploadPool.stop(id);
    }

    /**
     * Resumes an existing upload and adds it to the upload pool.
     * @param uploadId Unique upload id to resume.
     */
    public void resumeUploadId(long uploadId) {
        Log.d(TAG, "resumeUploadId(): " + uploadId);
        DBUploadFactory factory = DBUploadFactory.getInstance();

        UploadMetadata meta = factory.getUploadMetadata(uploadId);
        List<UploadPartsMetadata> parts = factory.getUploadPartsList(uploadId);

        UploadManager manager = new UploadManager(meta, parts);
        manager.addObserver(this);

        uploadPool.add(manager);

    }

    @Override
    public void onPauseDownloadId(long id) {
        downloadPool.remove(id);
    }

    @Override
    public void onStartDownloadId(long id) {
        resumeDownloadId(id);
    }

    @Override
    public void onStopDownloadId(long id) {
        downloadPool.stop(id);
    }

    /**
     * Resumes an existing download and adds it to the download pool.
     * @param downloadID Unique upload id to resume.
     */
    public void resumeDownloadId(long downloadID) {
        Log.d(TAG, "resumeUploadId(): " + downloadID);
        DBDownloadFactory factory = DBDownloadFactory.getInstance();

        DownloadMetadata meta = factory.getDownloadMetadata(downloadID);
        Log.d(TAG, "resumeUploadId(): meta info");
        Log.d(TAG, "resumeUploadId(): meta info + id=" + meta.getId());
        Log.d(TAG, "resumeUploadId(): meta info + status" + meta.getStatus().name());
        Log.d(TAG, "resumeUploadId(): meta info + file_name" + meta.getFileName());
        List<DownloadPartsMetadata> parts = factory.getDownloadPartsList(downloadID);
        Log.d(TAG, "resumeUploadId(): + parts size" + parts.size());
        DownloadManager manager = new DownloadManager(meta, parts);
        manager.addObserver(this);

        downloadPool.add(manager);

    }
}

