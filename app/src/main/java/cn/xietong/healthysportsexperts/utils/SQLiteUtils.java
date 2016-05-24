package cn.xietong.healthysportsexperts.utils;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import cn.xietong.healthysportsexperts.model.DatabaseHelper;
import cn.xietong.healthysportsexperts.model.UserInfo;

/**对数据库操作的工具类
 * Created by Administrator on 2015/11/3.
 */
public class SQLiteUtils {

    /**
     * 向数据库的某张表中插入数据
     * @param dbHelper DatabaseHelper对象
     * @param user UserInfo对象
     * @param name 表名
     */
    public static void insert(DatabaseHelper dbHelper,UserInfo user,String name){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(name.equals("step")){
            Log.i("info",user.getCount()+"存储"+user.getDatetime()+"");
            values.put("datetime",user.getDatetime());
            values.put("count",user.getCount());
            db.insert("step",null,values);
            values.clear();
            db.close();

        }

    }

    /**
     * 数据库的更新操作
     * @param dbHelper DatabaseHelper对象
     * @param user  UserInfo对象
     * @param name  表名
     */
    public static void update(DatabaseHelper dbHelper,UserInfo user,String name){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(name.equals("step")){
            values.put("datetime",user.getDatetime());
            values.put("count",user.getCount());
            Log.i("info",user.getCount()+"存储"+user.getDatetime()+"");
            int i = db.update(name,values,"datetime=?",new String[]{user.getDatetime()});
            values.clear();
            if(i == 0){
                insert(dbHelper,user,name);
            }
            db.close();
        }

    }

}
