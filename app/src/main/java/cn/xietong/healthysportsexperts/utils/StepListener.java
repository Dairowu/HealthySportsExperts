package cn.xietong.healthysportsexperts.utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.Calendar;

/**计步用的传感器监听器
 * Created by Administrator on 2015/10/22.
 */
public class StepListener implements SensorEventListener {

    public static  final int ALL_DAY_TIME = 1440;
    //时间处理类
    private Calendar cal;
    //当前步数
    private int count = 0;
    //对步数进行部分计数
    private int tempStep = 0;

    //存放三轴数据
    float[] oriValues = new float[3];
    final int valueNum = 4;
    //用于存放计算阈值的波峰波谷差值
    float[] tempValue = new float[valueNum];
    //是否为第一次开始或者中断后开始计数
    boolean isStart = true;
    int tempCount = 0;
    //是否上升的标志位
    boolean isDirectionUp = false;
    //持续上升次数
    int continueUpCount = 0;
    //上一点的持续上升的次数，为了记录波峰的上升次数
    int continueUpFormerCount = 0;
    //上一点的状态，上升还是下降
    boolean lastStatus = false;
    //波峰值
    float peakOfWave = 0;
    //波谷值
    float valleyOfWave = 0;
    //此次波峰的时间
    long timeOfThisPeak = 0;
    //上次波峰的时间
    long timeOfLastPeak = 0;
    //当前的时间
    long timeOfNow = 0;
    //当前传感器的值
    float gravityNew = 0;
    //上次传感器的值
    float gravityOld = 0;
    //动态阈值需要动态的数据，这个值用于这些动态数据的阈值
    final float initialValue = (float) 1.3;
    //初始阈值
    float ThreadValue = (float) 2.0;

    // 用于计算步子时间的差值
    float[] tempTimeStep = new float[valueNum];
    int tempTime = 0;
    int tempTimeNow = 0;

    // 刚开始时只有达到了十步才计算
    public static final int continueStep = 10;

    /** 注册了G-Sensor后一直会调用这个函数
    *对三轴数据进行平方和开根号的处理
    *调用detectorNewStep检测步子
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        if(Math.abs(oriValues[0] - event.values[0]) > 0.3 && Math.abs(oriValues[1] - event.values[1]) > 0.5 && Math.abs(oriValues[2] - event.values[2]) > 0.5) {
            oriValues[0] = event.values[0];
            oriValues[1] = event.values[1];
            oriValues[2] = event.values[2];
            gravityNew = (float) Math.sqrt(oriValues[0] * oriValues[0]
                    + oriValues[1] * oriValues[1] + oriValues[2] * oriValues[2]);
            detectorNewStep(gravityNew);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*
   * 检测步子，并开始计步
   * 1.传入sersor中的数据
   * 2.如果检测到了波峰，并且符合时间差以及阈值的条件，则判定为1步
   * 3.符合时间差条件，波峰波谷差值大于initialValue，则将该差值纳入阈值的计算中
   * */
    public void detectorNewStep(float values) {
        if (gravityOld == 0) {
            gravityOld = values;
        } else {
            if (DetectorPeak(values, gravityOld)) {
                timeOfLastPeak = timeOfThisPeak;
                timeOfNow = System.currentTimeMillis();
                if (timeOfNow - timeOfLastPeak >= correctTime(timeOfNow - timeOfLastPeak)
                        && (peakOfWave - valleyOfWave >= ThreadValue)) {
                    timeOfThisPeak = timeOfNow;
                    /*
                     * 一般在通知更新界面之前，增加下面处理，为了处理无效运动：
                     * 1.连续记录10才开始计步
                     * 2.例如记录的9步用户停住超过3秒，则前面的记录失效，下次从头开始
                     * 3.连续记录了9步用户还在运动据，之前的数据才有效
                     * */

                    onStep();

                }
                if (timeOfNow - timeOfLastPeak >= correctTime(timeOfNow - timeOfLastPeak)
                        && (peakOfWave - valleyOfWave >= initialValue)) {
                    timeOfThisPeak = timeOfNow;
                    ThreadValue = Peak_Valley_Thread(peakOfWave - valleyOfWave);
                }
            }
        }
        gravityOld = values;
    }

    private void onStep() {
        if(timeOfNow - timeOfLastPeak > 60000){
            tempStep = 0;
        }
        if(tempStep < continueStep){
            tempStep++;
        }else if(tempStep == continueStep){
            count += tempStep;
            tempStep++;
        }else{
            count += 1;
        }
    }


    /**
     * 矫正时间差，用于矫正多个步数之间的时间差值 通过计算多个时间差值的平均数 使得当一次采集时间大于前一次时间的2/3的时候，才算正常行走
     *
     * @param times
     * @return
     */
    private float correctTime(float times) {

        // 第一次步子时间差设置为250毫秒
        if (tempTime == 0) {
            tempTimeStep[tempTime++] = times;
            tempTimeNow++;
            return (times + 250) / 3;
        } else if (tempTime < valueNum - 1) {
            tempTimeNow++;
            tempTimeStep[tempTime++] = times;
            return averageValue(tempTimeStep, tempTime);
        } else {
            tempTimeStep[tempTimeNow] = times;
            if (tempTimeNow == valueNum - 1) {
                tempTimeNow = 0;
            } else {
                tempTimeNow++;
            }
            return averageValue(tempTimeStep, tempTime);
        }
    }

    /*
     * 检测波峰
     * 以下四个条件判断为波峰：
     * 1.目前点为下降的趋势：isDirectionUp为false
     * 2.之前的点为上升的趋势：lastStatus为true
     * 3.到波峰为止，持续上升大于等于2次
     * 4.波峰值大于20
     * 记录波谷值
     * 1.观察波形图，可以发现在出现步子的地方，波谷的下一个就是波峰，有比较明显的特征以及差值
     * 2.所以要记录每次的波谷值，为了和下次的波峰做对比
     * */
    public boolean DetectorPeak(float newValue, float oldValue) {
        lastStatus = isDirectionUp;
        if (newValue >= oldValue) {
            isDirectionUp = true;
            continueUpCount++;
        } else {
            continueUpFormerCount = continueUpCount;
            continueUpCount = 0;
            isDirectionUp = false;
        }

        if (!isDirectionUp && lastStatus
                && (continueUpFormerCount >= 2 || oldValue >= 20)) {
            peakOfWave = oldValue;
            return true;
        } else if (!lastStatus && isDirectionUp) {
            valleyOfWave = oldValue;
            return false;
        } else {
            return false;
        }
    }

    /*
     * 阈值的计算
     * 1.通过波峰波谷的差值计算阈值
     * 2.记录4个值，存入tempValue[]数组中
     * 3.在将数组传入函数averageValue中计算阈值
     * */
    public float Peak_Valley_Thread(float value) {
        float tempThread = ThreadValue;
        if (tempCount < valueNum) {
            tempValue[tempCount] = value;
            tempCount++;
        } else {
            tempThread = averageValue(tempValue, valueNum);
            for (int i = 1; i < valueNum; i++) {
                tempValue[i - 1] = tempValue[i];
            }
            tempValue[valueNum - 1] = value;
        }
        return tempThread;

    }

    /*
     * 梯度化阈值
     * 1.计算数组的均值
     * 2.通过均值将阈值梯度化在一个范围里
     * */
    public float averageValue(float value[], int n) {
        float ave = 0;
        for (int i = 0; i < n; i++) {
            ave += value[i];
        }
        ave = ave / valueNum;
        if (ave >= 8)
            ave = (float) 4.3;
        else if (ave >= 7 && ave < 8)
            ave = (float) 3.3;
        else if (ave >= 4 && ave < 7)
            ave = (float) 2.3;
        else if (ave >= 3 && ave < 4)
            ave = (float) 2.0;
        else {
            ave = (float) 1.3;
        }
        return ave;
    }

    public int getCount() {
        cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int minute_of_day = hour*60+minute;
        if(minute_of_day!=ALL_DAY_TIME) {
            return count;
        }else {
            setCount(0);
        }
        return 0;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
