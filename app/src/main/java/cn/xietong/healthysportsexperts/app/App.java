package cn.xietong.healthysportsexperts.app;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.xietong.healthysportsexperts.model.DatabaseHelper;
import cn.xietong.healthysportsexperts.utils.CollectionUtils;
import cn.xietong.healthysportsexperts.utils.SharePreferenceUtil;
import cn.xietong.healthysportsexperts.utils.StepListener;

/**
 * Created by Administrator on 2015/10/27.
 */
public class App extends Application {

    private static App instance;
    private static SharePreferenceUtil mSpUtil;
    private static  DatabaseHelper dbHelper;
    private static StepListener stepListener;
    //private static ImageLoader imageLoader;
    public static final String DATABASE_NAME = "healthysportsexperts_db";
    public static final String PREFERENCE_NAME = "_sharedinfo";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        dbHelper = new DatabaseHelper(this,DATABASE_NAME);
        stepListener = new StepListener();
        init();//Magic,2016.4.8
        //imageLoader = ImageLoader.build(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }

    public static App getInstance() {
        return instance;
    }

    /**
     * 获取SharedPreferences实例
     * @return
     */
    public synchronized SharePreferenceUtil getSharedPreferencesUtil(){
        if(mSpUtil==null) {
            String currentId = BmobUserManager.getInstance(
                    getApplicationContext()).getCurrentUserObjectId();
            String shared_name = currentId+PREFERENCE_NAME;
            mSpUtil = new SharePreferenceUtil(this,shared_name);
        }
        return mSpUtil;
    }

    public static DatabaseHelper getDBHelper(){
        return dbHelper;
    }

    public static StepListener getStepListener(){return stepListener;}

   // public static ImageLoader getImageLoader(){return imageLoader;}

    private Map<String,BmobChatUser> contactList = new HashMap<String,BmobChatUser>();

    public Map<String, BmobChatUser> getContactList() {
        if(contactList == null || contactList.size()==0)return null;
        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, BmobChatUser> contactList) {
        if(this.contactList!=null){
            this.contactList.clear();
        }
        this.contactList = contactList;
    }


    /**
     * 退出应用
     */
    public void logout() {

        BmobUserManager.getInstance(getApplicationContext()).logout();
        setContactList(null);

    }

    /**
     * Magic,2016.4.8
     */
    private void init(){
        BmobChat.DEBUG_MODE = true;
        if (BmobUserManager.getInstance(getApplicationContext())
                .getCurrentUser() != null) {
            // 获取本地好友user list到内存,方便以后获取好友list
            contactList = CollectionUtils.list2map(BmobDB.create(
                    getApplicationContext()).getContactList());
        }
    }
}
