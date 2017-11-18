package work.uet.anhdt.ftpstorageofficial.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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

import work.uet.anhdt.ftpstorageofficial.R;
import work.uet.anhdt.ftpstorageofficial.fragments.DownloadFragment;
import work.uet.anhdt.ftpstorageofficial.fragments.FileFragment;
import work.uet.anhdt.ftpstorageofficial.fragments.UploadFragment;
import work.uet.anhdt.ftpstorageofficial.util.Constant;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FileFragment.OnFileFragmentInteractionListener,
                    UploadFragment.OnUploadFragmentInteractionListener, DownloadFragment.OnDownloadFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FileFragment fileFragment;
    private UploadFragment uploadFragment;
    private DownloadFragment downloadFragment;

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        requestForPermission();

        initFirstFragment();

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
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (id == R.id.nav_file) {
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
        }
        else if (id == R.id.nav_upload) {
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

        }
        else if (id == R.id.nav_download) {
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
        }
        fragmentTransaction.commit();
        ((DrawerLayout)findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFileFragmentInteraction(Uri uri) {

    }

    @Override
    public void onUploadFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDownloadFragmentInteraction(Uri uri) {

    }
}

