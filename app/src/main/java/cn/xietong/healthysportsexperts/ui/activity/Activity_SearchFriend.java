package cn.xietong.healthysportsexperts.ui.activity;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.listener.FindListener;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.adapter.AddFriendAdapter;
import cn.xietong.healthysportsexperts.model.MyUser;

/**
 * Created by 林思旭 on 2016/4/9.
 */
public class Activity_SearchFriend extends BaseActivity implements View.OnClickListener {
    private String TAG = "SeachNewFriendActivity";
    private EditText et_find_name;
    private Button btn_search_friends;
    private ListView mListView;
    private AddFriendAdapter adapter;
    private List<BmobChatUser> users = new ArrayList<BmobChatUser>();
    private int curPage = 0;
    private ProgressDialog progress ;
    private MyUser User_name;
    BmobUserManager userManager;
    BmobChatManager manager;
    private String searchName ="";
    private String search_user_name,search_user_ID;
    @Override
    public int getLayoutId() {
        return R.layout.activity_searchfriend;
    }

    @Override
    public void initViews() {
        userManager = BmobUserManager.getInstance(this);
        manager = BmobChatManager.getInstance(this);
        et_find_name = (EditText)findViewById(R.id.edit_search_friends);
        btn_search_friends = (Button)findViewById(R.id.btn_search_friends);
        mListView = (ListView)findViewById(R.id.listView_send_friend_request);
        btn_search_friends.setOnClickListener(this);
        Log.i(TAG, "hello");
    }

    @Override
    public void setupViews() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search_friends://����
                users.clear();
                searchName = et_find_name.getText().toString();
                if(searchName!=null && !searchName.equals("")){
                    SearchFriend(searchName);
                }else{
                    Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
    private void initXListView(){
        adapter = new AddFriendAdapter(this, users);
        mListView.setAdapter(adapter);
        Log.i(TAG, "添加");
    }
    private void SearchFriend(String friendName){
        User_name = new MyUser();
        Log.i(TAG, "other");
//		userManager.queryUserByName("bb", new FindListener<User>(){}
        userManager.queryUserByName(friendName , new FindListener<BmobChatUser>() {

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                Log.i(TAG, arg1+"失败");
            }

            @Override
            public void onSuccess(List<BmobChatUser> arg0) {
                // TODO Auto-generated method stub
                Log.i(TAG, arg0.get(0)+"123");
                search_user_ID = arg0.get(0).getObjectId();//(��ָ��) 11.12
                Log.i(TAG, search_user_ID);
                search_user_name =arg0.get(0).getUsername();
                User_name.setUsername(search_user_name);
                User_name.setObjectId(search_user_ID);
                users.add(User_name);
                Log.i(TAG, users+"");
                initXListView();
            }
        });
    }
}
