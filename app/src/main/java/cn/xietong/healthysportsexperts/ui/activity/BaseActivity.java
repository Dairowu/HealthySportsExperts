package cn.xietong.healthysportsexperts.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**所有的Activity都要继承自该Activity
 * Created by Administrator on 2015/10/27.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
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

    /** 打印Log
     * ShowLog
     * @return void
     * @throws
     */
    public void ShowLog(String msg){
        Log.i("life", msg);
    }

    public void showToast(String info){
        Toast.makeText(BaseActivity.this,info,Toast.LENGTH_SHORT).show();
    }
}
