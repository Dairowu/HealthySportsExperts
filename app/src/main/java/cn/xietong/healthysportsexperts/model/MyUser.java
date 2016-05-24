package cn.xietong.healthysportsexperts.model;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.Objects;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by mr.deng on 2015/11/9
 */
public class MyUser extends BmobChatUser {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 显示数据拼音的首字母，实现微信那种联系人按字母先后顺序排序的效果
     */
    private String sortLetters;


    private String signature;

    /**
     * 性别-true-男
     */
    private boolean sex;

    private float height;
    private float weight;

    /**
     * 头像
     */
    private BmobFile head_photo;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean getSex() {
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object otherObject) {

        if(this == otherObject) return true;

        if(otherObject == null) return false;

        if(getClass() != otherObject.getClass()) return false;

        MyUser other = (MyUser) otherObject;

        return Objects.equals(getUsername(),other.getUsername()) &&
                Objects.equals(getMobilePhoneNumber(),other.getMobilePhoneNumber()) &&
                sex == other.sex && Objects.equals(signature,other.signature);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(getUsername(),getMobilePhoneNumber(),sex,signature);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[name=" + getUsername() + ",mobilePhoneNumber=" + getMobilePhoneNumber()
                +",sex=" + sex + ",signature=" + signature + ",avatar="+ getAvatar()+"]";
    }

}
