package cn.xietong.healthysportsexperts.ui.activity;

import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.adapter.UserInfoAdapter;
import cn.xietong.healthysportsexperts.config.BmobConstants;
import cn.xietong.healthysportsexperts.model.ItemListViewBean;
import cn.xietong.healthysportsexperts.model.MyUser;

/**
 * 个人信息展示界面
 * Created by mr.deng on 2016/4/18.
 */
public class MyInfoActivity extends BaseActivity{

    private ListView mList;
    private List<ItemListViewBean> mDatas;
    private int[] layouts;
    private MyUser user;

    @Override
    public int getLayoutId() {
        return R.layout.activity_myinfo;
    }

    @Override
    public void initViews() {
        initTopbarForLeft("个人信息",new OnTopbarButtonClickListener());
        topBar.setButtonVisable(1,false);
        mList = (ListView) findViewById(R.id.id_list_userInfo);
        user = mUserManager.getCurrentUser(MyUser.class);
        mDatas = new ArrayList<>();
        layouts = new int[]{R.layout.listitem_1tv,R.layout.listitem_2tv,R.layout.listitem_tv_iv};
        initData();
        UserInfoAdapter adapter = new UserInfoAdapter(this,mDatas,layouts);
        mList.setAdapter(adapter);
    }

    @Override
    public void setupViews() {

    }

    /**
     * 初始化ListView的数据
     */
    private void initData() {
        ItemListViewBean item1 = new ItemListViewBean(BmobConstants.LAYOUT_ONE_TV,"","",null);
        mDatas.add(item1);

        ItemListViewBean item2 = new ItemListViewBean(BmobConstants.LAYOUT_TV_IV,"头像","",user.getAvatar());
        mDatas.add(item2);

        ItemListViewBean item3 = new ItemListViewBean(BmobConstants.LAYOUT_TWO_TV,"昵称",user.getNick(),null);
        mDatas.add(item3);

        ItemListViewBean item4 = new ItemListViewBean(BmobConstants.LAYOUT_TWO_TV,"账号",user.getUsername(),null);
        mDatas.add(item4);

        ItemListViewBean item5 = new ItemListViewBean(BmobConstants.LAYOUT_ONE_TV,"","",null);
        mDatas.add(item5);

        String sex;
        if(user.getSex()){
            sex = "男";
        }else {
            sex = "女";
        }
        ItemListViewBean item6 = new ItemListViewBean(BmobConstants.LAYOUT_TWO_TV,"性别",sex,null);
        mDatas.add(item6);

        ItemListViewBean item7 = new ItemListViewBean(BmobConstants.LAYOUT_TWO_TV,"个性签名",user.getSignature(),null);
        mDatas.add(item7);

        ItemListViewBean item8 = new ItemListViewBean(BmobConstants.LAYOUT_ONE_TV,"","",null);
        mDatas.add(item8);
    }

}
