package cn.xietong.healthysportsexperts.ui.activity;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.adapter.ListViewAdapter;
import cn.xietong.healthysportsexperts.model.Comment;

public class Activity_PostDetail extends BaseActivity implements View.OnClickListener {
    private String TAG = "Activity_PostDetail";
    private ListView listView;
    private List<Comment> comments = new ArrayList<>();
    private ListViewAdapter adapter;
    private String name;
    private String text;
    private String id;
    private TextView userName;
    private TextView item_main_article;
    private TextView tv_btn_comment;
    private ImageView iv_back;
    private EditText ed_comment;
    private TextView comment_number_tag;
    private BmobUser user;
    private static final int REFESH = 1;
    private static final int REFESH_FAILED = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFESH:
                    Log.e(TAG, "刷新成功");
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    showToast("服务器出故障啦~");
                    break;
            }
        }
    };


    @Override
    public int getLayoutId() {
        return R.layout.activity_post_detail;
    }

    /**
     * 初始化控件
     */
    @Override
    public void initViews() {
        initTopbarForLeft("正文", new OnTopbarButtonClickListener());
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        text = intent.getStringExtra("text");
        id = intent.getStringExtra("id");

        listView = (ListView) findViewById(R.id.list_comment);
        tv_btn_comment = (TextView) findViewById(R.id.tv_btn_comment);
        ed_comment = (EditText) findViewById(R.id.ed_comment);
        //给listview设置适配器和添加头部
        View header = LayoutInflater.from(this).inflate(R.layout.header_post_detail, listView, false);
        listView.addHeaderView(header);
        adapter = new ListViewAdapter(Activity_PostDetail.this, R.layout.item_comment, comments);
        listView.setAdapter(adapter);
        userName = (TextView) header.findViewById(R.id.item_user_name);
        item_main_article = (TextView) header.findViewById(R.id.item_main_article);
        iv_back = (ImageView) header.findViewById(R.id.iv_back);
        comment_number_tag = (TextView) header.findViewById(R.id.comment_number_tag);
    }

    /**
     * 初始化事件
     */
    @Override
    public void setupViews() {
        tv_btn_comment.setOnClickListener(this);
        ed_comment.setOnClickListener(this);
        userName.setText(name);
        item_main_article.setText(text);
        refresh();
    }

    /**
     * 监听器
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_comment:
                Comment comment = new Comment(name, ed_comment.getText().toString(), id);
                comment.setId(id);
                comment.save(this, new SaveListener() {
                    @Override
                    public void onSuccess() {//发送成功，返回刷新
                        showToast("发送成功");
                        ed_comment.setText(null);
                        refresh();

                    }

                    @Override
                    public void onFailure(int i, String s) {//发送失败
                        Log.e("wxj", "onFailure: " + s);
                        showToast("发送失败");
                    }
                });
                break;
            case R.id.iv_back:
                finish();
        }
    }

    /**
     * 刷新的方法。由于要访问网络所以新建子线程
     */
    public void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //清空原有数据；
                    comments.clear();
                    BmobQuery<Comment> query = new BmobQuery<>();
                    //查询comment中对应id的数据
                    query.addWhereEqualTo("id", id);
                    query.setLimit(20);//限制最多20条数据
                    query.findObjects(Activity_PostDetail.this, new FindListener<Comment>() {
                        @Override
                        public void onSuccess(List<Comment> object) {
                            // TODO Auto-generated method stub
                            int i = 1;//记住当前几楼
                            for (Comment comment : object) {//从服务端得到数据
                                Log.e(TAG, "onSuccess: 查找成功");
                                comment.setIndex(i);
                                comments.add(comment);
                                Message message = new Message();
                                message.what = REFESH;
                                handler.sendMessage(message);
                                i++;
                            }
                            //设置评论条数
                            comment_number_tag.setText("评论" + (i - 1) + "条");
                        }

                        @Override
                        public void onError(int code, String msg) {
                            // TODO Auto-generated method stub
                            Log.e(TAG, "查找失败");
                            Message message = new Message();
                            message.what = REFESH_FAILED;
                            handler.sendMessage(message);
                        }
                    });
                } catch (Exception e) {
                    showToast("网络错误");
                }
            }
        }).start();
    }

}
