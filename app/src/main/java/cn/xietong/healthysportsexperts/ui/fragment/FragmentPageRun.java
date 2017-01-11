package cn.xietong.healthysportsexperts.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;

import java.util.ArrayList;
import java.util.List;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.service.HealthyService;
import cn.xietong.healthysportsexperts.ui.view.BasePopupWindow;
import cn.xietong.healthysportsexperts.ui.view.SlideFromBottomPopup;
import cn.xietong.healthysportsexperts.utils.LogUtils;
import cn.xietong.healthysportsexperts.utils.MyOrientationListener;

import static android.content.Context.SENSOR_SERVICE;


/**跑步绘图界面
 * Created by 林思旭1 on 2015/10/14.
 */
public class FragmentPageRun extends BaseFragment {
    private String TAG = "FragmentPageRun";
    private final int UpLoadFragment = 0,QueryFragment = 1;
    private boolean isFirst = true;//第一次进入应该先定位自己位置，所以这个让玩家先地位再点开始
    private int number = 0;
    public static String REFREH_MESSAGE = "open_or_close";
    private boolean refresh_map = false;
    private IsRefreshBoardcastReceiver myreceiver; //广播,接受轨迹开始和结束的信息
    private IntentFilter intentFilter;
    public static OnStopTraceListener stopBackListener = null;
    public static OnStartTraceListener startTraceListener = null;//开启轨迹服务监听器
//    private ProgressDialog myProgressDialog;
    private MyOrientationListener myOrientationListener;//林思旭 2016.7.27
    private float mXDirection = 0;
    private double longitude_service,latitude_service;
    private final double fault_message = -1;

    //fragment
    private SlideFromBottomPopup mypopup;
    private Context context;
    private boolean service_message = false;
    private Overlay myLocation,myCircle,myPath;
    /**
     * 采集周期（单位 : 秒）
     */
    private int gatherInterval = 5;

    /**
     * 打包周期（单位 : 秒）
     */
    private int packInterval = 30;
    // 覆盖物
    protected  OverlayOptions overlay;
    //半径覆盖物
    protected  OverlayOptions fenceOverlay = null;
    //半径长
    protected  int radius = 50;
    // 路线覆盖物
    private  PolylineOptions polyline = null;

    private List<LatLng> pointList ;//改

    protected boolean isTraceStart = false;

    private MapStatusUpdate msUpdate = null;

    private View view = null;

    public boolean isInUploadFragment = true;

    private PowerManager.WakeLock wakeLock = null;
    //fragment
    /**
     * Track监听器
     */
    public static OnTrackListener trackListener = null;

    protected static MapView bmapView = null;
    protected static BaiduMap mBaiduMap = null;

//    private TrackUploadFragment mTrackUploadFragment;//轨迹追踪器


    public static  Context mContext = null;

    private BitmapDescriptor realtimeBitmap;//改;

    private MyLocationData.Builder builder;
    @Override
    public int getLayoutId() {
        return R.layout.activty_slide_map;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        pointList = new ArrayList<LatLng>();
        SDKInitializer.initialize(mContext);

        //注册传感器,林思旭，2017.7.27
        initOritationListener();

        //动态注册广播
        initBoardcastReceiver();

        //fragment

        mypopup = new SlideFromBottomPopup(getActivity());//林思旭
        // 初始化监听器
        initListener();
    }

    @Override
    protected void initViews(View mContentView) {

        startService();//开启服务
        //初始化Progress
//        initProgressDialog();改

        // 初始化OnTrackListener
//        initOnTrackListener();

        //初始化开始进程接口
//        initOnStartTraceListener();

        //初始化结束进程借口
//        initOnStopTraceListener();

//        client.setOnTrackListener(trackListener);

        // 添加entity
//        addEntity();

    }

    @Override
    public void onStart() {
        super.onStart();
        // 初始化组件
        initComponent();

        // 初始化
        init();
    }

    @Override
    public void setupViews(Bundle savedInstanceState) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.i(TAG,"onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG,"onDestroy");
        initProgressDialog("正在退出后台程序，请稍等……");
//        myProgressDialog.show();
        HealthyService.client.stopTrace(HealthyService.trace, stopBackListener);
        stopService();
        mBaiduMap.clear();
        if(startTraceListener != null){
            startTraceListener = null;
        }
        if(stopBackListener != null){
            stopBackListener = null;
        }
        if(myLocation != null){
            myLocation.remove();
            overlay = null;
            myLocation = null;
        }
        if(myCircle != null){
            myCircle.remove();
            fenceOverlay = null;
            myCircle = null;
        }
        if(polyline != null){
            polyline = null;
            myPath.remove();
            myPath = null;
        }
    }
    /**
     * 初始化组件
     */
    private void initComponent() {
        // 初始化控件
        myOrientationListener.start();
        bmapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = bmapView.getMap();//获取地图控制器
        mBaiduMap.setMaxAndMinZoomLevel(19.0f,9.0f);
        bmapView.showZoomControls(false);
        SensorManager sm = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        Sensor mSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);//没有方向传感器的手机打印mSensor为空(我的手机为空，舍友手机不为空)
        if(mSensor == null){//2016.10.11：成功的为有没有传感器的手机配不同的图标
            realtimeBitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.location_myplace_no_direction);
        }else{
            realtimeBitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.location_myplace);
        }
//        LogUtils.i(TAG,"mSensor="+mSensor);
//        Bitmap bmp=BitmapFactory.decodeResource(getResources(), R.drawable.location_myplace);
//        mBaiduMap.setCompassIcon(bmp);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(5000).build()));//添加比例尺成功
        MyLocationConfiguration.LocationMode current_mode = MyLocationConfiguration.LocationMode.FOLLOWING;
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(current_mode,true,realtimeBitmap));
        builder = new MyLocationData.Builder();
    }

    //林思旭，测试(成功)
    private void initOritationListener()
    {
        myOrientationListener = new MyOrientationListener(mContext);
//        LogUtils.i(TAG,"myOrientationListener="+myOrientationListener);
        myOrientationListener.setmOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mXDirection = x;
                builder.direction(mXDirection);
                mBaiduMap.setMyLocationData(builder.build());//2016.8.27在舍友手机上成功测试
//                LogUtils.i(TAG,"mXDirection12="+mXDirection);
            }
        });

    }
    /**
     * 初始化OnStartTraceListener
     */
    private void initOnStartTraceListener() {
        // 初始化startTraceListener
        startTraceListener = new OnStartTraceListener() {

            // 开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
            public void onTraceCallback(int arg0, String arg1) {
                // TODO Auto-generated method stub
                showMessage("开启轨迹服务回调接口消息 [消息编码 : " + arg0 + "，消息内容 : " + arg1 + "]", Integer.valueOf(arg0));
                if (0 == arg0 || 10006 == arg0 || 10008 == arg0) {
                    isTraceStart = true;
//                    OpenService();
//                    Intent intent = new Intent(getActivity(),AniService.class);
//                    getActivity().startService(intent);
                }
            }

            // 轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
            public void onTracePushCallback(byte arg0, String arg1) {
                // TODO Auto-generated method stub
                showMessage("轨迹服务推送接口消息 [消息类型 : " + arg0 + "，消息内容 : " + arg1 + "]", null);
            }

        };
    }
    /**
     * 初始化OnStopTraceListener
     */
    private void initOnStopTraceListener() {
        // 初始化stopTraceListener
        stopBackListener = new OnStopTraceListener() {

            // 轨迹服务停止成功
            public void onStopTraceSuccess() {
                // TODO Auto-generated method stub
//                myProgressDialog.dismiss();
                myOrientationListener.stop();
                Intent intent_destroy = new Intent(BasePopupWindow.SETDATA);
                intent_destroy.putExtra("onDestroy","onDestroy");
                mContext.sendBroadcast(intent_destroy);
//                finish();
            }

            // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
            public void onStopTraceFailed(int arg0, String arg1) {
                // TODO Auto-generated method stub
//                Toast("停止轨迹服务接口消息 [错误编码 : " + arg0 + "，消息内容 : " + arg1 + "]", null);
                Toast.makeText(getActivity(),"成功关闭",Toast.LENGTH_SHORT).show();
                Intent intent_destroy = new Intent(BasePopupWindow.SETDATA);
                intent_destroy.putExtra("onDestroy","onDestroy");
                mContext.sendBroadcast(intent_destroy);
//                myProgressDialog.dismiss();
                myOrientationListener.stop();
//                finish();
            }
        };
    }

    private void initBoardcastReceiver(){
        myreceiver = new IsRefreshBoardcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(FragmentPageRun.REFREH_MESSAGE);
        intentFilter.setPriority(2);
        getActivity().registerReceiver(myreceiver,intentFilter);
    }

    public class IsRefreshBoardcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            longitude_service = intent.getDoubleExtra("Longitude", fault_message);//2016.9.4
            latitude_service = intent.getDoubleExtra("latitude",fault_message);//2016.9.4
            double Request = intent.getDoubleExtra("RequsetFail",fault_message);
            LogUtils.i(TAG,"="+longitude_service+",se="+latitude_service);
            if(longitude_service != fault_message && latitude_service != fault_message){
                LogUtils.i(TAG,"执行绘制");
                builder.latitude(latitude_service).longitude(longitude_service).direction(mXDirection);
                mBaiduMap.setMyLocationData(builder.build());
                LatLng myLatLng = new LatLng(latitude_service , longitude_service);
//                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(myLatLng, 9);
//                mBaiduMap.setMapStatus(msu);
                showRealtimeTrack(myLatLng);
            }
            if(Request != fault_message){
                Toast.makeText(getActivity(),"请打开网络和GPS",Toast.LENGTH_SHORT).show();
            }
            LogUtils.i(TAG,"IsRefreshBoardcastReceiver");
        }
    }

    /**
     *
     * @param content progressBar的内容
     */
    private void initProgressDialog(String content){
//        myProgressDialog = new ProgressDialog(mContext);
//        myProgressDialog.setMessage(content);
    }

    //fragment
    /**
     * 初始化
     */
    private void init() {

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mypopup.showPopupWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                LogUtils.i(getClass().getName(),"地图点击2");
                mypopup.showPopupWindow();
                return false;
            }
        });
    }

    /**
     * 查询entityList(如果删除它对功能没影响，再删除)
     */
    @SuppressWarnings("unused")
    private void queryEntityList() {
        // entity标识列表（多个entityName，以英文逗号"," 分割）
        String entityNames = HealthyService.entityName;
        // 属性名称（格式为 : "key1=value1,key2=value2,....."）
        String columnKey = "";
        // 返回结果的类型（0 : 返回全部结果，1 : 只返回entityName的列表）
        int returnType = 0;
        // 活跃时间（指定该字段时，返回从该时间点之后仍有位置变动的entity的实时点集合）
        int activeTime = (int) (System.currentTimeMillis() / 1000 - 30);
        // 分页大小
        int pageSize = 10;
        // 分页索引
        int pageIndex = 1;

        HealthyService.client.queryEntityList(HealthyService.serviceId, entityNames, columnKey, returnType, activeTime,
                pageSize,
                pageIndex, HealthyService.entityListener);
    }

    /**
     * 显示实时轨迹
     */
    private void showRealtimeTrack(LatLng location) {

//        if (null == refreshThread || !refreshThread.refresh) {
//            return;
//        }
        double latitude = location.latitude;
        double longitude = location.longitude;
        Log.i(getClass().getName(),"latitude="+latitude+"   longitude="+longitude);
        if (Math.abs(latitude - 0.0) < 0.000001 && Math.abs(longitude - 0.0) < 0.000001) {
            showMessage("当前查询无轨迹点", null);

        } else {

//            if(pointList!=null){
//                pointList.clear();
//            }
            LatLng latLng = new LatLng(latitude, longitude);
            pointList.add(latLng);//将所有的点添加进数组里面


            if (isInUploadFragment) {
                // 绘制实时点
                drawRealtimePoint(latLng);
            }

        }

    }

    /**
     * 绘制实时点
     */
    private void drawRealtimePoint(LatLng point) {

//        MainActivity.mBaiduMap.clear();//清除
        LogUtils.i(TAG,"drawRealtimePoint");
        MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(18).build();

        msUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

//        realtimeBitmap = BitmapDescriptorFactory
//                .fromResource(R.drawable.location_myplace);


//        Overlay myOverlay = new Overlay();
//        overlay = new MarkerOptions().position(point)
//                .icon(realtimeBitmap).zIndex(9).draggable(true);

        fenceOverlay = new CircleOptions().center(point).fillColor(R.color.color_grey_focus)
                .stroke(new Stroke(6, getResources().getColor(R.color.ring_text_color)))
                .radius(radius);

        if (pointList.size() >= 2 && pointList.size() <= 10000) {
            // 添加路线（轨迹）
//            polyline = new PolylineOptions().width(10)
//                    .color(Color.RED).points(pointList);
            LogUtils.i(TAG,"pointList="+pointList);
            polyline = new PolylineOptions().width(10)
                    .color(getActivity().getResources().getColor(R.color.color_blue_polyline)).dottedLine(true).points(pointList);//虚线添加成功，dotteLine就是虚线添加
        }

        addMarker();

    }
    /**
     * 添加地图覆盖物
     */
    public void addMarker() {
//        MainActivity.mBaiduMap.clear();
        LogUtils.i(TAG,"addMarker");
        if (null != msUpdate) {
//            MainActivity.mBaiduMap.setMapStatus(msUpdate);2016.7.22
            mBaiduMap.setMapStatus(msUpdate);
            mBaiduMap.animateMapStatus(msUpdate);
//            MainActivity.mBaiduMap.animateMapStatus(msUpdate); 2016.7.22
        }
        if(myPath != null){
            myPath.remove();
        }
        // 路线覆盖物
        if (null != polyline) {
            Log.i(TAG,"路线覆盖物poly"+polyline);
            Log.i(TAG,"路线覆盖物"+pointList);
//            myPath = MainActivity.mBaiduMap.addOverlay(polyline);2016.7.22
            myPath = mBaiduMap.addOverlay(polyline);
        }


//        if(myLocation != null){
//            myLocation.remove();
//        }
//        // 实时点覆盖物
//        if (null != overlay) {
//            Log.i(TAG,"实时点覆盖物");
//            myLocation = MainActivity.mBaiduMap.addOverlay(overlay);
//        }


        if(myCircle != null){
            myCircle.remove();
        }
        if(fenceOverlay != null){
            Log.i(TAG,"半径覆盖物");
//            myCircle = MainActivity.mBaiduMap.addOverlay(fenceOverlay);
            myCircle = mBaiduMap.addOverlay(fenceOverlay);
        }
    }
    private void showMessage(final String message, final Integer errorNo) {

        new Handler(getActivity().getMainLooper()).post(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                if (null != errorNo) {
                    if (0 == errorNo.intValue() || 10006 == errorNo.intValue() || 10008 == errorNo.intValue()
                            || 10009 == errorNo.intValue()) {
                    } else if (1 == errorNo.intValue() || 10004 == errorNo.intValue()) {

                    }
                }
            }
        });
    }
    /**
     * 初始化监听器
     */
    private void initListener() {
        // 初始化开启轨迹服务监听器
        initOnStartTraceListener();

        // 初始化停止轨迹服务监听器
        initOnStopTraceListener();
    }

    private void startService(){
        Intent myIntent = new Intent(mContext,HealthyService.class);
        getActivity().startService(myIntent);
    }
    private void stopService(){
        Intent myIntent = new Intent(mContext,HealthyService.class);
        getActivity().stopService(myIntent);
    }
}
