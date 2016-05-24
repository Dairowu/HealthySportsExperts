package cn.xietong.healthysportsexperts.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.ui.fragment.FragmentPageMessage;
import cn.xietong.healthysportsexperts.ui.fragment.FragmentPageMy;
import cn.xietong.healthysportsexperts.ui.fragment.FragmentPagePosition;
import cn.xietong.healthysportsexperts.ui.fragment.FragmentPageRun;

/**
 * 主界面其中包含四个Fragment
 */
public class MainActivity extends FragmentActivity {

    //下端的标签栏
    private FragmentTabHost mTabHost;

    private LayoutInflater layoutInflater;

    //构建四个Fragment的数组
    private Class[] fragmentArray = {FragmentPagePosition.class,
            FragmentPageMessage.class,FragmentPageRun.class,FragmentPageMy.class};

    //每个子项对应的图片资源数组
    private int[] mImageViewArray = {R.drawable.iv_tab_position_item,R.drawable.iv_tab_message_item,R.drawable.iv_tab_run_item,R.drawable.iv_tab_my_item};

    //每个子项对应的文字数组
    private String[] mTextArray = {"悦动","嗨聊","跑步","我"};

    //之前按下返回键的时间
    private long mPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab_layout);

        //初始化视图
        initView();

    }

    private void initView() {
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //实例化TabHost
        mTabHost = (FragmentTabHost)  findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        int count = fragmentArray.length;

        for(int i = 0;i < count;i++){
            //为每一个Tab按钮设置图标和文字
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextArray[i]).setIndicator(getTabItemView(i));
           //将Tab按钮添加到Tab选项卡
            mTabHost.addTab(tabSpec,fragmentArray[i],null);
            //为Tab按钮设置背景
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }

    /**
     * 得到每一个TabHost所包含的子View
     * @param i
     * @return
     */
    private View getTabItemView(int i) {
        View view = layoutInflater.inflate(R.layout.tab_item_view,null);

        ImageView iv = (ImageView) view.findViewById(R.id.iv_bottom);
        iv.setImageResource(mImageViewArray[i]);

        TextView tv = (TextView) view.findViewById(R.id.tv_bottom);
        tv.setText(mTextArray[i]);
        return view;
    }

    /**
     * 通过该方法可以实现按下返回键跟按下home键效果相同
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(false);//true对任何Activity都适用
//            return true;
//        }
// 获取第一次按键时间
        long mNowTime = System.currentTimeMillis();
        // 比较两次按键时间差
        if((mNowTime - mPressedTime) > 2000){
            mPressedTime = mNowTime;
            return true;
            }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        long mNowTime = System.currentTimeMillis();
        // 比较两次按键时间差
        if((mNowTime - mPressedTime) > 2000){
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mPressedTime = mNowTime;
        }else {
            super.onBackPressed();
        }
    }
}
