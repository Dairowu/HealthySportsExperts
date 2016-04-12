package cn.xietong.healthysportsexperts.ui.activity;

import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.utils.SharePreferenceUtil;

/**
 * Created by Administrator on 2015/11/15.
 */
public class Activity_Remind extends BaseActivity implements CompoundButton.OnCheckedChangeListener{

    //初始化三个选择按钮
    private SwitchCompat switch_accept,switch_voice,switch_vibrate;

    private TextView tv_voice,tv_vibrate;
    private View v_devier;

    SharePreferenceUtil mSharedUtil;

    @Override
    public int getLayoutId() {
        return R.layout.notify_swich;
    }

    @Override
    public void initViews() {

        initTopbarForLeft("消息与提醒",new OnTopbarButtonClickListener());

        mSharedUtil = mApplication.getSharedPreferencesUtil();

        switch_accept = (SwitchCompat) findViewById(R.id.toggle_btn_accept);
        switch_voice  = (SwitchCompat) findViewById(R.id.toggle_btn_voice);
        switch_vibrate = (SwitchCompat) findViewById(R.id.toggle_btn_vibrate);

        tv_voice  = (TextView) findViewById(R.id.tv_voice );
        tv_vibrate = (TextView) findViewById(R.id.tv_vibrate);

        v_devier = findViewById(R.id.view_devider);

        switch_accept.setOnCheckedChangeListener(this);
        switch_voice.setOnCheckedChangeListener(this);
        switch_vibrate.setOnCheckedChangeListener(this);

    }

    @Override
    public void setupViews() {

        boolean isAllowNotify = mSharedUtil.isAllowPushNotify();

        switch_accept.setChecked(isAllowNotify);
        if(isAllowNotify){
            switch_voice.setVisibility(View.VISIBLE);
            switch_vibrate.setVisibility(View.VISIBLE);
            boolean isAllowVoice = mSharedUtil.isAllowVoice();
            if(isAllowVoice){
                switch_voice.setChecked(true);
            }else{
                switch_voice.setChecked(false);
            }

            boolean isAllowVibrate = mSharedUtil.isAllowVibrate();
            if(isAllowVibrate){
                switch_vibrate.setChecked(true);
            }else{
                switch_vibrate.setChecked(false);
            }
        }else {
            switch_voice.setVisibility(View.INVISIBLE);
            tv_voice.setVisibility(View.INVISIBLE);
            switch_vibrate.setVisibility(View.INVISIBLE);
            tv_vibrate.setVisibility(View.INVISIBLE);
            v_devier.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()){
            case R.id.toggle_btn_accept:
                switch_accept.setChecked(isChecked);
                mSharedUtil.setPushNotifyEnable(isChecked);
                if(!isChecked){
                    switch_voice.setVisibility(View.INVISIBLE);
                    tv_voice.setVisibility(View.INVISIBLE);
                    switch_vibrate.setVisibility(View.INVISIBLE);
                    tv_vibrate.setVisibility(View.INVISIBLE);
                    v_devier.setVisibility(View.INVISIBLE);
                }else{
                    switch_voice.setVisibility(View.VISIBLE);
                    tv_voice.setVisibility(View.VISIBLE);
                    switch_vibrate.setVisibility(View.VISIBLE);
                    tv_vibrate.setVisibility(View.VISIBLE);
                    v_devier.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.toggle_btn_voice:
                switch_voice.setChecked(isChecked);
                mSharedUtil.setAllowVoiceEnable(isChecked);
                break;
            case R.id.toggle_btn_vibrate:
                switch_vibrate.setChecked(isChecked);
                mSharedUtil.setAllowVibrateEnable(isChecked);
                break;
            default:
                break;
        }

    }
}
