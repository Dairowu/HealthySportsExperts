package cn.xietong.healthysportsexperts.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.FindListener;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.adapter.UserInfoAdapter;
import cn.xietong.healthysportsexperts.config.BmobConstants;
import cn.xietong.healthysportsexperts.model.ItemListViewBean;
import cn.xietong.healthysportsexperts.model.MyUser;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by mr.deng on 2016/5/7
 */
public class DetailInfoActivity extends BaseActivity{

    private ListView mList;
    private List<ItemListViewBean> mDatas;
    private UserInfoAdapter adapter;
    private int[] layouts;
    private MyUser user;
    CompositeSubscription subscription = new CompositeSubscription();
    private String TAG = "DetailInfoActivity";

    String from;
    String userName;

    public static void actionStart(Context context, String from, String userName){
        Intent intent = new Intent(context,DetailInfoActivity.class);
        intent.putExtra("from",from);
        intent.putExtra("userName",userName);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_myinfo;
    }

    @Override
    public void initViews() {
        from = getIntent().getStringExtra("from");
        userName = getIntent().getStringExtra("userName");

        initTopbarForLeft("详细信息",new OnTopbarButtonClickListener());
        topBar.setButtonVisable(1,false);

        mList = (ListView) findViewById(R.id.id_list_userInfo);
        mDatas = new ArrayList<>();
        layouts = new int[]{R.layout.listitem_1tv,R.layout.listitem_iv_tv,R.layout.listitem_2tv_iv,R.layout.listitem_1button};

        if(from != null && from.equals("me")){
            Log.i(TAG,"zhixing");
            user = mUserManager.getCurrentUser(MyUser.class);
            initData();
        }else{
            Log.i(TAG,userName);

            /**
             * TODO 使用RxJava解决异步问题
             */
            mUserManager.queryUser(userName, new FindListener<MyUser>() {

                @Override
                public void onError(int i, String s) {
                    Log.i(TAG,s+"error");
                }

                @Override
                public void onSuccess(List<MyUser> list) {
                    Log.i(TAG,list.toString()+"null");
                    if(list != null & list.size() > 0) {
                        user = list.get(0);
                    }
                    initData();
                    adapter.notifyDataSetChanged();
                }

            });
        }


        adapter = new UserInfoAdapter(this,mDatas,layouts);
        mList.setAdapter(adapter);
    }

    private void initData() {

        ItemListViewBean item1 = new ItemListViewBean(BmobConstants.LAYOUT_ONE_TV,"","",null);
        mDatas.add(item1);

        ItemListViewBean item2 = new ItemListViewBean(BmobConstants.LAYOUT_TWOTV_IV,user.getNick(),user.getSignature(),user.getAvatar());
        mDatas.add(item2);

        ItemListViewBean item3 = new ItemListViewBean(BmobConstants.LAYOUT_IV_TV,user.getUsername(),"",R.drawable.icon_id_btn);
        mDatas.add(item3);

        String sex;
        if(user.getSex()){
            sex = "男";
        }else {
            sex = "女";
        }
        ItemListViewBean item4 = new ItemListViewBean(BmobConstants.LAYOUT_TWO_TV,sex,"",R.drawable.icon_sex);
        mDatas.add(item4);

        ItemListViewBean item5 = new ItemListViewBean(BmobConstants.LAYOUT_ONE_TV,"","",null);
        mDatas.add(item5);


        ItemListViewBean item6 = new ItemListViewBean(BmobConstants.LAYOUT_ONE_BTN,"发送消息","",null);
        mDatas.add(item6);

        ItemListViewBean item7 = new ItemListViewBean(BmobConstants.LAYOUT_ONE_TV,"","",null);
        mDatas.add(item7);


    }

    @Override
    public void setupViews() {

    }
}
