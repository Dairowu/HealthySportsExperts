package cn.xietong.healthysportsexperts.app;

import android.app.Application;

/**
 * Created by Administrator on 2015/10/27.
 */
public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public static App getInstance() {
        return instance;
    }
}
