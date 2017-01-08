package cn.xietong.healthysportsexperts.ui.activity;

import cn.xietong.healthysportsexperts.R;

/**
 * Created by mr.deng on 2017/1/2.
 */

public class AboutUsActivity extends BaseActivity{

    @Override
    public int getLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    public void initViews() {
        initTopbarForLeft("关于我们",new OnTopbarButtonClickListener());
    }

    @Override
    public void setupViews() {

    }
}
