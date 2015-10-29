package cn.xietong.healthysportsexperts.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

/**计步的Serice
 * Created by Administrator on 2015/10/22.
 */
public class Service_Calculate_Step extends Service {

    public static PowerManager.WakeLock wakeLock;
    //步数监听
    private StepListener mStepListeners;
    //定义系统Sensor管理器
    private SensorManager sensorManager;

    private MyBinder binder = new MyBinder();

    public class MyBinder extends Binder {

        public int getCount() {
            return mStepListeners.getCount();
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepListeners = new StepListener();
        sensorManager.registerListener(mStepListeners,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        //得到对电源管理的PowerManager实例
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //加上ACQUIRE_CAUSES_WAKEUP就可以让Screen或Keyboard的illumination没开启的情况，马上开启它们
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK| PowerManager.ACQUIRE_CAUSES_WAKEUP, "MYTAG");
        if(wakeLock!=null) {
            wakeLock.acquire();//申请锁
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(wakeLock!=null) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
