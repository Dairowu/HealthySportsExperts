package cn.xietong.healthysportsexperts.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.xietong.healthysportsexperts.R;

/**
 * Created by mr.deng on 2015/11/10
 */
public class TopBar extends RelativeLayout{

    private final String mLeftText;
    private final int mLeftTextColor;
    private final Drawable mLeftBackground;

    private final int mTitletextColor;
    private final float mTitletextSize;

    private final int mRightTextColor;
    private final String mRightText;
    private final Drawable mRightBackground;

    private LayoutParams mTitleParams;
    private LayoutParams mLeftParams;
    private LayoutParams mRightParams;


    private TextView mTitleView;
    private ImageButton mRightButton;
    private ImageButton mLeftButton;

    private topbarClickListener mListener;

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar);

        mLeftTextColor = ta.getColor(R.styleable.TopBar_topLeftTextColor,0);
        mLeftText = ta.getString(R.styleable.TopBar_topLeftText);
        mLeftBackground = ta.getDrawable(R.styleable.TopBar_topLeftBackground);


        mTitletextSize = ta.getDimension(R.styleable.TopBar_topTitleTextSize, 10);
        mTitletextColor = ta.getColor(R.styleable.TopBar_topTitleTextColor, 0);

        mRightTextColor = ta.getColor(R.styleable.TopBar_topRightTextColor,0);
        mRightText = ta.getString(R.styleable.TopBar_topRightText);
        mRightBackground = ta.getDrawable(R.styleable.TopBar_topRightBackground);

        ta.recycle();

        mLeftButton = new ImageButton(context);
        mTitleView = new TextView(context);
        mRightButton = new ImageButton(context);

        mRightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.rightClick();
            }
        });
        mLeftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.leftClick();
            }
        });


    }

    /**
     * 设置显示标题
     * @param title
     */
    public void setDefaultTitle(String title){
        if(title!=null&&!title.equals("")){
            mTitleView.setText(title);
            mTitleView.setTextSize(18);
            mTitleView.setTextColor(mTitletextColor);
            mTitleView.setGravity(Gravity.CENTER);
            mTitleParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
            mTitleParams.addRule(RelativeLayout.CENTER_IN_PARENT,TRUE);
            addView(mTitleView,mTitleParams);

        }else{
            mTitleView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置显示标题和监听器
     * @param title
     * @param mListener
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setTitleAndLeftImageButton(String title,topbarClickListener mListener){

        setDefaultTitle(title);
        if(mLeftButton!=null&&mLeftBackground!=null){
            mLeftButton.setBackground(mLeftBackground);
            mLeftParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
            mLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,TRUE);
            addView(mLeftButton,mLeftParams);
            setOnTopbarClickListener(mListener);
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setTitleAndBothButton(String title,String text,topbarClickListener mListener){
        setTitleAndLeftImageButton(title,mListener);
        if(mRightButton!=null&&mLeftBackground!=null){
            mRightButton.setBackground(mRightBackground);
            mRightParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
            mRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,TRUE);
            addView(mRightButton,mRightParams);
        }
    }

    /**
     * 设置按钮的显示与否，id区分按钮，flag区分是否显示
     * @param id 0表示左侧按钮 1表示右侧按钮
     * @param flag 是否显示
     */
    public void setButtonVisable(int id,boolean flag){
        if(flag){
            if(id == 0){
                mLeftButton.setVisibility(VISIBLE);
            }else {
                mRightButton.setVisibility(VISIBLE);
            }
        }else {
            if(id == 0){
                mLeftButton.setVisibility(GONE);
            }else {
                mRightButton.setVisibility(GONE);
            }
        }
    }

    /**
     * 设置按钮的可用与否，id区分按钮，flag区分是否显示
     * @param id 0表示左侧按钮 1表示右侧按钮
     * @param flag 是否显示
     */
    public void setButtonEnable(int id,boolean flag){
        if(flag){
            if(id == 0){
                mLeftButton.setEnabled(true);
            }else {
                mRightButton.setEnabled(true);;
            }
        }else {
            if(id == 0){
                mLeftButton.setEnabled(false);
            }else {
                mRightButton.setEnabled(false);
            }
        }
    }

    public interface topbarClickListener{
        void leftClick();
        void rightClick();
    }

    public void setOnTopbarClickListener(topbarClickListener mListener){
        this.mListener = mListener;
    }

}
