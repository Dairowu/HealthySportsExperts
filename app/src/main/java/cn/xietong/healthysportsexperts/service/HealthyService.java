package cn.xietong.healthysportsexperts.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.Trace;
import com.baidu.trace.TraceLocation;

import cn.xietong.healthysportsexperts.ui.fragment.FragmentPageRun;
import cn.xietong.healthysportsexperts.ui.view.BasePopupWindow;
import cn.xietong.healthysportsexperts.utils.LogUtils;
import cn.xietong.healthysportsexperts.utils.TimeUtil;

/**
 * @author 林思旭
 * @since 2017/1/3
 */

public class HealthyService extends Service {
    private String TAG = "HealthyService";
    private RefreshThread myThread;
    //    private SetThread setThread;
    private boolean refresh = true,isSend = true;//设置是否一直循环下去
    private boolean isSendTime = false,isRunTime = true;//10.31，时间显示的有关参数
    private boolean isFirst = true;
    private int number = 0;
    private int message = 0;//设置广播接收的值
    private int defaultFlag = 0;//设置广播默认接收值
    public static String IsOpenService = "HealthyService";
    private ServiceBroadcast myreceiver;
    private IntentFilter intentFilter;
    private int recordTime = 0;//Magic,2016.10.31
    private String sendTime = null;//Magic,2016.10.31
    private TimeThread timeThread;//2016.10.31
    private double lastLongitude,lastLatitude;//10.31
    private double distanceSum = 0.00;//10.31
    /**
     * 采集周期（单位 : 秒）
     */
    private int gatherInterval = 5;

    /**
     * 打包周期（单位 : 秒）
     */
    private int packInterval = 30;
    /**
     * 轨迹服务
     */
    public static Trace trace = null;

    /**
     * entity标识
     */
    public static String entityName = null;

    /**
     * 鹰眼服务ID，开发者创建的鹰眼服务对应的服务ID
     */
    public static long serviceId = 132232; // serviceId为开发者创建的鹰眼服务ID

    /**
     * 轨迹服务类型（0 : 不建立socket长连接， 1 : 建立socket长连接但不上传位置数据，2 : 建立socket长连接并上传位置数据）
     */
    private int traceType = 2;

    /**
     * 轨迹服务客户端
     */
    public static LBSTraceClient client = null;

    /**
     * Entity监听器
     */
    public static OnEntityListener entityListener = null;
    /**
     * 设置请求协议
     */
    private int type = 0;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
//        SDKInitializer.initialize(context);

        // 初始化entity标识
        entityName = "HealthySportsExperts";

        // 初始化轨迹服务客户端
        client = new LBSTraceClient(context);
        LogUtils.i(TAG,"client="+client+";context="+context);

        // 设置定位模式
        client.setLocationMode(LocationMode.High_Accuracy);


        client.setInterval(gatherInterval, packInterval);

        client.setProtocolType(type);

        trace = new Trace(this,serviceId,entityName,traceType);//改2016.9.4

        client.setInterval(gatherInterval, packInterval);

        client.setProtocolType(type);

        initOnEntityListener();

        initServiceBoardcastReceiver();//注册广播

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myThread = new RefreshThread();
        myThread.start();
        timeThread = new TimeThread();//2016.10.31
        timeThread.start();//2016.10.31(开启计时的线程)
//        setRefresh = true;
//        setRefresh = new SetThread();
        LogUtils.i(TAG,"intent_query");
        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 初始化OnEntityListener
     */
    private void initOnEntityListener() {
        LogUtils.i(TAG,"initOnEntityListener");
        entityListener = new OnEntityListener() {
            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
                Looper.prepare();
                LogUtils.i(TAG,"onRequestFailedCallback="+arg0);
                Intent intent_fail = new Intent(FragmentPageRun.REFREH_MESSAGE);
                intent_fail.putExtra("RequsetFail",1);
                sendBroadcast(intent_fail);
//                initOnEntityListener();
                Looper.loop();
            }

            // 添加entity回调接口
            @Override
            public void onAddEntityCallback(String arg0) {
                // TODO Auto-generated method stub
                Looper.prepare();
                LogUtils.i(TAG,"onAddEntityCallback="+arg0);
                Looper.loop();
            }

            // 查询entity列表回调接口
            @Override
            public void onQueryEntityListCallback(String message) {
                // TODO Auto-generated method stub
                LogUtils.i(TAG,"onQueryEntityListCallback="+message);

            }
            //下面这个方法一直在调用（5秒刷新一次）
            @Override
            public void onReceiveLocation(TraceLocation location) {
                // TODO Auto-generated method stub
//                LogUtils.i(getClass().getName(),"onReceiveLocation="+number);
//                LogUtils.i(getClass().getName(),"map="+refresh_map+"   isFirst="+isFirst);
//                intent_location.putExtra("Longitude",location.getLongitude());
//                intent_location.putExtra("latitude",location.getLatitude());
//                sendBroadcast(intent_location);//2016.9.15成功后台运行
                if(isSend){
                    if(isFirst == true){
                        isSend = false;//设置第一次进来定位后就停止
                        isFirst = false;//第一次进来后就设置成false
                    }
                    if(number == 0) {
                        Intent intent_location = new Intent(FragmentPageRun.REFREH_MESSAGE);
                        intent_location.putExtra("Longitude",location.getLongitude());
                        intent_location.putExtra("latitude",location.getLatitude());
                        context.sendBroadcast(intent_location);
                        lastLatitude = location.getLatitude();
                        lastLongitude = location.getLongitude();
                    }else{
                        distanceSum = TimeUtil.getMyTimeUtils().getDistance(lastLongitude,lastLatitude,
                                getTest(number).longitude,getTest(number).latitude,distanceSum);
                        LogUtils.i(TAG,"distance="+distanceSum+"number="+number);
                        if(number <= 12){
//                            LogUtils.i(TAG,"发送？");
                            Intent intent_location = new Intent(FragmentPageRun.REFREH_MESSAGE);
//                            LogUtils.i(TAG,"发送1？");
                            intent_location.putExtra("Longitude",getTest(number).longitude);
//                            LogUtils.i(TAG,"发送2？");
                            intent_location.putExtra("latitude",getTest(number).latitude);
//                            LogUtils.i(TAG,"发送3？");
                            context.sendBroadcast(intent_location);
                        }
                        sendDistance(distanceSum);//Magic，2016.10.31
                        lastLongitude = getTest(number).longitude;
                        lastLatitude = getTest(number).latitude;
                    }
                    number++;
                    LogUtils.i(TAG, "location="+location.getLongitude()+"number="+number);
                }
            }
        };
    }

    private class RefreshThread extends Thread {

        @Override
        public void run() {
            // TODO Auto-generated method stub
//            Looper.prepare();
            LogUtils.i(TAG,"client="+client+";serviceId="+serviceId+"  entityListener="+HealthyService.entityListener);
            LogUtils.i(TAG,"RefreshThread="+refresh);
            synchronized (this.getClass()) { //同步锁防止client被修改，性能方面可能出现问题，以后再做修改
                while (refresh) {//一直循环
                    queryRealtimeLoc();//这个查询实时轨迹没啥用2016.9.4(删除测试后并没有什么用)
                    try {
                        Thread.sleep(gatherInterval * 1000);//5秒休息时间
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        System.out.println("线程休眠失败");
                    }
                }
            }
        }
    }

    /**
     * 查询实时轨迹
     *  用于定位设备当前位置，定位信息不会存储在轨迹服务端，即不会形成轨迹信息，相当于通过定位SDK发起定位请求。
     *  相当于调用了entityListener接口
     */
    public void queryRealtimeLoc() {
//        LogUtils.i(TAG,"client="+client+";serviceId="+serviceId+"  entityListener="+AniService.entityListener);
//        LogUtils.i(TAG,"refresh="+refresh);
        client.queryRealtimeLoc(serviceId, entityListener);
    }

    /**
     * 每两点发送一次距离，Magic,2016.10.31
     */
    public void sendDistance(double sum){
        Intent intent_distamce = new Intent(BasePopupWindow.SETDATA);
//        LogUtils.i(TAG,"变化后的distance1");
//        LogUtils.i(TAG,"sum="+DateUtils.getMyDataUtils().dateToString(sum));
        intent_distamce.putExtra("distance",TimeUtil.getMyTimeUtils().dateToString(sum));
//        LogUtils.i(TAG,"变化后的distance="+DateUtils.getMyDataUtils().dateToString(sum));
        context.sendBroadcast(intent_distamce);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG,"onDestroy");
        refresh = false;
        isRunTime = false;
        unregisterReceiver(myreceiver);
        myThread.interrupt();
        timeThread.interrupt();
        trace = new Trace(this,serviceId,entityName,0);
        client.onDestroy();
        trace = null;
        client = null;
    }

    private LatLng getTest(int i){
        if(i == 1)return new LatLng(23.041685,113.404633);
        if(i == 2)return new LatLng(23.041885,113.404866);
        if(i == 3)return new LatLng(23.041905,113.405036);
        if(i == 4)return new LatLng(23.042105,113.405256);
        if(i == 5)return new LatLng(23.042335,113.405489);
        if(i == 6)return new LatLng(23.042525,113.405688);
        if(i == 7)return new LatLng(23.042715,113.405874);
        if(i == 8)return new LatLng(23.042905,113.406090);
        if(i == 9)return new LatLng(23.043125,113.406288);
        if(i == 10)return new LatLng(23.043385,113.406476);
        if(i == 11)return new LatLng(23.043585,113.406677);
        if(i == 12)return new LatLng(23.043783,113.406889);
        return null;
    }

    /**
     * 注册广播
     */
    private void initServiceBoardcastReceiver(){
        myreceiver = new ServiceBroadcast();
        intentFilter = new IntentFilter();
        intentFilter.addAction(HealthyService.IsOpenService);
        registerReceiver(myreceiver,intentFilter);
    }
    /**
     * 注册广播，接受是否开启计步服务
     * 整数 0代表关，1代表开,defaultFlag默认值为0
     */
    public class ServiceBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            message = intent.getIntExtra("TrueOrFalse", defaultFlag);
            isSetRefresh(message);
        }
    }

    /**
     *
     * @param flag 开启服务的标志，0关闭服务，1开启服务
     */
    private void isSetRefresh(int flag){
        if(flag == 0){ //关闭服务
            if(isSend == true){
                isSend = false;
                isSendTime = false;//10.31
                timeThread.interrupt();
            }
        }
        if(flag == 1 ){
            if(isSend == false){
                isSend = true;
                isSendTime = true;
//                LogUtils.i(TAG,"执行到了");
            }
        }
//        LogUtils.i(TAG,"flag="+flag+",isSend="+isSend+",myThread.isAlive()="+myThread.isAlive());
    }

    /**
     * 计跑步时间
     */
    //Magic,2016.10.31
    class TimeThread extends Thread{
        @Override
        public void run() {
            super.run();
            synchronized (this.getClass()){
                while(isRunTime){
                    try {
                        TimeThread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(isSendTime){
                        recordTime += 60000;//按秒计时
                        sendTime = TimeUtil.getMyTimeUtils().getHMS(recordTime);
                        LogUtils.i(TAG,"sendTime="+sendTime);
                        Intent intent_time = new Intent(BasePopupWindow.SETDATA);
                        intent_time.putExtra("time",sendTime);
                        context.sendBroadcast(intent_time);
                    }
                }
            }
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
