package cn.xietong.healthysportsexperts.model;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2015/11/9.
 */
public class MyUser extends BmobUser{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 显示数据拼音的首字母，实现微信那种联系人按字母先后顺序排序的效果
     */
    private String sortLetters;

    /**
     * 性别-true-男
     */
    private boolean sex;

    /**
     * 头像
     */
    private BmobFile head_photo;

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public BmobFile getHead_photo() {
        return head_photo;
    }

    public void setHead_photo(BmobFile head_photo) {
        this.head_photo = head_photo;
    }

}
