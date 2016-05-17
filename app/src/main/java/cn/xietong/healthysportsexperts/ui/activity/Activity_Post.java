package cn.xietong.healthysportsexperts.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.listener.SaveListener;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.model.Post;
import cn.xietong.healthysportsexperts.ui.view.TopBar;

public class Activity_Post extends BaseActivity implements View.OnClickListener {
    private EditText editText;
    private static String SEND_COMPLETE = "SUCCESS";
    private String TAG = "Activity_Post";
    private String name;
    private BmobUserManager mUserManager;
    /**
     * 设置监听器
     */
    @Override
    public void setupViews() {
    }

    /**
     * 初始化界面
     */
    @Override
    public void initViews() {
        mUserManager = BmobUserManager.getInstance(this);
        initTopbarForBoth("正文","发送",new OnTopbarButtonClickListener());
        findViews();
        // 获取编辑框焦点
        editText.setFocusable(true);
        //打开软键盘
        InputMethodManager imm = (InputMethodManager) Activity_Post.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        name = mUserManager.getCurrentUserName();//获取用户名
    }

    /**
     * 获取控件ID
     */
    public void findViews() {
        editText = (EditText) findViewById(R.id.et);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_post;
    }

    /**
     * 实现监听器的方法
     * @param v
     */
    @Override
    public void onClick(View v) {

    }
    class OnTopbarButtonClickListener implements TopBar.topbarClickListener {

        @Override
        public void leftClick() {
            finish();
        }

        @Override
        public void rightClick() {
            Post post = new Post(name, editText.getText().toString());
            post.save(Activity_Post.this, new SaveListener() {
                @Override
                public void onSuccess() {//发送成功，返回刷新
                    showToast("发送成功");
                    Intent intent = new Intent(Activity_Post.this, MainActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(int i, String s) {//发送失败
                    Log.e(TAG, "onFailure: " + s);
                    showToast("发送失败");
                }
            });
        }
    }
}
