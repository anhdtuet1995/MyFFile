package work.uet.anhdt.ftpstorageofficial.application;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.util.Log;

/**
 * Created by anansaj on 11/18/2017.
 */

public class MyApplication extends com.activeandroid.app.Application {

    private static Context context;
    private static final String TAG = "MyApplication";

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        Log.d(TAG, "active android is enabled");

        ActiveAndroid.initialize(this);
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

}
