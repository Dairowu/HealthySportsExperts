package cn.xietong.healthysportsexperts.adapter;

import android.content.Context;
import android.util.Log;

import java.util.List;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.model.ItemListViewBean;
import cn.xietong.healthysportsexperts.utils.ViewHolder;

/**个人信息Adapter
 * Created by mr.deng on 2016/4/18.
 */
public class UserInfoAdapter extends CommonAdapter<ItemListViewBean> {

    public UserInfoAdapter(Context context, List datas, int... layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        return mDatas.get(position).getType();
    }

    @Override
    protected void initConvert(ViewHolder holder, ItemListViewBean itemListViewBean) {

        try {
            if(itemListViewBean.getContent_photoRes() != 0)
            holder.setDrawableLeft(R.id.id_listItem_title, mContext.getResources().
                    getDrawable(itemListViewBean.getContent_photoRes()));
        }catch (Exception e){
            Log.i("info",e.toString());
        }
        holder.setText(R.id.id_listItem_title,itemListViewBean.getTitle());
        holder.setText(R.id.id_listItem_content,itemListViewBean.getContent_text());
        holder.setImageUrl(R.id.id_listItem_iv,itemListViewBean.getContent_photoUrl());

    }

}
