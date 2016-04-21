package cn.xietong.healthysportsexperts.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import cn.xietong.healthysportsexperts.R;

/**
 * Created by deng on 2016/4/20.
 */
public class ImageLoaderOptions {

    public static DisplayImageOptions getOptions(){
        DisplayImageOptions options =new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.icon_head)
                .showImageOnFail(R.drawable.icon_head)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .displayer(new FadeInBitmapDisplayer(100))
                .build();
        return options;
    }

}
