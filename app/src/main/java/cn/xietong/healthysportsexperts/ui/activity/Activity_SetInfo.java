package cn.xietong.healthysportsexperts.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;

import cn.bmob.v3.listener.SaveListener;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.config.BmobConstants;
import cn.xietong.healthysportsexperts.model.MyUser;
import cn.xietong.healthysportsexperts.utils.ActivityCollector;
import cn.xietong.healthysportsexperts.utils.CommonUtils;

/**
 * Created by deng on 2015/11/25.。。
 */
public class Activity_SetInfo extends BaseActivity{

    private static final int MAX_LENGTH = 70;
    EditText et_nickname,et_signature;
    TextView tv_signatureNumber;
    RadioGroup rg;
    Button btn_complete;
    ImageView iv_headphoto;

    @Override
    public int getLayoutId() {
        return R.layout.activity_setinfo;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.i("info","setInfoRestart");
        final File croppedFile = new File(getCacheDir(), "cropped.jpg");
        if(croppedFile.exists()) {
            iv_headphoto.setImageURI(Uri.fromFile(croppedFile));
        }
//           DisplayImageOptions options = new DisplayImageOptions.Builder()
//                    .cacheInMemory(true)
//                    .cacheOnDisk(true)
//                    .bitmapConfig(Bitmap.Config.RGB_565)
//                    .build();
//
//            ImageLoader.getInstance().displayImage(String.valueOf(croppedFile),iv_headphoto,options);

    }

    @Override
    public void initViews() {
        Log.i("info","onCreate");
        initTopbarForLeft("完成注册", new OnTopbarButtonClickListener());
        topBar.setButtonVisable(1,false);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        et_signature = (EditText) findViewById(R.id.et_setSignature);
        tv_signatureNumber = (TextView) findViewById(R.id.tv_setSignatureNumber);
        rg = (RadioGroup) findViewById(R.id.rg_gender);
        btn_complete = (Button) findViewById(R.id.btn_complete);
        iv_headphoto = (ImageView) findViewById(R.id.iv_setPicture);

    }

    @Override
    public void setupViews() {

        et_signature.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = et_signature.getText().toString();
                tv_signatureNumber.setText(MAX_LENGTH - content.length()+"");
//                if (content.length() > 70){
//                tv_signatureNumber.setTextColor(Color.rgb(243, 108, 96));
//                }else{
//                    tv_signatureNumber.setTextColor(getResources().getColor(R.color.base_color_text_black));
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /**
         * 选择头像
         */
        iv_headphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent,BmobConstants.REQUESTCODE_TAKE_CAMERA);//2016.4.8注释掉
//                sysimageUtils = new SysimageUtils(Activity_SetInfo.this);
//                sysimageUtils.startActionPick();
            }
        });

        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                register();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BmobConstants.REQUESTCODE_TAKE_CAMERA
        &&resultCode == Activity.RESULT_OK){
            Intent intent = new Intent(Activity_SetInfo.this,ChoosePhotoActivity.class);
            intent.setData(data.getData());
            startActivity(intent);
        }
//        sysimageUtils.actionResult(requestCode,resultCode,data,iv_headphoto);
    }

    private void register() {

        String nick = et_nickname.getText().toString();
        String signature = et_signature.getText().toString();

        boolean isMan = true;

        if( rg.getCheckedRadioButtonId()!=R.id.rb_man){
            isMan = false;
        }

        if(TextUtils.isEmpty(nick)){
            showToast("请输入您的昵称!");
            return;
        }

        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if(!isNetConnected){
            showToast("当前网络不可用，请检查您的网络设置");
            return;
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        String phonenumber = bundle.getString("phonenumber");
        String password = bundle.getString("password");

        final ProgressDialog progressDialog = new ProgressDialog(Activity_SetInfo.this);
        progressDialog.setMessage("正在注册");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final MyUser mUser = new MyUser();

        Log.i("info",phonenumber+"  "+password);

        mUser.setUsername(phonenumber);
        mUser.setPassword(password);
        mUser.setNick(nick);
        mUser.setSignature(signature);
        mUser.setSex(isMan);
        mUser.setAvatar(mApplication.getSharedPreferencesUtil().getAvatarUrl());

        mUser.signUp(Activity_SetInfo.this, new SaveListener() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                showToast("注册成功");
                Intent intent = new Intent(Activity_SetInfo.this,MainActivity.class);
                startActivity(intent);
                ActivityCollector.finishAll();
            }

            @Override
            public void onFailure(int i, String s) {

                progressDialog.dismiss();
                showToast("注册失败"+s);

            }
        });
    }
}
