package cn.xietong.healthysportsexperts.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import cn.xietong.healthysportsexperts.R;

/**算法思想：通过mMove值的变化计算出mCurrentValue，然后根据mCurrentValue计算两侧的刻度线。
 * Created by mr.deng on 2016/5/19
 */
public class ScaleRulerView extends View {

    private int mWidth,mHeight;

    private Paint mNormalLinePaint;
    private Paint mSelectLinePaint;

    //当前尺子的最大值，最小值和当前值
    private float mMaxValue;
    private float mMinValue;
    private float mCurrentValue;

    //选中刻度的颜色和正常情况下的颜色
    private int mSelectColor;
    private int mNormalLineColor;

    private float mNormalLineWidth;
    private float mSelectLineWidth;
    private float mLineSpace;

    private int mMinVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private int mMove = 0;//滑动的距离,计算需要滑动的刻度数
    private int mLastX = 0;
    private int mModeType = 5;//精度值

    private OnValueChangedListener mListener;

    public ScaleRulerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScaleRulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScaleRulerView);
        initDefineAttribute(a);

        mScroller = new Scroller(context);
        //获取可测量到的最小速度值
        mMinVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();

        mNormalLinePaint = new Paint();
        mNormalLinePaint.setStrokeWidth(mNormalLineWidth);
        mNormalLinePaint.setColor(mNormalLineColor);

        mSelectLinePaint = new Paint();
        mSelectLinePaint.setStrokeWidth(mSelectLineWidth);
        mSelectLinePaint.setColor(mSelectColor);
    }

    public interface OnValueChangedListener{
        void onValueChanged(float value);
    }

    public void setOnValueChangedListener(OnValueChangedListener listener){
        mListener = listener;
    }

    private void notifyValueChange(){
        if(null != mListener){
            mListener.onValueChanged(mCurrentValue);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScaleLines(canvas);
        drawMiddleLine(canvas);
    }

    /**
     * 绘制刻度线
     * @param canvas
     */
    private void drawScaleLines(Canvas canvas) {
        canvas.save();
        int width = mWidth;
        //绘制的总长度
        int drawWidth = 0;
        float xPosition;

        for(int spaceNum = 0;drawWidth < 4*width;spaceNum++){

            //要绘制直线的横坐标位置
            xPosition = (width / 2 - mMove) + spaceNum * mLineSpace;

            if ((xPosition + getPaddingRight()) < mWidth && (mCurrentValue + spaceNum) <= mMaxValue){
                if((mCurrentValue + spaceNum) % mModeType == 0){
                    canvas.drawLine(xPosition,getHeight(),xPosition,getHeight()/4,mNormalLinePaint);
                }else{
                    canvas.drawLine(xPosition,getHeight(),xPosition,getHeight()/2,mNormalLinePaint);
                }
            }

            xPosition = (width / 2 - mMove) - spaceNum * mLineSpace;

            if ((xPosition + getPaddingLeft()) < mWidth && (mCurrentValue - spaceNum) >= mMinValue){
                if((mCurrentValue - spaceNum) % mModeType == 0){
                    canvas.drawLine(xPosition,getHeight(),xPosition,getHeight()/4,mNormalLinePaint);
                }else{
                    canvas.drawLine(xPosition,getHeight(),xPosition,getHeight()/2,mNormalLinePaint);
                }
            }
            drawWidth += 2*mLineSpace;
        }
        canvas.restore();
    }

    /**
     * 绘制中间的提示线
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas) {
        canvas.save();
        canvas.drawLine(mWidth / 2,0,mWidth / 2,mHeight,mSelectLinePaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int nowX = (int) event.getX();

        if(mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action){
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);
                mLastX = nowX;
                mMove = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                mMove += (mLastX - nowX);
                //通过判断避免当当前值已经等于最大值或者最小值时也能滑动
                if(mCurrentValue != mMaxValue && mCurrentValue != mMinValue) {
                    changeMoveAndValue();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                countMoveEnd();
                countVelocityTracker();
                return false;
            default:
                break;
        }
        mLastX = nowX;
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            if(mScroller.getCurrX() == mScroller.getFinalX()){
                countMoveEnd();
            }else{
                int nowX = mScroller.getCurrX();
                mMove += (mLastX - nowX);
                changeMoveAndValue();
                mLastX = nowX;
            }
        }
    }

    /**
     * 通过速度追踪计算惯性滑动
     */
    private void countVelocityTracker() {

        mVelocityTracker.computeCurrentVelocity(1000);
        int xVelocity = (int) mVelocityTracker.getXVelocity();
        if(Math.abs(xVelocity) > mMinVelocity){
            mScroller.fling(0,0,xVelocity,0,Integer.MIN_VALUE,Integer.MAX_VALUE,0,0);
        }
    }

    /**
     * 计算一系列事件结束时的刻度值
     */
    private void countMoveEnd() {

        int roundValue = Math.round(mMove / mLineSpace);
        mCurrentValue += roundValue;
        mCurrentValue = mCurrentValue < mMinValue?mMinValue:mCurrentValue;
        mCurrentValue = mCurrentValue > mMaxValue?mMaxValue:mCurrentValue;

        mLastX = 0;
        mMove = 0;

        notifyValueChange();
        postInvalidate();
    }

    public void setmCurrentValue(float mCurrentValue) {
        this.mCurrentValue = mCurrentValue;
        notifyValueChange();
        postInvalidate();
    }

    /**
     * 计算滑动过程中滑动距离和刻度值的改变
     */
    private void changeMoveAndValue() {

        //通过滑动距离计算改变额多少刻度
        int tValue = (int) (mMove / mLineSpace);
        if(tValue != 0){
            mCurrentValue += tValue;
            //计算过刻度的滑动距离应该减掉
            mMove -= tValue * mLineSpace;
            if(mCurrentValue < mMinValue || mCurrentValue > mMaxValue){
                mCurrentValue = mCurrentValue > mMaxValue?mMaxValue:mMinValue;
                mMove = 0;
                mScroller.forceFinished(true);
            }
        }
        notifyValueChange();
        postInvalidate();
    }

    private void initDefineAttribute(TypedArray a) {
        mSelectColor = a.getColor(R.styleable.ScaleRulerView_selectLineColor,0xffffff);
        mNormalLineColor = a.getColor(R.styleable.ScaleRulerView_normalLineColor,0xeeeeee);
        mMaxValue = a.getFloat(R.styleable.ScaleRulerView_maxValue,100);
        mMinValue = a.getFloat(R.styleable.ScaleRulerView_minValue,0);
        mCurrentValue = a.getFloat(R.styleable.ScaleRulerView_currentValue,50);
        mNormalLineWidth = a.getDimension(R.styleable.ScaleRulerView_normalLineWidth,4);
        mSelectLineWidth = a.getDimension(R.styleable.ScaleRulerView_selectLineWidth,8);
        mLineSpace = 3*mSelectLineWidth;
    }
}
