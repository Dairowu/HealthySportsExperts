package cn.xietong.healthysportsexperts.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by gundam on 2015/11/12.
 * 用于存放评论数据的类，id为对应帖子。
 */

public class Comment extends BmobObject {
    private String username;
    private int index;
    private String comment;
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {

        return id;
    }

    public Comment(String username, String comment, String id) {
        this.username = username;
        this.comment = comment;
        this.id = id;
    }

    public Comment(String username, String comment) {
        this.comment = comment;
        this.index = index;
        this.username = username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public int getIndex() {
        return index;
    }

    public String getComment() {
        return comment;
    }


}
