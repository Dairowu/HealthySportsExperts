package cn.xietong.healthysportsexperts.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.app.App;
import cn.xietong.healthysportsexperts.model.MyUser;
import cn.xietong.healthysportsexperts.utils.CollectionUtils;
import cn.xietong.healthysportsexperts.utils.CommonUtils;

/**
 * Created by Administrator on 2015/11/16.
 */
public class Activity_Login extends BaseActivity implements View.OnClickListener{

    private BmobUserManager userManager;///Magic 2016.4.7
    private String TAG = "Activity_Login";
    EditText et_username,et_password;
    Button btn_login;
    TextView tv_register;


    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews() {
        Log.i(TAG,"initViews");
        et_username = (EditText) findViewById(R.id.et_phonenumber);
        et_password = (EditText) findViewById(R.id.et_password);

        btn_login  = (Button) findViewById(R.id.btn_login);
        tv_register = (TextView) findViewById(R.id.tv_register);

        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);

    }

    @Override
    public void setupViews() {

    }



    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.tv_register){
            Intent intent = new Intent(Activity_Login.this,Activity_Register.class);
            startActivity(intent);
        }else {
            boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
            if(!isNetConnected){
                showToast("当前网络不可用，请检查您的网络");
                return;
            }
            login();
            Log.i(TAG,"login");
        }
    }

    private void login() {

        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

        if(TextUtils.isEmpty(username)){
            showToast("用户名不能为空");
            return;
        }
        if(TextUtils.isEmpty(password)){
            showToast("密码不能为空");
            return;
        }

        final ProgressDialog progress = new ProgressDialog(
                Activity_Login.this);
        progress.setMessage("正在登陆...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        MyUser user = new MyUser();
        user.setUsername(username);
        user.setPassword(password);

        mUserManager.login(user, new SaveListener() {
            @Override
            public void onSuccess() {

                updateUserInfos();
                progress.dismiss();
//                Intent intent = new Intent(Activity_Login.this,MainActivity.class);
//                startActivity(intent);//注释掉是因为我需要在进去前完成数据的加载.Magic,2016.4.8
                GetcurrentUserFriend();//Magic,2016.4.8
            }

            @Override
            public void onFailure(int i, String s) {
                progress.dismiss();
                BmobLog.i(s);
                showToast(s);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //2016.3.31(获取当前账号的好友)
    private void GetcurrentUserFriend(){
        BmobUserManager.getInstance(this).queryCurrentContactList(new FindListener<BmobChatUser>() {

            @Override
            public void onSuccess(List<BmobChatUser> arg0) {
                // TODO Auto-generated method stub
                Log.i(TAG,"arg0="+arg0);
                Log.i(TAG,"CollectionUtils.list2map(arg0)="+CollectionUtils.list2map(arg0));
                App.getInstance().setContactList(CollectionUtils.list2map(arg0));//将arg0数据保存在缓存区
                Log.i(TAG, "CustomApplcation:" + App.getInstance().getContactList() + "");
                Intent intent = new Intent(Activity_Login.this,MainActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                Toast.makeText(Activity_Login.this,"登陆失败",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
