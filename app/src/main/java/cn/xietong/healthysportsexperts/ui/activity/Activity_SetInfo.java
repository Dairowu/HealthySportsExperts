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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.config.BmobConstants;
import cn.xietong.healthysportsexperts.model.MyUser;
import cn.xietong.healthysportsexperts.ui.view.ScaleRulerView;
import cn.xietong.healthysportsexperts.utils.ActivityCollector;
import cn.xietong.healthysportsexperts.utils.CommonUtils;

/**注册时用户设置个人信息的界面
 * Created by deng on 2015/11/25.
 */
public class Activity_SetInfo extends BaseActivity{

    private static final int MAX_LENGTH = 30;
    private EditText et_nickname,et_signature;
    private TextView tv_signatureNumber;
    private RadioGroup rg;
    private Button btn_complete;
    private ImageView iv_headphoto;
    private ScaleRulerView mHeightRuler,mWeightRuler;
    private TextView mTextHeight,mTextWeight;

    private float height = 170,weight = 65;

    @Override
    public int getLayoutId() {
        return R.layout.activity_setinfo;
    }

    @Override
    public void initViews() {
        initTopbarForLeft("完成注册", new OnTopbarButtonClickListener());
        topBar.setButtonVisable(1,false);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        et_signature = (EditText) findViewById(R.id.et_setSignature);
        tv_signatureNumber = (TextView) findViewById(R.id.tv_setSignatureNumber);
        rg = (RadioGroup) findViewById(R.id.rg_gender);
        btn_complete = (Button) findViewById(R.id.btn_complete);
        iv_headphoto = (ImageView) findViewById(R.id.iv_setPicture);
        mHeightRuler = (ScaleRulerView) findViewById(R.id.id_height_scale);
        mTextHeight = (TextView) findViewById(R.id.tv_user_height_value);
        mWeightRuler = (ScaleRulerView) findViewById(R.id.id_weight_scale);
        mTextWeight = (TextView) findViewById(R.id.tv_user_weight_value);
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
            }
        });

        mHeightRuler.setOnValueChangedListener(new ScaleRulerView.OnValueChangedListener() {
            @Override
            public void onValueChanged(float value) {
                height = value;
                mTextHeight.setText(String.valueOf(value));
            }
        });

        mWeightRuler.setOnValueChangedListener(new ScaleRulerView.OnValueChangedListener() {
            @Override
            public void onValueChanged(float value) {
                weight = value;
                mTextWeight.setText(String.valueOf(value));
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
        Uri uri = null;
        if(data != null){
            uri = data.getData();
        }
        if(requestCode == BmobConstants.REQUESTCODE_TAKE_CAMERA
        &&resultCode == Activity.RESULT_OK){
            Intent intent = new Intent(Activity_SetInfo.this,ChoosePhotoActivity.class);
            intent.setData(uri);
            startActivityForResult(intent,BmobConstants.REQUESTCODE_CHOOSE_PHOTO);
        }else if(requestCode == BmobConstants.REQUESTCODE_CHOOSE_PHOTO && resultCode == requestCode){
            if(uri != null){
                iv_headphoto.setImageURI(uri);
                final BmobFile bmobFile = new BmobFile(new File(uri.getPath()));
                bmobFile.upload(Activity_SetInfo.this, new UploadFileListener() {
                    @Override
                    public void onSuccess() {
                        showToast("头像设置成功");
                        String url = bmobFile.getFileUrl(Activity_SetInfo.this);
                        mApplication.getSharedPreferencesUtil().setAvatarUrl(url);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        showToast("头像上传失败"+s);
                        mApplication.getSharedPreferencesUtil().setAvatarUrl(null);
                    }
                });
            }else {
                showToast("头像设置失败");
            }
        }
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

        mUser.setUsername(phonenumber);
        mUser.setPassword(password);
        mUser.setNick(nick);
        mUser.setSignature(signature);
        mUser.setSex(isMan);
        mUser.setHeight(height);
        mUser.setWeight(weight);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final File croppedFile = new File(getCacheDir(), "cropped.jpg");
    }
}
