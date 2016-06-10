package cn.xietong.healthysportsexperts.model;

/**日历的GridView中每一个Item所具有的属性和内容
 * Created by Administrator on 2016/6/4
 */
public class Cell {

    private String which_day;

    private float progress;

    private boolean is_today;

    private boolean isCurrentMonth;

    /**
     *
     * @param which_day 一个月中的哪一天
     * @param progress 当天完成的步数
     * @param is_today 是否为今天
     * @param isCurrentMonth 是否为当前月份，每个月的前几天空出来的不为当前月
     */
    public Cell(String which_day, float progress, boolean is_today, boolean isCurrentMonth) {
        this.which_day = which_day;
        this.progress = progress;
        this.is_today = is_today;
        this.isCurrentMonth = isCurrentMonth;
    }

    public String getWhich_day() {
        return which_day;
    }

    public void setWhich_day(String which_day) {
        this.which_day = which_day;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public boolean is_today() {
        return is_today;
    }

    public void setIs_today(boolean is_today) {
        this.is_today = is_today;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public void setCurrentMonth(boolean currentMonth) {
        isCurrentMonth = currentMonth;
    }
}
