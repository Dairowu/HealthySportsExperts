package cn.xietong.healthysportsexperts.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import cn.xietong.healthysportsexperts.R;

/**
 * Created by mr.deng on 2016/5/14
 */
public class ClockView extends View {

    private Paint pathPaint;
    private Paint paddingPaint;
    private Paint centerPaint;
    private Bitmap backBitmap;
    private RectF rectF;

    private int mWidth;
    private int mHeight;

    private float radius;

    private float pathWidth;

    private float mMax;
    private float mProgress;

    //接收16进制的颜色
    private final int outerPathColor;
    //进度填充颜色
    private final int outerPaddingColor;
    //内部实心圆的颜色
    private final int contentCircleColor;
    //分界部分的细线的颜色
    private final int borderColor;

    // 梯度渐变的填充颜色
    private int[] arcColors = new int[] {0xFF599cd1,0xFF7d70b8,0xFFc8417b,0xFFe35a61,0xFFe98d42,0xFFdabf4a,0xFFdabf4a
            ,0xFF4defc8,0xFF47c8db,0xFF599cd1};

    private SweepGradient sweepGradient;

    // 指定了光源的方向和环境光强度来添加浮雕效果
    private EmbossMaskFilter emboss = null;
    // 设置光源的方向
    float[] direction = new float[] { 1, 1, 1 };
    // 设置环境光亮度
    float light = 0.4f;
    // 选择要应用的反射等级
    float specular = 6;
    // 向 mask应用一定级别的模糊
    float blur = 3.5f;
    // 指定了一个模糊的样式和半径来处理 Paint 的边缘
    private BlurMaskFilter mBlur = null;

    private Context mContext;

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        emboss = new EmbossMaskFilter(direction, light, specular, blur);
        mBlur = new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL);
        sweepGradient = new SweepGradient(getMeasuredWidth()/2,getMeasuredWidth()/2,arcColors,null);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBarCutout);

        //获得内圆半径和外环的画笔宽度
        pathWidth = a.getDimension(R.styleable.CircleProgressBarCutout_pathWidth,45);

        //获得进度的最大和最小值
        mMax = a.getFloat(R.styleable.CircleProgressBarCutout_max,1000);
        mProgress = a.getFloat(R.styleable.CircleProgressBarCutout_progress,0);

        //获得外环颜色，进度填充颜色和内圆颜色
        outerPathColor = a.getColor(R.styleable.CircleProgressBarCutout_outerPathColor,0);
        outerPaddingColor = a.getColor(R.styleable.CircleProgressBarCutout_outerPaddingColor,0);
        contentCircleColor = a.getColor(R.styleable.CircleProgressBarCutout_contentCircleColor,0);
        borderColor = a.getColor(R.styleable.CircleProgressBarCutout_borderColor,0);

        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setDither(true);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);
        pathPaint.setMaskFilter(emboss);

        paddingPaint  = new Paint();
        paddingPaint.setAntiAlias(true);
        paddingPaint.setStyle(Paint.Style.STROKE);
        paddingPaint.setDither(true);
        paddingPaint.setStrokeWidth(pathWidth);
        paddingPaint.setStrokeJoin(Paint.Join.ROUND);
        paddingPaint.setStrokeCap(Paint.Cap.ROUND);//线是圆的
        paddingPaint.setMaskFilter(mBlur);

        //要绘制填充弧形的外切矩形
        rectF = new RectF();

        centerPaint = new Paint();
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setColor(contentCircleColor);
        centerPaint.setMaskFilter(emboss);

        a.recycle();
    }

    public ClockView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public ClockView(Context context){
        this(context,null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        radius = mWidth / 2 -pathWidth;

        pathPaint.setColor(outerPathColor);
        pathPaint.setStrokeWidth(pathWidth);

        //绘制进度的轨道
        canvas.drawCircle(mWidth/2,mHeight/2,radius,pathPaint);

        pathPaint.setColor(borderColor);
        pathPaint.setStrokeWidth(0.5f);
        //绘制进度轨道的外边界
        canvas.drawCircle(mWidth/2,mHeight/2,radius + pathWidth / 2 + 0.5f,pathPaint);
        //绘制进度轨道的内边界
        canvas.drawCircle(mWidth/2,mHeight/2,radius - pathWidth / 2 - 0.5f,pathPaint);

        //绘制进度
        rectF.set(mWidth/2 - radius,mHeight/2 - radius,mWidth/2 + radius,mHeight/2 + radius);
        float scale = mMax > 0 ? (float) mProgress / (float) mMax : 0;
        if(outerPaddingColor != 0){
            paddingPaint.setColor(outerPaddingColor);
            canvas.drawArc(rectF,-90,scale*360,false,paddingPaint);
        }else{
            paddingPaint.setShader(sweepGradient);
            canvas.drawArc(rectF,-90,scale*360,false,paddingPaint);
        }

        canvas.drawCircle(mWidth/2,mHeight/2,radius -  pathWidth,centerPaint);

        canvas.drawCircle(mWidth/2,mHeight/2, radius - pathWidth + 0.5f, pathPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    public void setMax(float max) {
        if (max < 0) {
            max = 0;
        }

        if (max != mMax) {
            mMax = max;
            postInvalidate();
        }

        if(mProgress > mMax){
            mProgress = mMax;
        }
        refreshProgress(mProgress);
    }

    public synchronized float getProgress() {
        return mProgress;
    }

    public synchronized void setProgress(float progress) {
        if(progress == mProgress){
            return ;
        }
        this.mProgress = progress;

        //判断如果需要更新的进度与当前的进度相差超过了总进度的100分之一，则更新进度，减少invalidate的使用
        if(Math.abs(progress - mProgress) > mMax/100) {
            refreshProgress(mProgress);
        }
    }

    private synchronized void refreshProgress(float progress) {
            invalidate();
    }


}
