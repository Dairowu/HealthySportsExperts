package cn.xietong.healthysportsexperts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.xietong.healthysportsexperts.utils.ViewHolder;

/**
 * Created by mr.deng on 2016/4/16
 */
public abstract class CommonAdapter<T> extends BaseAdapter{

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<T> mDatas;
    //将布局的layoutId设置为一个数组类型用于适配listView中有多种布局的场景
    private int[] layoutId;

    public CommonAdapter(Context context, List<T> datas,int... layoutId){
        this.mContext = context;
        this.mDatas = datas;
        this.layoutId = layoutId;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return layoutId.length;
    }

    /**
     * 得到当前行的Type类型
     * @param position 位置
     * @return 返回当前行的Type类型应该为构造Adapter时传入layoutId的序号
     */
    @Override
    public abstract int getItemViewType(int position);

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        ViewHolder holder = ViewHolder.newInstance(mContext,convertView,parent,
                layoutId[getItemViewType(position)],position);

        initConvert(holder,getItem(position));

        return holder.getConvertView();

    }

    /**
     * 调用者实现控件内容的设置
     * @param holder
     * @param t
     */
    protected abstract void initConvert(ViewHolder holder, T t);

}
