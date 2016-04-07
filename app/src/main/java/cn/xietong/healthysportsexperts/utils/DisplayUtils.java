package cn.xietong.healthysportsexperts.utils;

import android.content.Context;

import cn.xietong.healthysportsexperts.app.App;

/**
 * Created by Administrator on 2015/12/14.
 */
public class DisplayUtils {

    private static Context mContext = App.getInstance().getApplicationContext();

    /**
     * px转dp
     * @param pxValue
     * @return
     */
    public static int px2dp(float pxValue){
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue/scale + 0.5f);
    }

    /**
     * dp转px
     * @param dpValue
     * @return
     */
    public static int dp2px(float dpValue){
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }

    /**
     * px转sp
     * @param pxValue
     * @return
     */
    public static int px2sp(float pxValue){
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    /**
     * sp转px
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue){
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int)(spValue * scale + 0.5f);
    }

}
