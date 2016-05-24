package cn.xietong.healthysportsexperts.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.app.App;
import cn.xietong.healthysportsexperts.model.MyUser;
import cn.xietong.healthysportsexperts.ui.activity.MainActivity;

/**计步的Serice
 * Created by Administrator on 2015/10/22.
 */
public class Service_Calculate_Step extends Service {

    private static final double CALORIE_ARGUMENT = 0.36/100000*1.036;

    public static PowerManager.WakeLock wakeLock;
    //步数监听
    private StepListener mStepListeners = App.getInstance().getStepListener();
    //定义系统Sensor管理器
    private SensorManager sensorManager;

    Notification notify = null;

    private MyBinder binder = new MyBinder();
    //当前步数
    private int count;
    //目标步数
    private int goalCount = 1000;

    private MyUser mUser;

    private double calorie;

    public class MyBinder extends Binder {

        public int getCount() {
            count = mStepListeners.getCount();
            calorie = count *mUser.getHeight()*mUser.getWeight()*CALORIE_ARGUMENT;
            RemoteViews  mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_step);
            mRemoteViews.setProgressBar(R.id.progress_step,goalCount,count,false);
            mRemoteViews.setTextViewText(R.id.tv_ntf_step,count+ "步" );
            mRemoteViews.setTextViewText(R.id.tv_kal, String.format("%.2f",calorie) + "kal");
            notify.contentView = mRemoteViews;
            //一定要有这句才会刷新
            startForeground(1, notify);
            return count;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mUser =  App.getInstance().getUserManager().getCurrentUser(MyUser.class);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
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

                NotificationCompat.Builder mBuider = new NotificationCompat.Builder(getApplicationContext());
                RemoteViews  mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_step);
                mRemoteViews.setProgressBar(R.id.progress_step,goalCount,0,false);
                mRemoteViews.setTextViewText(R.id.tv_ntf_step,count + "步");
                mRemoteViews.setTextViewText(R.id.tv_kal,0+"kal");
                mRemoteViews.setTextViewText(R.id.tv_kal,0+"kal");

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent pendingIntent =  PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                mBuider.setContent(mRemoteViews)
                        .setContentIntent(pendingIntent)
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setTicker("正在计步")
                        .setSmallIcon(R.drawable.icon_ntf_run);

                notify = mBuider.build();
                notify.flags = Notification.FLAG_ONGOING_EVENT;
                startForeground(1, notify);

    }


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
        if(notify!=null) {
            stopForeground(true);
            notify = null;
        }
    }
}
