package com.hefei.smatisse;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * <pre>
 *     author: hefei
 *     time  : 2020/08/26
 *     desc  :
 * </pre>
 */
public class ThisApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.addLogAdapter(new AndroidLogAdapter());
    }
}
