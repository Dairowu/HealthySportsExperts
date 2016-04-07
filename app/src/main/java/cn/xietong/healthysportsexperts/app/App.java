package cn.xietong.healthysportsexperts.app;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.xietong.healthysportsexperts.model.DatabaseHelper;
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
        return contactList;
    }

    public void setContactList(Map<String, BmobChatUser> contactList) {
        if(contactList!=null){
            contactList.clear();
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
}
