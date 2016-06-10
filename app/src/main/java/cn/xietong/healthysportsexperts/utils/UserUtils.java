package cn.xietong.healthysportsexperts.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import cn.xietong.healthysportsexperts.app.App;
import cn.xietong.healthysportsexperts.model.DatabaseHelper;
import cn.xietong.healthysportsexperts.model.UserInfo;

/**对用户数据进行操作的类
 * Created by Administrator on 2015/11/3.
 */
public class UserUtils{

    //保存用户从开始使用程序到现在的每一天的记录
    private static ArrayList<UserInfo> arrayList;
    public static TreeMap<String ,Integer> map;
    //保存用户联系人相关的信息
    private static ArrayList<HashMap<String,Object>>  contactList = new ArrayList<HashMap<String,Object>>();

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
        Cursor cursor_step = db.query("step",new String[]{"datetime","count"},null,null,null,null,"datetime desc");
//        Cursor cursor_contact = db.query("contact",new String[]{"id","head_photo","name","number","sex","sign"},null,null,null,null,
//                "order by name,id");
        arrayList = new ArrayList<UserInfo>();
        map  = new TreeMap<>();
        for (int i = 0; i < cursor_step.getCount(); i++) {
            cursor_step.moveToPosition(i);//将游标移动一个固定的行
            String datetime = cursor_step.getString(0);
            int count = cursor_step.getInt(1);
            UserInfo userInfo = new UserInfo();
            userInfo.setDatetime(datetime);
            userInfo.setCount(count);
            arrayList.add(userInfo);
            Log.i("info","datetime = " + datetime + " count = " + count);
            map.put(datetime,count);
        }

//        for (int i = 0; i < cursor_contact.getCount(); i++) {
//            cursor_contact.moveToPosition(i);//将游标移动一个固定的行
//            HashMap<String ,Object> map = new HashMap<String, Object>();
//            byte[] in  = cursor_contact.getBlob(cursor_contact.getColumnIndex("head_photo"));
//            Bitmap btm = BitmapFactory.decodeByteArray(in,0,in.length);
//            map.put("head_photo",btm);
//            String name = cursor_contact.getString(cursor_contact.getColumnIndex("name"));
//            map.put("name",name);
//            String number = cursor_contact.getString(cursor_contact.getColumnIndex("number"));
//            map.put("number",number);
//            String sex = cursor_contact.getString(cursor_contact.getColumnIndex("sex"));
//            map.put("sex",sex);
//            String sign = cursor_contact.getString(cursor_contact.getColumnIndex("sign"));
//            map.put("sign",sign);
//
//            contactList.add(map);
//        }

        cursor_step.close();
    }

}
