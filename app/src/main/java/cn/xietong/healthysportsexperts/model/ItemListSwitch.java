package cn.xietong.healthysportsexperts.model;

import android.widget.CompoundButton;

/**
 * 设置个人信息List的bean类
 * Created by mr.deng on 2016/4/20.
 */
public class ItemListSwitch extends ItemListViewBean{

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
    private boolean isChecked;

    public ItemListSwitch(int type,String title,boolean isChecked){
        super(type,title,"","");
        this.isChecked = isChecked;


    }

    public ItemListSwitch(int type, String title, String content_text, int content_photoRes){
        super(type, title, content_text, content_photoRes);
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public CompoundButton.OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public boolean getChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
