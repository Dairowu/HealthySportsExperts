package cn.xietong.healthysportsexperts.ui.activity;

import android.os.Handler;
import android.os.Message;

import cn.bmob.im.BmobChat;
import cn.xietong.healthysportsexperts.R;

/**
 * Created by Administrator on 2015/11/16
 */
public class Activity_Splash extends BaseActivity {

    private static final int GO_HOME = 100;
    private static final int GO_LOGIN = 200;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GO_HOME:
                    startAnimActivity(MainActivity.class);
                    finish();
                    break;
                case GO_LOGIN:
                    startAnimActivity(Activity_Login.class);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initViews() {

        //可设置调试模式，当为true的时候，
        // 会在logcat的BmobChat下输出一些日志，包括推送服务是否正常运行，
        // 如果服务端返回错误，也会一并打印出来。方便开发者调试
        BmobChat.DEBUG_MODE = true;
        //此方法包含了bmobSDk的初始化
        BmobChat.getInstance(this).init("f1a3d5dc79f46ed06d79284d3131fb8d");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mUserManager.getCurrentUser()!=null){
            updateUserInfos();
            mHandler.sendEmptyMessageDelayed(GO_HOME,2000);
        }else {
            mHandler.sendEmptyMessageDelayed(GO_LOGIN,2000);
        }
    }

    @Override
    public void setupViews() {

    }
}
