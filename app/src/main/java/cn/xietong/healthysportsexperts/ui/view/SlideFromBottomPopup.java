package cn.xietong.healthysportsexperts.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Toast;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.service.HealthyService;
import cn.xietong.healthysportsexperts.ui.fragment.FragmentPageRun;


/**
 * 林思旭
 * Created by 大灯泡 on 2016.7.26
 * 从底部滑上来的popup
 */
public class SlideFromBottomPopup extends BasePopupWindow implements View.OnClickListener{

    private View popupView;
    private Button btn_startAndStop;
    private  PowerManager pm = null;
    private PowerManager.WakeLock wakeLock = null;
    private boolean isRegister = false;
    public SlideFromBottomPopup(Activity context) {
        super(context);
        bindEvent();
    }

    //底部
    @Override
    protected Animation getShowAnimation() {
        return getTranslateAnimation(250*2,0,300);
    }

    @Override
    protected Animation getShowTopAnimation() {
        return getTranslateAnimation(-250*2,0,300);
    }

    //消失
    @Override
    protected View getClickToDismissView() {
        return popupView.findViewById(R.id.click_to_dismiss);
    }

//    @Override
//    protected View getClickToTopDismissView() {
//        return topPopupView.findViewById(R.id.top_click_to_dismiss);
//    }

    @Override
    public View getPopupView() {
        popupView= LayoutInflater.from(mContext).inflate(R.layout.popup_slide_from_bottom,null);
        return popupView;
    }
//    @Override
//    public View getTopPopupView() {
//        topPopupView= LayoutInflater.from(mContext).inflate(R.layout.popoup_slide_from_top,null);
//        return topPopupView;
//    }

    @Override
    public View getAnimaView() {
        return popupView.findViewById(R.id.popup_anima);
    }

    @Override
    public View getTopAnimaView() {
        return popupView.findViewById(R.id.top_popup_anima);
    }




    private void bindEvent() {
        if (popupView!=null){
            popupView.findViewById(R.id.btn_cancle).setOnClickListener(this);
            btn_startAndStop = (Button) popupView.findViewById(R.id.btn_Start);
            btn_startAndStop.setOnClickListener(this);
            popupView.findViewById(R.id.btn_Stop).setOnClickListener(this);
        }
//        LogUtils.i(getClass().getName(),"timer="+time);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancle:
                dismiss();
//                Intent intent_stop = new Intent(mContext, AniService.class);
//                mContext.stopService(intent_stop);
                break;
            case R.id.btn_Start:
                if (!isRegister) {
                    Toast.makeText(mContext, "正在开启轨迹服务，请稍候", Toast.LENGTH_LONG).show();
                    Intent intent_open = new Intent(HealthyService.IsOpenService);
                    intent_open.putExtra("TrueOrFalse",1);
                    mContext.sendBroadcast(intent_open);
                    if (null == pm) {
                        pm = (PowerManager) FragmentPageRun.mContext.getSystemService(Context.POWER_SERVICE);
                    }
                    if (null == wakeLock) {
                        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TrackUpload");
                    }
                    isRegister = true;
                    setColorButton(btn_startAndStop,true,"暂停");
                    HealthyService.client.startTrace(HealthyService.trace, FragmentPageRun.startTraceListener);
                }else{
                    Toast.makeText(mContext, "正在暂停轨迹服务，请稍候……", Toast.LENGTH_SHORT).show();
                    Intent intent_close = new Intent(HealthyService.IsOpenService);
                    intent_close.putExtra("TrueOrFalse",0);
                    mContext.sendBroadcast(intent_close);
                    isRegister = false;
                    setColorButton(btn_startAndStop,false,"继续");
                    HealthyService.client.stopTrace(HealthyService.trace, FragmentPageRun.stopBackListener);
                }
                break;
            case R.id.btn_Stop:
                Toast.makeText(mContext, "正在停止轨迹服务，请稍候", Toast.LENGTH_SHORT).show();
//                if (isRegister) {
//                    try {
//                        Intent intent_close = new Intent(AniService.IsOpenService);
//                        intent_close.putExtra("TrueOrFalse",0);
//                        mContext.sendBroadcast(intent_close);
//                        isRegister = false;
//                    } catch (Exception e) {
//                    }
//                }
//                AniService.client.stopTrace(AniService.trace, TrackUploadFragment.stopTraceListener);
                break;
            default:
                break;
        }
    }


    /**
     *
     * @param btn_select 需要改变颜色和内容的按钮
     * @param flag 需要改变的颜色
     * @param txt_content 需要改变的内容
     */
    private void setColorButton(Button btn_select,boolean flag,String txt_content){
//        btn_select.setBackgroundColor(mContext.getResources().getColor(color));
//        btn_select.setText(txt_content);
        btn_select.setText(txt_content);
        btn_select.setSelected(flag);
    }

}
