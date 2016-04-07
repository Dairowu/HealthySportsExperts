package cn.xietong.healthysportsexperts.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.xietong.healthysportsexperts.R;

/**
 * Created by Administrator on 2015/11/17.
 */
public class Activity_Register extends BaseActivity{

    private EditText et_register_phonenumber,et_rgs_password,et_rgs_password_confirm;
    private Button btn_nextstep;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initViews() {

        initTopbarForLeft("注册账号",new OnTopbarButtonClickListener());
        et_register_phonenumber = (EditText) findViewById(R.id.et_register_phonenumber);
        et_rgs_password = (EditText) findViewById(R.id.et_register_password);
        et_rgs_password_confirm = (EditText) findViewById(R.id.et_register_password_confirm);

        btn_nextstep = (Button) findViewById(R.id.btn_nextstep);

    }

    @Override
    public void setupViews() {

        btn_nextstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep();
            }
        });

    }

    private void nextStep() {

        String phonenumber = et_register_phonenumber.getText().toString();
        String password = et_rgs_password.getText().toString();
        String psw_again = et_rgs_password_confirm.getText().toString();

        if(TextUtils.isEmpty(phonenumber)){
            showToast("手机号码不能为空");
            return;
        }

        if(TextUtils.isEmpty(password)){
            showToast("密码不能为空");
            return;
        }

        if(!psw_again.equals(password)){
            showToast("两次密码不一致，请重新输入");
            return;
      }

        Bundle data = new Bundle();
        data.putString("phonenumber",phonenumber);
        data.putString("password",password);

        Intent intent = new Intent(Activity_Register.this,Activity_SetInfo.class);
        intent.putExtra("data",data);
        startActivity(intent);
    }
}
