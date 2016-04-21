package cn.xietong.healthysportsexperts.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.adapter.UserInfoAdapter;
import cn.xietong.healthysportsexperts.config.BmobConstants;
import cn.xietong.healthysportsexperts.model.ItemListViewBean;
import cn.xietong.healthysportsexperts.model.MyUser;
import cn.xietong.healthysportsexperts.ui.activity.Activity_Login;
import cn.xietong.healthysportsexperts.ui.activity.Activity_Remind;
import cn.xietong.healthysportsexperts.ui.activity.MyInfoActivity;

/**我的界面，该界面用于显示用户个人信息等
 * Created by Administrator on 2015/10/14.
 */
public class FragmentPageMy extends BaseFragment{

    private ListView mList;
    private List<ItemListViewBean> mDatas;
    private int[] layouts;
    private MyUser user;

    @Override
    public int getLayoutId() {
        return R.layout.activity_myinfo;
    }

    @Override
    protected void initViews(View mContentView) {

        initTopbarForOnlyTitle("我");
        topBar.setButtonVisable(1,false);
        mList = (ListView) findViewById(R.id.id_list_userInfo);
        user = mUserManager.getCurrentUser(MyUser.class);
        mDatas = new ArrayList<>();
        layouts = new int[]{R.layout.listitem_1tv,R.layout.listitem_iv_tv,R.layout.listitem_2tv_iv};
        initData();
        UserInfoAdapter adapter = new UserInfoAdapter(getActivity(),mDatas,layouts);
        mList.setAdapter(adapter);

    }

    @Override
    public void setupViews(Bundle savedInstanceState) {

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        startActivity(new Intent(getActivity(), MyInfoActivity.class));
                        break;
                    case 4:
                        Intent intent = new Intent(getActivity(), Activity_Remind.class);
                        startActivity(intent);
                        break;
                    case 6:
                        break;
                    case 8:
                        mApplication.logout();
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), Activity_Login.class));
                        break;
                    default:
                        break;
                }
            }
        });

    }
    
    /**
     * 初始化ListView的数据
     */
    private void initData() {
        ItemListViewBean item1 = new ItemListViewBean(BmobConstants.LAYOUT_ONE_TV,"","",null);
        mDatas.add(item1);

        ItemListViewBean item2 = new ItemListViewBean(BmobConstants.LAYOUT_TWOTV_IV,user.getNick(),user.getSignature(),user.getAvatar());
        mDatas.add(item2);

        ItemListViewBean item3 = new ItemListViewBean(BmobConstants.LAYOUT_IV_TV,user.getUsername(),"",R.drawable.icon_id_btn);
        mDatas.add(item3);

        ItemListViewBean item4 = new ItemListViewBean(BmobConstants.LAYOUT_ONE_TV,"","",null);
        mDatas.add(item4);

        ItemListViewBean item5 = new ItemListViewBean(BmobConstants.LAYOUT_IV_TV,"消息与提醒","",R.drawable.icon_btn_remind);
        mDatas.add(item5);

        ItemListViewBean item6 = new ItemListViewBean(BmobConstants.LAYOUT_ONE_TV,"","",null);
        mDatas.add(item6);

        ItemListViewBean item7 = new ItemListViewBean(BmobConstants.LAYOUT_IV_TV,"关于软件","",R.drawable.icon_btn_about);
        mDatas.add(item7);

        ItemListViewBean item8 = new ItemListViewBean(BmobConstants.LAYOUT_ONE_TV,"","",null);
        mDatas.add(item8);

        ItemListViewBean item9 = new ItemListViewBean(BmobConstants.LAYOUT_IV_TV,"退出登录","",R.drawable.icon_btn_exit);
        mDatas.add(item9);

        ItemListViewBean item10 = new ItemListViewBean(BmobConstants.LAYOUT_ONE_TV,"","",null);
        mDatas.add(item10);

    }
}
