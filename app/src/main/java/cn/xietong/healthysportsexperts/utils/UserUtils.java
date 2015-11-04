package cn.xietong.healthysportsexperts.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import cn.xietong.healthysportsexperts.app.App;
import cn.xietong.healthysportsexperts.model.DatabaseHelper;
import cn.xietong.healthysportsexperts.model.UserInfo;

/**对用户数据进行操作的类
 * Created by Administrator on 2015/11/3.
 */
public class UserUtils{

    //保存用户从开始使用程序到现在的每一天的记录
    private static ArrayList<UserInfo> arrayList = new ArrayList<UserInfo>();

    private static DatabaseHelper dbHelper = App.getInstance().getDBHelper();

    public void put(UserInfo user){
        arrayList.add(user);
    }

    public static ArrayList<UserInfo> getList(){
        return arrayList;
    }

    /**从数据库中加载数据
     *
     */
    public static void loadFromDatabase(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("step",new String[]{"datetime","count"},null,null,null,null,"datetime desc");

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);//将游标移动一个固定的行
            String datetime = cursor.getString(0);
            int count = cursor.getInt(1);

            UserInfo userInfo = new UserInfo();
            userInfo.setDatatime(datetime);
            userInfo.setCount(count);
            arrayList.add(userInfo);
        }
    }

}
