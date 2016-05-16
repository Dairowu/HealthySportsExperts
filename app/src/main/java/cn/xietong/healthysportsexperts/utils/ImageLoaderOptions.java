package cn.xietong.healthysportsexperts.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Created by deng on 2016/4/20
 */
public class ImageLoaderOptions {

    public static DisplayImageOptions getOptions(){
        DisplayImageOptions options =new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .build();
        return options;
    }

}
