package cn.xietong.healthysportsexperts.ui.activity;

import android.widget.CompoundButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.adapter.SetRemindAdapter;
import cn.xietong.healthysportsexperts.config.BmobConstants;
import cn.xietong.healthysportsexperts.model.ItemListSwitch;
import cn.xietong.healthysportsexperts.utils.SharePreferenceUtil;

/**用户设置提醒相关的配置信息
 * Created by Administrator on 2015/11/15.
 */
public class Activity_Remind extends BaseActivity {

    private ListView mList;
    private List<ItemListSwitch> mDatas;
    //传递给Adapter的布局数组
    private int[] layouts;
    private SetRemindAdapter mAdapter;

    SharePreferenceUtil mSharedUtil;

    @Override
    public int getLayoutId() {
        return R.layout.activity_myinfo;
    }

    @Override
    public void initViews() {

        initTopbarForLeft("消息与提醒",new OnTopbarButtonClickListener());

        mSharedUtil = mApplication.getSharedPreferencesUtil();

        topBar.setButtonVisable(1,false);
        mList = (ListView) findViewById(R.id.id_list_userInfo);
        mDatas = new ArrayList<>();
        layouts = new int[]{R.layout.listitem_1tv,R.layout.listitem_tv_swich};
        initData();
        mAdapter = new SetRemindAdapter(this,mDatas,layouts);
        mList.setAdapter(mAdapter);

    }

    @Override
    public void setupViews() {

    }

    /**
     * 初始化List的数据
     */
    public void initData(){
        final ItemListSwitch item1 = new ItemListSwitch(BmobConstants.LAYOUT_TV_SWITCH,"接收新消息通知", mSharedUtil.isAllowPushNotify());

        final ItemListSwitch item2 = new ItemListSwitch(BmobConstants.LAYOUT_TV_SWITCH,"声音", mSharedUtil.isAllowVoice());

        final ItemListSwitch item3 = new ItemListSwitch(BmobConstants.LAYOUT_TV_SWITCH,"震动", mSharedUtil.isAllowVibrate());

        ItemListSwitch item4 = new ItemListSwitch(BmobConstants.LAYOUT_ONE_TV,"",false);

        item1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item1.setChecked(isChecked);
                //通过判断是否选中决定是否加载后两行
                if(!isChecked){
                    mDatas.remove(item2);
                    mDatas.remove(item3);
                    mAdapter.notifyDataSetChanged();
                }else if(mDatas.size() == 3){
                    mDatas.add(item2);
                    mDatas.add(item3);
                    mAdapter.notifyDataSetChanged();
                }

                mSharedUtil.setPushNotifyEnable(isChecked);
            }
        });

        item2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item2.setChecked(isChecked);
                mSharedUtil.setAllowVoiceEnable(isChecked);
            }
        });

        item3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item3.setChecked(isChecked);
                mSharedUtil.setAllowVibrateEnable(isChecked);
            }
        });

        mDatas.add(item4);
        mDatas.add(item1);
        mDatas.add(item4);
        if( mSharedUtil.isAllowPushNotify()){
            mDatas.add(item2);
            mDatas.add(item3);
        }
    }
}
