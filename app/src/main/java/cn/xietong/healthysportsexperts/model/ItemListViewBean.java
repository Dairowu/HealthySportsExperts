package cn.xietong.healthysportsexperts.model;

/**该类主要为ListView的每个Item的Bean类
 * Created by mr.deng on 2016/4/18.
 */
public class ItemListViewBean {

    private  int type;
    private  String title;
    private  String content_text;
    private  String content_photoUrl;
    private  int content_photoRes;

    public ItemListViewBean(int type, String title, String content_text, int content_photoRes) {
        this.type = type;
        this.title = title;
        this.content_text = content_text;
        this.content_photoRes = content_photoRes;
        this.content_photoUrl = null;
    }

    public ItemListViewBean(int type, String title, String content_text, String content_photoUrl) {
        this.type = type;
        this.title = title;
        this.content_text = content_text;
        this.content_photoUrl = content_photoUrl;
        this.content_photoRes = 0;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getContent_text() {
        return content_text;
    }

    public String getContent_photoUrl() {
        return content_photoUrl;
    }

    public int getContent_photoRes() {
        return content_photoRes;
    }

    public void setContent_text(String content_text) {
        this.content_text = content_text;
    }

    public void setContent_photoUrl(String content_photoUrl) {
        this.content_photoUrl = content_photoUrl;
    }
}
