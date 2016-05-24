package cn.xietong.healthysportsexperts.model;

/**用户操作后保留的一些数据
 * Created by Administrator on 2015/11/3.
 */
public class UserInfo {

    private String Datetime;// 日期 格式： yyyy-MM-dd
    private int count;//走的步数
    private float distance; // 跑步距离 单位：m
    private float caloria; // 消耗 单位：千卡

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDatetime() {
        return Datetime;
    }

    public void setDatetime(String datatime) {
        Datetime = datatime;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getCaloria() {
        return caloria;
    }

    public void setCaloria(float caloria) {
        this.caloria = caloria;
    }
}
