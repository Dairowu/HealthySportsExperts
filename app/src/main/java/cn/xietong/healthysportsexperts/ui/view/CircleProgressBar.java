package cn.xietong.healthysportsexperts.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/10/31.
 */
public class CircleProgressBar extends TextView {

    private int maxProgress = 100;//最大进度
    private int progress = 30;//当前进度
    private int progressStrokeWidth = 4;//进度条宽度
    private boolean isPaintText = false;
    Paint paint;//画笔
    RectF oval;//绘制的圆所在的矩形区域w


    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        oval = new RectF();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        //判断宽度和高度是否相等，如果不相等取最小的数值
        if(width!=height){
            int min = Math.min(width,height);
            width = min;
            height = min;
        }

        paint.setAntiAlias(true);//设置抗锯齿
        paint.setColor(Color.WHITE);//设置画笔颜色
        canvas.drawColor(Color.TRANSPARENT);//设置画布颜色
        paint.setStrokeWidth(progressStrokeWidth);//设置绘制的边界的宽度
        paint.setStyle(Paint.Style.STROKE);//设置类型为空心

        oval.left = progressStrokeWidth/2;//左上角X
        oval.top = progressStrokeWidth/2;//左上角Y
        oval.right = width - progressStrokeWidth/2;//右下角X
        oval.bottom = height - progressStrokeWidth/2;//右下角Y

        canvas.drawArc(oval,-90,360,false,paint);//绘制进度条背景
        paint.setColor(Color.rgb(0x03,0xa9,0xf4));
        canvas.drawArc(oval,-90,((float)progress/maxProgress)*360,false,paint);//绘制进度圆弧

        if(isPaintText){
            paint.setStrokeWidth(1);
            String text = progress + "%";
            int textHeight = height/4;
            paint.setTextSize(textHeight);
            int textWidth = (int) paint.measureText(text,0,text.length());
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(text,width/2-textWidth/2,height/2+textHeight/2,paint);
        }

    }

    public void setIsPaintText(boolean isPaintText) {
        this.isPaintText = isPaintText;
    }

    public void setMax(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    /**
     * 非UI线程中调用
     * @param progress
     */
    public void setProgressNotInUiThread(int progress){
        this.progress = progress;
        this.postInvalidate();
    }

    public void setProgressStrokeWidth(int progressStrokeWidth) {
        this.progressStrokeWidth = progressStrokeWidth;
    }
}
