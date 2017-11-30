package work.uet.anhdt.ftpstorageofficial.util;

import com.activeandroid.util.Log;

/**
 * Created by anansaj on 11/23/2017.
 */

public class LogMsg {

    private static int CALL = 5;
    private static String DEFAULT_TAG = "TAG";

    public static void d(String msg) {
        Log.d(DEFAULT_TAG, getLogMsg(msg));
    }

    public static void d(String tag, String msg) {
        Log.d(tag, getLogMsg(msg));
    }

    public static void e(String tag, String msg) {
        Log.e(tag, getLogMsg(msg));
    }

    private static String getLogMsg(String msg) {
        return "[" + getCallingLine() + "] " + "[ " + getCallingClassName() + " ]" + "( " + getCallingMethodName() +" )" + " - " + msg;
    }

    private static String getCallingClassName() {
        return Thread.currentThread().getStackTrace()[CALL].getClassName().substring(
                Thread.currentThread().getStackTrace()[CALL].getClassName().lastIndexOf('.') + 1
        );
    }

    private static String getCallingMethodName() {
        return Thread.currentThread().getStackTrace()[CALL].getMethodName();
    }

    private static int getCallingLine() {
        return Thread.currentThread().getStackTrace()[CALL].getLineNumber();
    }
}
