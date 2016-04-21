package cn.xietong.healthysportsexperts.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 实现万能适配器精简代码
 * Created by mr.deng on 2016/4/16.
 */
public class ViewHolder {

    //比Map更高效的键值存储方式
    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    public ViewHolder(Context context, ViewGroup parent,int layoutId,int position){

        this.mPosition = position;
        this.mViews = new SparseArray<>();

        mConvertView = LayoutInflater.from(context).inflate(layoutId,parent,false);
        mConvertView.setTag(this);
    }

    /**
     * 用于生成ViewHolder的一个实例
     * @param context 上下文
     * @param convertView 缓存的每一行的views
     * @param parent 父容器View
     * @param layoutId 当前行的布局ID
     * @param position 当前行所在位置
     * @return holder的实例
     */
    public static  ViewHolder newInstance(Context context,View convertView,
                                          ViewGroup parent,int layoutId,int position){

        if(convertView == null){
            return new ViewHolder(context,parent,layoutId,position);
        }else{
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mPosition = position;
            return holder;
        }

    }

    /**
     * 获得Item布局中的View控件
     * @param viewId view的ID
     * @param <T> View类型的子类
     * @return v View类型的子类
     */
    public <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);

        if(view == null){
            view = mConvertView.findViewById(viewId);
            if (view != null)
            mViews.put(viewId,view);
        }

        return (T) view;
    }

    /**
     * 为adapter的getView方法提供返回值
     * @return 缓存的View或者新inflate的View
     */
    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 为TextView设置内容
     * @param viewId view的ID
     * @param text View显示的内容
     * @return 调用这个函数的对象本身,实现链式编程
     */
    public ViewHolder setText(int viewId,String text){
        TextView tv = getView(viewId);
        if(tv!=null)
        tv.setText(text);
        return this;
    }

    /**
     * 为TextView设置图片
     * @param viewId 布局Id
     * @param drawable drawable资源
     * @return
     */
    public ViewHolder setDrawableLeft(int viewId,Drawable drawable){
        TextView tv = getView(viewId);
        if(tv != null && drawable != null) {
            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            tv.setCompoundDrawables(drawable, null, null, null);
        }
        return this;
    }

    /**
     * 为ImageView设置内容
     * @param viewId view的Id
     * @param resId 需要显示图片的Id
     * @return 调用这个函数的对象本身,实现链式编程
     */
    public ViewHolder setImageResource(int viewId,int resId){
        ImageView iv = getView(viewId);
        if(iv!=null)
        iv.setImageResource(resId);
        return this;
    }

    /**
     * 为ImageView设置内容
     * @param viewId view的Id
     * @param bitmap 需要显示的bitmap
     * @return 调用这个函数的对象本身,实现链式编程
     */
    public ViewHolder setImageBitmap(int viewId,Bitmap bitmap){
        ImageView iv = getView(viewId);
        if(iv!=null)
        iv.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 为ImageView设置内容
     * @param viewId view的Id
     * @param url 需要显示图片的url
     * @return 调用这个函数的对象本身,实现链式编程
     */
    public ViewHolder setImageUrl(int viewId,String url){
        ImageView iv = getView(viewId);
        if(iv!=null)
        ImageLoader.getInstance().displayImage(url,iv, ImageLoaderOptions.getOptions());
        return this;
    }

    /**
     * 为View设置OnClick事件
     * @param viewId view的Id
     * @param onClickListener 点击监听器
     * @return
     */
    public ViewHolder setOnClickListener(int viewId, View.OnClickListener onClickListener){
        View v = getView(viewId);
        if(v != null){
            v.setOnClickListener(onClickListener);
        }
        return this;
    }

    public ViewHolder setChecked(int viewId,boolean isChecked){
        CompoundButton cBtn = getView(viewId);
        if(cBtn != null){
            cBtn.setChecked(isChecked);
        }
        return this;
    }

    /**
     * 为switch类型控件设置装填监听
     * @param viewId view的Id
     * @param onCheckedChangeListener switch类型控件设置监听器
     * @return
     */
    public ViewHolder setOnCheckedChangeListener(int viewId, CompoundButton.OnCheckedChangeListener onCheckedChangeListener){
        CompoundButton cBtn = getView(viewId);
        if(cBtn != null){
            cBtn.setOnCheckedChangeListener(onCheckedChangeListener);
        }
        return this;
    }

}
