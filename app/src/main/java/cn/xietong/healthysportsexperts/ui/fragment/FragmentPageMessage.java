package cn.xietong.healthysportsexperts.ui.fragment;

import android.os.Bundle;
import android.view.View;

import cn.xietong.healthysportsexperts.R;

/**对应着Tab里第二个按钮所显示的界面，跟计步界面一样，该布局应该包含两个Fragment
 * 一个显示消息界面，一个显示联系人界面
 * Created by Administrator on 2015/10/14.
 */
public class FragmentPageMessage extends BaseFragment {

    @Override
    public int getLayoutId() {
        return R.layout.fragment_2;
    }

    @Override
    protected void initViews(View mContentView) {

    }

    @Override
    public void setupViews(Bundle savedInstanceState) {

    }
}
