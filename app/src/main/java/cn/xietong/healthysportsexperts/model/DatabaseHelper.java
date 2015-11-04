package cn.xietong.healthysportsexperts.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**数据库助手类，用于表的创建和SQLiteDatabase对象的获取
 * Created by Administrator on 2015/11/3.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    //初始版本常量
    private static final int VERSION = 1;

    //建表语句，为计步数和日期建立一张表，主键为日期
    public static final String CREATE_STEPDATA = "create table step(datetime verchar(30),count integer)";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, int version){
        this(context,name,null,version);
    }

    public DatabaseHelper(Context context,String name){
        this(context,name, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_STEPDATA);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
