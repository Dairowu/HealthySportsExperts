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

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.utils.Service_Calculate_Step;

/**计步界面
 * Created by Administrator on 2015/10/18.
 */
public class FragmentPagePosition_son1 extends BaseFragment {

    public static final int UPDATE_TEXT = 1;
    private View view;
    private TextView tv_stepNumber;
    private TextView tv_date;
    private Intent intent;
    boolean right;
    Service_Calculate_Step.MyBinder binder;

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
        Intent bindIntent = new Intent(context,Service_Calculate_Step.class);
        right = context.getApplicationContext().bindService(bindIntent, conn, Context.BIND_AUTO_CREATE);
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

    }

    /**
     * 开启线程动态获取步数
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL,new Locale("zh","CN")) ;
//        tv_date.setText(df.format(new Date()));
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
}
