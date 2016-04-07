package cn.xietong.healthysportsexperts.ui.activity;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.utils.SharePreferenceUtil;

/**
 * Created by Administrator on 2015/11/15.
 */
public class Activity_Remind extends BaseActivity implements CompoundButton.OnCheckedChangeListener{

    //初始化三个选择按钮
    private ToggleButton toggle_accept,toggle_voice,toggle_vibrate;

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

        toggle_accept = (ToggleButton) findViewById(R.id.toggle_btn_accept);
        toggle_voice  = (ToggleButton) findViewById(R.id.toggle_btn_voice);
        toggle_vibrate = (ToggleButton) findViewById(R.id.toggle_btn_vibrate);

        tv_voice  = (TextView) findViewById(R.id.tv_voice );
        tv_vibrate = (TextView) findViewById(R.id.tv_vibrate);

        v_devier = findViewById(R.id.view_devider);

        toggle_accept.setOnCheckedChangeListener(this);
        toggle_voice.setOnCheckedChangeListener(this);
        toggle_vibrate.setOnCheckedChangeListener(this);

    }

    @Override
    public void setupViews() {

        boolean isAllowNotify = mSharedUtil.isAllowPushNotify();

        toggle_accept.setChecked(isAllowNotify);
        if(isAllowNotify){
            toggle_voice.setVisibility(View.VISIBLE);
            toggle_vibrate.setVisibility(View.VISIBLE);
            boolean isAllowVoice = mSharedUtil.isAllowVoice();
            if(isAllowVoice){
                toggle_voice.setChecked(true);
            }else{
                toggle_voice.setChecked(false);
            }

            boolean isAllowVibrate = mSharedUtil.isAllowVibrate();
            if(isAllowVibrate){
                toggle_vibrate.setChecked(true);
            }else{
                toggle_vibrate.setChecked(false);
            }
        }else {
            toggle_voice.setVisibility(View.INVISIBLE);
            tv_voice.setVisibility(View.INVISIBLE);
            toggle_vibrate.setVisibility(View.INVISIBLE);
            tv_vibrate.setVisibility(View.INVISIBLE);
            v_devier.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()){
            case R.id.toggle_btn_accept:
                toggle_accept.setChecked(isChecked);
                mSharedUtil.setPushNotifyEnable(isChecked);
                if(!isChecked){
                    toggle_voice.setVisibility(View.INVISIBLE);
                    tv_voice.setVisibility(View.INVISIBLE);
                    toggle_vibrate.setVisibility(View.INVISIBLE);
                    tv_vibrate.setVisibility(View.INVISIBLE);
                    v_devier.setVisibility(View.INVISIBLE);
                }else{
                    toggle_voice.setVisibility(View.VISIBLE);
                    tv_voice.setVisibility(View.VISIBLE);
                    toggle_vibrate.setVisibility(View.VISIBLE);
                    tv_vibrate.setVisibility(View.VISIBLE);
                    v_devier.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.toggle_btn_voice:
                toggle_voice.setChecked(isChecked);
                mSharedUtil.setAllowVoiceEnable(isChecked);
                break;
            case R.id.toggle_btn_vibrate:
                toggle_vibrate.setChecked(isChecked);
                mSharedUtil.setAllowVibrateEnable(isChecked);
                break;
            default:
                break;
        }

    }
}
