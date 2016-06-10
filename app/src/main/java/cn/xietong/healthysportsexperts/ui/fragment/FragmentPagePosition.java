package cn.xietong.healthysportsexperts.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.ui.view.DropPop;

/**计步和发帖界面
 * Created by mr.deng on 2015/10/14.
 */
public class FragmentPagePosition extends BaseFragment implements View.OnClickListener{

    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    private FragmentPagerAdapter mAdapter;
    //泡泡窗声明
    private DropPop dropPop;

    //记录按钮
    private Button btn_recorder;
    //显示帖子界面按钮
    private Button btn_sportShow;
    //回复提醒按钮
    private Button btn_discuss;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_1;
    }

    @Override
    public void setupViews(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化监听事件
        initEvent();
        //默认显示第一个子Fragment
        setSelect(0);
    }

    protected void initViews(View mContentView){
        mViewPager = (ViewPager) mContentView.findViewById(R.id.id_viewPager);
        btn_recorder = (Button) mContentView.findViewById(R.id.btn_recorder);
        btn_sportShow = (Button) mContentView.findViewById(R.id.btn_sportShow);
        btn_discuss = (Button) mContentView.findViewById(R.id.btn_discuss);

        mFragments = new ArrayList<Fragment>();
        Fragment mTab1 = new FragmentPagePosition_son1();
        Fragment mTab2 = new FragmentPagePosition_son2();
        mFragments.add(mTab1);
        mFragments.add(mTab2);

        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mFragments.get(i);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i1) {
                int currentItem = mViewPager.getCurrentItem();
                setTab(currentItem);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * 为按钮初始化监听事件
     */
    private void initEvent() {
        btn_recorder.setOnClickListener(this);
        btn_sportShow.setOnClickListener(this);
        btn_discuss.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_recorder:
                setSelect(0);
                break;
            case R.id.btn_sportShow:
                setSelect(1);
                break;
            case R.id.btn_discuss:
                btn_discuss.setBackgroundResource(R.drawable.icon_discuss_sel);
                if(dropPop==null){
                    OnClickListener paramOnClickListener = new OnClickListener();
                    dropPop = new DropPop(getActivity(),paramOnClickListener);

                    //监听窗口消失事件，当窗口消失时更换评论按钮的背景
                    dropPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            dropPop = null;
                            btn_discuss.setBackgroundResource(R.drawable.icon_discuss_nor);
                        }
                    });
                }
                    //设置默认获取焦点
                    dropPop.setFocusable(true);
                    //以某个控件的偏移量显示窗口
                    dropPop.showAsDropDown(btn_discuss, 0, 1);
                    //如果存在则更新
                    dropPop.update();
                break;
            default:
                break;
        }
    }

    private void setSelect(int i) {
        setTab(i);
        mViewPager.setCurrentItem(i);
    }

    /**根据选择不同的按钮更换按钮的背景
     * @param i 显示哪一个界面，0显示计步界面，1显示发帖界面
     */
    private void setTab(int i){

        switch (i){
            case 0:
                btn_recorder.setBackgroundResource(R.drawable.top_btn_sel_drawable);
                btn_recorder.setTextColor(Color.parseColor("#ffffff"));
                btn_sportShow.setBackgroundResource(R.drawable.top_btn_nor_drawable);
                btn_sportShow.setTextColor(Color.parseColor("#56abe4"));
                break;
            case 1:
                btn_sportShow.setBackgroundResource(R.drawable.top_btn_sel_drawable);
                btn_sportShow.setTextColor(Color.parseColor("#ffffff"));
                btn_recorder.setBackgroundResource(R.drawable.top_btn_nor_drawable);
                btn_recorder.setTextColor(Color.parseColor("#56abe4"));
                break;
            default:
                break;
        }
    }

    /**对泡泡窗中的每个按钮的点击事件进行监听
     */
    class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_alert:

                    break;
                case R.id.layout_theme:

                    break;

                case R.id.layout_reply:

                    break;

                default:
                    break;
            }

        }

    }
}
