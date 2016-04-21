package cn.xietong.healthysportsexperts.adapter;

import android.content.Context;

import java.util.List;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.model.ItemListSwitch;
import cn.xietong.healthysportsexperts.utils.ViewHolder;

/**设置提醒界面的Adapter
 * Created by mr.deng on 2016/4/20.
 */
public class SetRemindAdapter extends CommonAdapter<ItemListSwitch>{

    public SetRemindAdapter(Context context, List<ItemListSwitch> datas, int... layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        return  mDatas.get(position).getType();
    }

    @Override
    protected void initConvert(ViewHolder holder, ItemListSwitch itemListSwitch) {
        holder.setText(R.id.id_listItem_title,itemListSwitch.getTitle());
        holder.setOnCheckedChangeListener(R.id.id_listItem_switch, itemListSwitch.getOnCheckedChangeListener());
        holder.setChecked(R.id.id_listItem_switch,itemListSwitch.getChecked());
    }

}
