package com.ytspilot;

import android.app.Application;

import com.orm.SugarContext;

/**
 * Created by Devani (sdevani92@gmail.com) on 8/10/2016.
 */
public class Myapplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        JobManager.create(this).addJobCreator(new DemoJobCreator());

        SugarContext.init(this);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

}