package cn.xietong.healthysportsexperts.model;

/**用户操作后保留的一些数据
 * Created by Administrator on 2015/11/3.
 */
public class UserInfo {

    private String Datatime;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDatatime() {
        return Datatime;
    }

    public void setDatatime(String datatime) {
        Datatime = datatime;
    }
}
