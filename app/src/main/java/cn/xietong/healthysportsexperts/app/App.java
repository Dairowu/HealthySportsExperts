package cn.xietong.healthysportsexperts.app;

import android.app.Application;
import android.content.SharedPreferences;

import cn.xietong.healthysportsexperts.model.DatabaseHelper;
import cn.xietong.healthysportsexperts.utils.StepListener;

/**
 * Created by Administrator on 2015/10/27.
 */
public class App extends Application {

    private static App instance;
    private static SharedPreferences mPreferences;
    private static  DatabaseHelper dbHelper;
    private static StepListener stepListener;
    public static final String DATABASE_NAME = "healthysportsexperts_db";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mPreferences = getApplicationContext().getSharedPreferences("data",MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this,DATABASE_NAME);
        stepListener = new StepListener();
    }

    public static App getInstance() {
        return instance;
    }

    /**
     * 获取SharedPreferences实例
     * @return
     */
    public static SharedPreferences getSharedPreferencesInstance(){
        return mPreferences;
    }

    public static DatabaseHelper getDBHelper(){
        return dbHelper;
    }

    public static StepListener getStepListener(){return stepListener;}
}
