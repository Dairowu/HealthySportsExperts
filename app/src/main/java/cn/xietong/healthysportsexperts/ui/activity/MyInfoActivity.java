package cn.xietong.healthysportsexperts.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.adapter.UserInfoAdapter;
import cn.xietong.healthysportsexperts.config.BmobConstants;
import cn.xietong.healthysportsexperts.model.ItemListViewBean;
import cn.xietong.healthysportsexperts.model.MyUser;
import cn.xietong.healthysportsexperts.ui.view.ScaleRulerView;
import cn.xietong.healthysportsexperts.utils.CommonUtils;

/**
 * 个人信息展示界面
 * Created by mr.deng on 2016/4/18.
 */
public class MyInfoActivity extends BaseActivity{

    private ListView mList;
    private List<ItemListViewBean> mDatas;
    private UserInfoAdapter adapter;
    private int[] layouts;
    private MyUser user;
    private float height;
    private float weight;

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
        adapter = new UserInfoAdapter(this,mDatas,layouts);
        mList.setAdapter(adapter);
    }

    @Override
    public void setupViews() {

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LayoutInflater inflater = LayoutInflater.from(MyInfoActivity.this);
                LinearLayout linearLayout = new LinearLayout(MyInfoActivity.this,null);
                switch (position){
                    case 1:
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent,BmobConstants.REQUESTCODE_TAKE_CAMERA);
                        break;
                    case 2:
                        ChangeInfoActivity.actionStart(MyInfoActivity.this,
                                R.layout.activity_change_info,
                                "更改昵称",mDatas.get(2).getContent_text());
                        break;
                    case 3:
                        break;
                    case 5:
                        CharSequence[] choice = new CharSequence[]{"男","女"};
                        new AlertDialog.Builder(MyInfoActivity.this)
                                .setSingleChoiceItems(choice,0, new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case 0:
                                                user.setSex(true);
                                                mDatas.get(5).setContent_text("男");
                                                adapter.notifyDataSetChanged();
                                                dialog.dismiss();
                                                break;
                                            case 1:
                                                user.setSex(false);
                                                mDatas.get(5).setContent_text("女");
                                                adapter.notifyDataSetChanged();
                                                dialog.dismiss();
                                                break;
                                        }
                                    }
                                })
                                .create()
                                .show();
                        break;
                    case 6:
                        height = user.getHeight();
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        View choose_height = inflater.inflate(R.layout.choose_height,linearLayout);
                        ScaleRulerView scaleRulerView_height = (ScaleRulerView) choose_height.findViewById(R.id.id_height_scale);
                        scaleRulerView_height.setmCurrentValue(height);
                        final TextView mTextHeight = (TextView) choose_height.findViewById(R.id.tv_user_height_value);
                        mTextHeight.setText(String.valueOf(height));
                        scaleRulerView_height.setOnValueChangedListener(new ScaleRulerView.OnValueChangedListener() {
                            @Override
                            public void onValueChanged(float value) {
                                height = value;
                                mTextHeight.setText(String.valueOf(value));
                            }
                        });

                        new AlertDialog.Builder(MyInfoActivity.this)
                                .setView(linearLayout)
                                .setPositiveButton("确定", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mDatas.get(6).setContent_text(height + "cm");
                                        user.setHeight(height);
                                        user.update(MyInfoActivity.this);
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                                .create()
                                .show();
                        break;
                    case 7:
                        weight = user.getWeight();
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        View choose_weight = inflater.inflate(R.layout.choose_weight,linearLayout);
                        ScaleRulerView scaleRulerView_weight = (ScaleRulerView) choose_weight.findViewById(R.id.id_weight_scale);
                        scaleRulerView_weight.setmCurrentValue(weight);
                        final TextView mTextWeight = (TextView) choose_weight.findViewById(R.id.tv_user_weight_value);
                        mTextWeight.setText(String.valueOf(weight));
                        scaleRulerView_weight.setOnValueChangedListener(new ScaleRulerView.OnValueChangedListener() {
                            @Override
                            public void onValueChanged(float value) {
                                weight = value;
                                mTextWeight.setText(String.valueOf(value));
                            }
                        });

                        new AlertDialog.Builder(MyInfoActivity.this)
                                .setView(linearLayout)
                                .setPositiveButton("确定", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mDatas.get(7).setContent_text(weight + "kg");
                                        user.setWeight(weight);
                                        user.update(MyInfoActivity.this);
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                                .create()
                                .show();
                        break;
                    case 8:
                        ChangeInfoActivity.actionStart(MyInfoActivity.this,
                                R.layout.activity_change_info,
                                "个性签名",mDatas.get(8).getContent_text());
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        String text = null;
        final ProgressDialog progressDialog  = new ProgressDialog(MyInfoActivity.this);
        if(data != null){
            uri = data.getData();
            text = data.getStringExtra("et_content");
        }
        if(requestCode == BmobConstants.REQUESTCODE_TAKE_CAMERA
                &&resultCode == Activity.RESULT_OK){
            Intent intent = new Intent(MyInfoActivity.this,ChoosePhotoActivity.class);
            intent.setData(uri);
            startActivityForResult(intent,BmobConstants.REQUESTCODE_CHOOSE_PHOTO);
        }else if(requestCode == BmobConstants.REQUESTCODE_CHOOSE_PHOTO && resultCode == requestCode){
            if(uri != null){
                final BmobFile bmobFile = new BmobFile(new File(uri.getPath()));

                progressDialog.show();
                if(!CommonUtils.isNetworkAvailable(MyInfoActivity.this)){
                    progressDialog.dismiss();
                    showToast("网络连接不可用");
                    return ;
                }

                //上传修改后的头像
                bmobFile.upload(MyInfoActivity.this, new UploadFileListener() {
                    @Override
                    public void onSuccess() {
                        String url = bmobFile.getFileUrl(MyInfoActivity.this);
                        user.setAvatar(url);
                        user.update(MyInfoActivity.this);
                        mDatas.get(1).setContent_photoUrl(url);
                        adapter.notifyDataSetChanged();
                        mApplication.getSharedPreferencesUtil().setAvatarUrl(url);
                        progressDialog.dismiss();
                        showToast("头像修改成功");
                    }

                    @Override
                    public void onFailure(int i, String s) {

                        mApplication.getSharedPreferencesUtil().setAvatarUrl(null);
                        progressDialog.dismiss();
                        showToast("头像修改失败"+s);
                    }
                });
            }else {
                showToast("头像设置失败");
            }
        }else if(requestCode == BmobConstants.REQUESTCODE_CHANGE_INFO_NICK &&
                resultCode == BmobConstants.REQUESTCODE_CHANGE_INFO){

            if(!CommonUtils.isNetworkAvailable(MyInfoActivity.this)){
                showToast("网络连接不可用");
                return ;
            }

            user.setNick(text);
            user.update(MyInfoActivity.this);
            mDatas.get(2).setContent_text(text);
            adapter.notifyDataSetChanged();
        }else if(requestCode == BmobConstants.REQUESTCODE_CHANGE_INFO_SIGNATURE &&
                resultCode == BmobConstants.REQUESTCODE_CHANGE_INFO){

            if(!CommonUtils.isNetworkAvailable(MyInfoActivity.this)){
                showToast("网络连接不可用");
                return ;
            }

            user.setSignature(text);
            user.update(MyInfoActivity.this);
            mDatas.get(8).setContent_text(text);
            adapter.notifyDataSetChanged();
        }
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

        ItemListViewBean item7 = new ItemListViewBean(BmobConstants.LAYOUT_TWO_TV,"身高",user.getHeight() + "cm",null);
        mDatas.add(item7);

        ItemListViewBean item8 = new ItemListViewBean(BmobConstants.LAYOUT_TWO_TV,"体重",user.getWeight() + "kg",null);
        mDatas.add(item8);

        ItemListViewBean item9 = new ItemListViewBean(BmobConstants.LAYOUT_TWO_TV,"个性签名",user.getSignature(),null);
        mDatas.add(item9);

        ItemListViewBean item10 = new ItemListViewBean(BmobConstants.LAYOUT_ONE_TV,"","",null);
        mDatas.add(item10);
    }

}
