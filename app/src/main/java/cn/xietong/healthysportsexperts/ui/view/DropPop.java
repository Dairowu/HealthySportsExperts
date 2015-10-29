package cn.xietong.healthysportsexperts.ui.view;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import cn.xietong.healthysportsexperts.R;

/**泡泡窗口
 * Created by Administrator on 2015/10/19.
 */
public class DropPop extends PopupWindow {

    private View view;
    private LayoutInflater inflater;
    private LinearLayout layoutAlert,layoutTheme,layoutReply;

    public DropPop(Activity paramActivity, View.OnClickListener paramOnClickListener){
        super(paramActivity);
        inflater = LayoutInflater.from(paramActivity);
        view = inflater.inflate(R.layout.discuss_pop,null);

        layoutAlert = (LinearLayout) view.findViewById(R.id.layout_alert);
        layoutTheme = (LinearLayout) view.findViewById(R.id.layout_theme);
        layoutReply = (LinearLayout) view.findViewById(R.id.layout_reply);

        if(paramOnClickListener!=null) {
            layoutAlert.setOnClickListener(paramOnClickListener);
            layoutTheme.setOnClickListener(paramOnClickListener);
            layoutReply.setOnClickListener(paramOnClickListener);
        }

        setContentView(view);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.AnimTools);
        //设置背景透明
        setBackgroundDrawable(new ColorDrawable(0));
    }

}
