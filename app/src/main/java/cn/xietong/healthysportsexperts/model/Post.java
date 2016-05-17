package cn.xietong.healthysportsexperts.model;


import cn.bmob.v3.BmobObject;

/*
 *用于存放帖子数据的类。
 */
public class Post extends BmobObject {
    private String userName;
    private String mainArticle;
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setMainArticle(String mainArticle) {
        this.mainArticle = mainArticle;
    }

    public Post() {
        super();
    }

    public String getMainArticle() {

        return mainArticle;
    }

    public Post(String userName, String mainArticle) {
        this.userName = userName;
        this.mainArticle = mainArticle;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {

        return userName;
    }
}
