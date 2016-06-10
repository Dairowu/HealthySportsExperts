package cn.xietong.healthysportsexperts.ui.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.adapter.CommonAdapter;
import cn.xietong.healthysportsexperts.model.Cell;
import cn.xietong.healthysportsexperts.model.DatabaseHelper;
import cn.xietong.healthysportsexperts.model.UserInfo;
import cn.xietong.healthysportsexperts.ui.view.ClockView;
import cn.xietong.healthysportsexperts.utils.SQLiteUtils;
import cn.xietong.healthysportsexperts.utils.Service_Calculate_Step;
import cn.xietong.healthysportsexperts.utils.StepListener;
import cn.xietong.healthysportsexperts.utils.UserUtils;
import cn.xietong.healthysportsexperts.utils.ViewHolder;

/**计步界面
 * Created by deng on 2015/10/18.
 */
public class FragmentPagePosition_son1 extends BaseFragment {

    public static final int UPDATE_TEXT = 0x1;
    //一整天的毫秒数
    public static final long DAYTIME_MILL = 86400000;
    private View view;
    //步数显示
    private TextView tv_stepNumber;
    //目标步数显示
    private TextView tv_goalStep;
    //日期显示
    private TextView tv_date;
    //记录
    private TextView tv_recorder;
    private Intent intent;
    //同一天内退出应用之前的数据
    private int oldCount;
    //目标步数
    private int goalCount = 5200;
    private ClockView mClockView;
    Service_Calculate_Step.MyBinder binder;
    //数据库操作对象
    DatabaseHelper dbHelper = mApplication.getDBHelper();
    //用户从使用程序到现在为止的每一天的操作数据
    List<UserInfo> userLists = null;
    TreeMap<String,Integer> map;
    //用户操作后的数据
    UserInfo user = new UserInfo();
    //计步监听器
    StepListener mStepListener = mApplication.getStepListener();
    //定义日期格式
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy年MM月dd日EEEE", Locale.CHINA);


    /**
     * 通过此步操作获得binder对象，再通过其获得Service中记录的步数
     */
    public ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (Service_Calculate_Step.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 动态显示步数
     */
    private Handler handler = new Handler() {

        public void handleMessage(Message msg){
            if(msg.what == UPDATE_TEXT){
                int stepNum = msg.arg1;
                tv_stepNumber.setText(Integer.toString(stepNum));
                startAnimator(stepNum);
            }
        }

    };

    //使用属性动画实现动态更新步数进度
    private synchronized void startAnimator(int progress) {
        mClockView.setProgress(progress);
//        int num = (progress/goalCount)*360;
//        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mClockView, "progress", 0,num);
//        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//        objectAnimator.setDuration(800);
//        objectAnimator.start();
    }

    /**
     * 绑定Service
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        UserUtils.loadFromDatabase();
        userLists = UserUtils.getList();
        map = UserUtils.map;
        //TODO 使用TreeMap实现根据每天的日期获得当天的步数

        for (Integer i: map.values()) {
            Log.i("info",i+"步");
        }
        
        if(userLists!=null&&userLists.size()>0) {
            //存储的最近一天的时间
//            Long recentTime = Long.parseLong(userLists.get(0).getDatetime());
            //当前时间
            Long nowTime = new Date().getTime();
            String old = userLists.get(0).getDatetime();
            String now = simpleDateFormat.format(new Date());
            Log.i("info","old"+ old+"now"+now);
            //通过比较最近一次的时间和今天时间是否相同，判断是否将数据库中记录的步数设置为今天已经计的步数，
            // 如果不是则将今天与记录之间的天数里的记录添加上并且值为0
            if ((old.equals(now))) {
                oldCount = userLists.get(0).getCount();
                mStepListener.setCount(oldCount);
            } else {
//                //记录的最近一天与当前时间间的天数
//                int among_days_number = (int) ((nowTime - recentTime)/DAYTIME_MILL - 1);
//                if(among_days_number > 0){
//                    for(int i = 0;i < among_days_number;i++){
//                        user.setCount(0);
//                        user.setDatetime((recentTime+DAYTIME_MILL*i)+"");
//                        SQLiteUtils.insert(dbHelper,user,"step");
//                    }
//                }
            }
        }

        Intent bindIntent = new Intent(context,Service_Calculate_Step.class);
        context.getApplicationContext().bindService(bindIntent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment1_son1;
    }

    @Override
    protected void initViews(View mContentView) {
        tv_stepNumber = (TextView) mContentView.findViewById(R.id.tv_stepNumber);
        tv_goalStep = (TextView) mContentView.findViewById(R.id.tv_goalStep);
        tv_date = (TextView) mContentView.findViewById(R.id.tv_date);
        tv_recorder = (TextView) mContentView.findViewById(R.id.tv_recorder);
        mClockView = (ClockView) findViewById(R.id.clock_view);

        tv_goalStep.setText(String.valueOf(goalCount));
        mClockView.setMax(goalCount);
    }

    @Override
    public void setupViews(Bundle savedInstanceState) {

        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnekeyShare onekeyShare = new OnekeyShare();
//                Platform platform = ShareSDK.getPlatform(getActivity(), SinaWeibo.NAME);
//                platform.SSOSetting(true);
//                onekeyShare.disableSSOWhenAuthorize();
                onekeyShare.setTitle("一键分享");
                onekeyShare.setTitleUrl("http://mob.com");
                onekeyShare.setText("我今天走了"+binder.getCount()+"步!健康运动每一天。");
                onekeyShare.setComment("测试文本");
                onekeyShare.setSite("Jiankang");
                onekeyShare.setSiteUrl("http://sharesdk.cn");
                onekeyShare.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
                onekeyShare.show(getActivity());
            }
        });

        tv_recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final int month = getMonth();
                final View contentView = inflater.inflate(R.layout.pop_bottom_calendar,null);
                final TextView tv_month = (TextView) contentView.findViewById(R.id.tv_month);
                ViewPager vp = (ViewPager) contentView.findViewById(R.id.vp_calendar);
                vp.setOffscreenPageLimit(3);
                vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                        GregorianCalendar now = new GregorianCalendar(Locale.CHINA);
                        now.add(Calendar.MONTH,position + 1 - month);
                        tv_month.setText(String.valueOf(now.get(Calendar.MONTH) + 1));

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                vp.setAdapter(new PagerAdapter() {

                    @Override
                    public Object instantiateItem(ViewGroup container, int position) {
                        GridView gridView = new GridView(getActivity());
                        gridView.setScrollbarFadingEnabled(true);
                        gridView.setNumColumns(7);
                        List<Cell> datas = initDatas(position + 1 - month);

                        BaseAdapter adapter = new CommonAdapter<Cell>(getActivity(),datas,R.layout.grid_item) {
                            @Override
                            public int getItemViewType(int position) {
                                return 0;
                            }

                            @Override
                            protected void initConvert(ViewHolder holder, Cell cell) {
                                ClockView cv = holder.getView(R.id.clockViewRecorder);
                                if(cell.isCurrentMonth()) {
                                    holder.setText(R.id.tv_day, cell.getWhich_day());
                                    cv.setProgress(cell.getProgress());
                                }else{
                                    cv.setVisibility(View.GONE);
                                }
                            }
                        };
                        gridView.setAdapter(adapter);

                        container.addView(gridView);
                        return gridView;
                    }

                    @Override
                    public void destroyItem(ViewGroup container, int position, Object object) {
                        container.removeView((View) object);
                    }

                    @Override
                    public int getCount()
                    {
                        return month;
                    }

                    @Override
                    public boolean isViewFromObject(View view, Object object) {
                        return view == object;
                    }
                });
                vp.setCurrentItem(month - 1);

                PopupWindow pop = new PopupWindow(contentView);
                pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

                pop.setAnimationStyle(R.style.pop_bottom_in_out);
                pop.setBackgroundDrawable(new ColorDrawable(0));
                pop.setFocusable(true);
                pop.setOutsideTouchable(true);

                View rootView = inflater.inflate(R.layout.main_tab_layout,null);
                pop.showAtLocation(rootView, Gravity.BOTTOM,0,0);
                pop.update();
            }
        });

    }

    /**
     * 获得开始使用软件到当前时间之间的月数
     * @return 月数
     */
    private int getMonth(){
        GregorianCalendar startDate = new GregorianCalendar();
        GregorianCalendar endDate = new GregorianCalendar();
        Date date = null;
        if(userLists != null && !userLists.isEmpty()) {
            try {
                date = simpleDateFormat.parse(userLists.get(userLists.size() - 1).getDatetime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            date = new Date();
        }
        startDate.setTime(date);

        int year = endDate.get(Calendar.YEAR)  - startDate.get(Calendar.YEAR) + 1;
        int month = 0;
        if(year == 0){
            month = endDate.get(Calendar.MONTH) - startDate.get(Calendar.MONTH) + 1;
        }else{
            int startMonth = 12 - startDate.get(Calendar.MONTH);
            int endMonth = endDate.get(Calendar.MONTH) + 1;
            int centerMonth = 12 * (year - 2);
            month = startMonth + centerMonth + endMonth;
        }
        return month;
    }

    /**
     * 初始化每个月的数据
     * @param position 要显示的月份与当前月份相差的数值
     * @return 初始化后的数据
     */
    private List<Cell> initDatas(int position) {
        List<Cell> datas = new ArrayList<>();
        GregorianCalendar d = new GregorianCalendar();
        d.add(Calendar.MONTH,position);

        int today = d.get(Calendar.DAY_OF_MONTH);
        int month = d.get(Calendar.MONTH);

        d.set(Calendar.DAY_OF_MONTH,1);

        int weekday = d.get(Calendar.DAY_OF_WEEK);

        int firstDayOfWeek = d.getFirstDayOfWeek();

        Cell cell = new Cell("",0,false,false);
        int intent = 0;
        while(weekday != firstDayOfWeek){
            d.add(Calendar.DAY_OF_MONTH,-1);
            weekday = d.get(Calendar.DAY_OF_WEEK);
            datas.add(cell);
            intent++;
        }

        //因为之前已经减到了上一个月，所以应该确保在当前月的情况下再设置到当前月的第一天
        d.add(Calendar.DAY_OF_MONTH,intent);

        d.set(Calendar.DAY_OF_MONTH,1);

        do{
            int day = d.get(Calendar.DAY_OF_MONTH);
            Cell c;
            //从数据库中获得的当天的步数
            int count = 0;
            if(map.get(simpleDateFormat.format(d.getTime())) != null)
                count = map.get(simpleDateFormat.format(d.getTime()));

            c = new Cell(Integer.toString(day), count, false, true);

            d.add(Calendar.DAY_OF_MONTH,1);
            datas.add(c);
        }while (d.get(Calendar.MONTH) == month);

        return datas;
    }

    /**
     * 开启线程动态获取步数
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tv_date.setText(simpleDateFormat.format(new Date()));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        Message message = new Message();
                        message.what = UPDATE_TEXT;
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(binder!=null) {
                            message.arg1 = binder.getCount();
                        }
                        handler.sendMessage(message);
                    }
                }
            }).start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        user.setCount(binder.getCount());
        user.setDatetime(simpleDateFormat.format(new Date()));
//        SQLiteUtils.insert(dbHelper,user,"step");
        SQLiteUtils.update(dbHelper,user,"step");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        user.setCount(binder.getCount());
        user.setDatetime(simpleDateFormat.format(new Date()));
//        SQLiteUtils.insert(dbHelper,user,"step");
        SQLiteUtils.update(dbHelper,user,"step");
    }

}
