package cn.xietong.healthysportsexperts.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**所有的Activity都要继承自该Activity
 * Created by Administrator on 2015/10/27.
 */
public abstract class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

    public void showToast(String info){
        Toast.makeText(BaseActivity.this,info,Toast.LENGTH_SHORT).show();
    }
}
