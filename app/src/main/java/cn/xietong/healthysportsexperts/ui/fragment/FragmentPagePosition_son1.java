package cn.xietong.healthysportsexperts.ui.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.model.DatabaseHelper;
import cn.xietong.healthysportsexperts.model.UserInfo;
import cn.xietong.healthysportsexperts.utils.SQLiteUtils;
import cn.xietong.healthysportsexperts.utils.Service_Calculate_Step;
import cn.xietong.healthysportsexperts.utils.StepListener;
import cn.xietong.healthysportsexperts.utils.UserUtils;

/**计步界面
 * Created by deng on 2015/10/18.
 */
public class FragmentPagePosition_son1 extends BaseFragment {

    public static final int UPDATE_TEXT = 1;
    //一整天的毫秒数
    public static final long DAYTIME_MILL = 86400000;
    private View view;
    //步数显示
    private TextView tv_stepNumber;
    //日期显示
    private TextView tv_date;
    private Intent intent;
    //同一天内退出应用之前的数据
    private int oldCount;
    Service_Calculate_Step.MyBinder binder;
    //数据库操作对象
    DatabaseHelper dbHelper = mApplication.getDBHelper();
    //用户从使用程序到现在为止的每一天的操作数据
    List<UserInfo> userLists = null;
    //用户操作后的数据
    UserInfo user = new UserInfo();
    //计步监听器
    StepListener mStepListener = mApplication.getStepListener();
    //定义日期格式
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日EEEE", Locale.CHINA);

    /**
     * 通过此步操作获得binder对象，再通过其获得Service中记录的步数
     */
    public ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (Service_Calculate_Step.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 动态显示步数
     */
    private Handler handler = new Handler() {

        public void handleMessage(Message msg){
            if(msg.what == UPDATE_TEXT){
                tv_stepNumber.setText(Integer.toString(msg.arg1));
            }
        }

    };

    /**
     * 绑定Service
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        UserUtils.loadFromDatabase();
        userLists = UserUtils.getList();

        if(userLists!=null&&userLists.size()>0) {
            //存储的最近一天的时间
            Long recentTime = Long.parseLong(userLists.get(0).getDatatime());
            //当前时间
            Long nowTime = new Date().getTime();
            String old = simpleDateFormat.format(new Date(recentTime));
            String now = simpleDateFormat.format(new Date());
            //通过比较最近一次的时间和今天时间是否相同，判断是否将数据库中记录的步数设置为今天已经计的步数，
            // 如果不是则将今天与记录之间的天数里的记录添加上并且值为0
            if ((old.equals(now))) {
                oldCount = userLists.get(0).getCount();
                mStepListener.setCount(oldCount);
            } else {
                //记录的最近一天与当前时间间的天数
                int among_days_number = (int) ((nowTime - recentTime)/DAYTIME_MILL - 1);
                if(among_days_number > 0){
                    for(int i = 0;i < among_days_number;i++){
                        user.setCount(0);
                        user.setDatatime((recentTime+DAYTIME_MILL*i)+"");
                        SQLiteUtils.insert(dbHelper,user,"step");
                    }
                }
            }
        }

        Intent bindIntent = new Intent(context,Service_Calculate_Step.class);
        context.getApplicationContext().bindService(bindIntent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment1_son1;
    }

    @Override
    protected void initViews(View mContentView) {
        tv_stepNumber = (TextView) mContentView.findViewById(R.id.tv_stepNumber);
        tv_date = (TextView) mContentView.findViewById(R.id.tv_date);
    }

    @Override
    public void setupViews(Bundle savedInstanceState) {

        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnekeyShare onekeyShare = new OnekeyShare();
//                Platform platform = ShareSDK.getPlatform(getActivity(), SinaWeibo.NAME);
//                platform.SSOSetting(true);
//                onekeyShare.disableSSOWhenAuthorize();
                onekeyShare.setTitle("一键分享");
                onekeyShare.setTitleUrl("http://mob.com");
                onekeyShare.setText("我今天走了"+binder.getCount()+"步!健康运动每一天。");
                onekeyShare.setComment("测试文本");
                onekeyShare.setSite("Jiankang");
                onekeyShare.setSiteUrl("http://sharesdk.cn");
                onekeyShare.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
                onekeyShare.show(getActivity());
            }
        });

    }

    /**
     * 开启线程动态获取步数
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tv_date.setText(simpleDateFormat.format(new Date()));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        Message message = new Message();
                        message.what = UPDATE_TEXT;
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(binder!=null) {
                            message.arg1 = binder.getCount();
                        }
                        handler.sendMessage(message);
                    }
                }
            }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        user.setCount(binder.getCount());
        user.setDatatime(new Date().getTime()+"");
        SQLiteUtils.insert(dbHelper,user,"step");
    }

}
