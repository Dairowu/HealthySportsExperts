package cn.xietong.healthysportsexperts.app;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.sharesdk.framework.ShareSDK;
import cn.xietong.healthysportsexperts.model.DatabaseHelper;
import cn.xietong.healthysportsexperts.utils.CollectionUtils;
import cn.xietong.healthysportsexperts.utils.SharePreferenceUtil;
import cn.xietong.healthysportsexperts.utils.StepListener;

/**application子类，不要在里面放太多加载项不然启动会很慢
 * Created by mr.deng on 2015/10/27.
 */
public class App extends Application {

    private static App instance;
    private static SharePreferenceUtil mSpUtil;
    private static  DatabaseHelper dbHelper;
    private static StepListener stepListener;
    public static final String DATABASE_NAME = "healthysportsexperts_db";
    public static final String PREFERENCE_NAME = "_sharedinfo";

    private BmobUserManager mUserManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // 初始化ShareSDK(2017.1.8)
        SDKInitializer.initialize(getApplicationContext());
        dbHelper = new DatabaseHelper(this,DATABASE_NAME);
        stepListener = new StepListener();
        mUserManager = BmobUserManager.getInstance(this);
        init();//Magic,2016.4.8
        initImageLoader();

        ShareSDK.initSDK(getApplicationContext());
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

    private Map<String,BmobChatUser> contactList = new HashMap<String,BmobChatUser>();

    public synchronized  BmobUserManager getUserManager() {
        return mUserManager;
    }

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

    /**
     * 初始化ImageLoader
     */
    private  void initImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(this,
                "healthysportsexperts/Cache");// 获取到缓存的目录地址
        FileNameGenerator fileName = new FileNameGenerator() {
            @Override
            public String generate(String imageUri) {
                return null;
            }
        };

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                // 线程池内加载的数量
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiskCache(cacheDir))// 自定义缓存路径
                // .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);
    }

}
