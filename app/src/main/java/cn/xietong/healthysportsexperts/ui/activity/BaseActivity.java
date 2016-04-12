package cn.xietong.healthysportsexperts.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.app.App;
import cn.xietong.healthysportsexperts.ui.view.TopBar;
import cn.xietong.healthysportsexperts.utils.ActivityCollector;
import cn.xietong.healthysportsexperts.utils.CollectionUtils;

/**所有的Activity都要继承自该Activity
 * Created by Administrator on 2015/10/27.
 */
public abstract class BaseActivity extends FragmentActivity {

    protected TopBar topBar;
    public App mApplication;
    BmobUserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(getLayoutId());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mApplication = App.getInstance();
        mUserManager = BmobUserManager.getInstance(this);
        initViews();
        setupViews();
    }

    /**
     * @return 返回一个布局ID
     */
    public abstract int getLayoutId();

    /**
     * 初始化视图
     */
    public abstract void initViews();

    public abstract  void setupViews();

    /**
     * Activity切换
     * @param cla 要切换到的目标Activity.class
     */
    public void startAnimActivity(Class<?> cla) {
        this.startActivity(new Intent(this, cla));
    }

    public void startAnimActivity(Intent intent) {
        this.startActivity(intent);
    }

    /**
     * 只有标题
     * @param title 标题
     */
    public void initTopbarForOnlyTitle(String title){
        topBar = (TopBar) findViewById(R.id.topBar);
        topBar.setDefaultTitle(title);
    }

    /**
     * 有返回按钮
     * @param title 标题
     * @param mListener 监听器
     */
    public void initTopbarForLeft(String title,TopBar.topbarClickListener mListener){
        topBar = (TopBar) findViewById(R.id.topBar);
        topBar.setTitleAndLeftImageButton(title,mListener);
    }

    /**
     * 带有两个按钮
     * @param title 标题
     * @param text 右部按钮的显示文本
     * @param mListener 监听器
     */
    public void initTopbarForBoth(String title,String text,TopBar.topbarClickListener mListener){
        topBar = (TopBar) findViewById(R.id.topBar);
        topBar.setTitleAndBothButton(title,text,mListener);
    }

    /** 打印Log
     * ShowLog
     * @return void
     * @throws
     */
    public void ShowLog(String msg){
        Log.i("info", msg);
    }

    public void showToast(String info){
        Toast.makeText(BaseActivity.this,info,Toast.LENGTH_SHORT).show();
    }


    public void updateUserInfos(){

        mUserManager.queryCurrentContactList(new FindListener<BmobChatUser>() {
            @Override
            public void onSuccess(List<BmobChatUser> list) {
                mApplication.setContactList(CollectionUtils.list2map(list));
            }

            @Override
            public void onError(int i, String s) {
                if(i== BmobConfig.CODE_COMMON_NONE){
                    ShowLog(s);
                }else{
                    ShowLog("查询好友列表失败"+s);
                }
            }
        });

    }

    class OnTopbarButtonClickListener implements TopBar.topbarClickListener {

        @Override
        public void leftClick() {
            finish();
        }

        @Override
        public void rightClick() {

        }
    }

}
