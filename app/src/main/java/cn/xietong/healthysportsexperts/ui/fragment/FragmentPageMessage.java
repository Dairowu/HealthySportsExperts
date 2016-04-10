package cn.xietong.healthysportsexperts.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.ui.activity.Activity_SearchFriend;

/**对应着Tab里第二个按钮所显示的界面，跟计步界面一样，该布局应该包含两个Fragment
 * 一个显示消息界面，一个显示联系人界面
 * Created by Administrator on 2015/10/14.
 */
public class FragmentPageMessage extends BaseFragment implements View.OnClickListener{

    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    private FragmentPagerAdapter mAdapter;

    //记录按钮
    private Button btn_message;
    //显示帖子界面按钮
    private Button btn_contact;
    //回复提醒按钮
    private Button btn_add_contact;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_2;
    }

    @Override
    protected void initViews(View mContentView) {

        mViewPager = (ViewPager) mContentView.findViewById(R.id.id_viewPager_message);
        btn_message = (Button) mContentView.findViewById(R.id.btn_message);
        btn_contact = (Button) mContentView.findViewById(R.id.btn_contact);
        btn_add_contact = (Button) mContentView.findViewById(R.id.btn_add_contact);

        mFragments = new ArrayList<Fragment>();
        Fragment mTab1 = new FragmentPageMessage_son1();
        Fragment mTab2 = new FragmentPageMessage_son2();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化监听事件
        initEvent();
        //默认显示第一个子Fragment
        setSelect(0);
    }


    /**
     * 为按钮初始化监听事件
     */
    private void initEvent() {
        btn_message.setOnClickListener(this);
        btn_contact.setOnClickListener(this);
        btn_add_contact.setOnClickListener(this);
    }


    @Override
    public void setupViews(Bundle savedInstanceState) {

    }

    private void setSelect(int i) {
        setTab(i);
        mViewPager.setCurrentItem(i);
    }

    /**根据选择不同的按钮更换按钮的背景
     * @param i 显示哪一个界面，0显示消息界面，1显示联系人界面
     */
    private void setTab(int i){

        switch (i){
            case 0:
                btn_message.setBackgroundResource(R.drawable.top_btn_sel_drawable);
                btn_message.setTextColor(Color.parseColor("#ffffff"));
                btn_contact.setBackgroundResource(R.drawable.top_btn_nor_drawable);
                btn_contact.setTextColor(Color.parseColor("#56abe4"));
                break;
            case 1:
                btn_contact.setBackgroundResource(R.drawable.top_btn_sel_drawable);
                btn_contact.setTextColor(Color.parseColor("#ffffff"));
                btn_message.setBackgroundResource(R.drawable.top_btn_nor_drawable);
                btn_message.setTextColor(Color.parseColor("#56abe4"));
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_message:
                setSelect(0);
                break;
            case R.id.btn_contact:
                setSelect(1);
                break;
            case R.id.btn_add_contact:
                /**
                 * 林思旭，2016.4.9
                 */
                Intent intent = new Intent(getActivity(), Activity_SearchFriend.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
