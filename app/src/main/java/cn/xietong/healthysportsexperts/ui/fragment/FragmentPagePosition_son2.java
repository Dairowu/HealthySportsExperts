package cn.xietong.healthysportsexperts.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.adapter.PostRecylerViewAdapter;
import cn.xietong.healthysportsexperts.model.Post;
import cn.xietong.healthysportsexperts.ui.activity.Activity_Post;
import cn.xietong.healthysportsexperts.ui.activity.Activity_PostDetail;


/**
 * 帖子界面
 * Created by wxj on 2015/10/18.
 */
public class FragmentPagePosition_son2 extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private String TAG = "wxj";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<Post> posts;
    private PostRecylerViewAdapter postRecylerViewAdapter;
    private FloatingActionButton fab;
    private static final int REFESH = 1;
    private static final int LOAD_NEXT = 2;
    private static final int REFESH_FAILED = 0;
    private boolean isLoading = false;
    private  int updateCounts =1;
    private boolean NO_MORE = false;
    /**
     * 获得子线程发出的message刷新。
     */
    private Handler refeshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFESH:
                    Log.e(TAG, "刷新成功");//提示用户
                    postRecylerViewAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    isLoading = false;
                    updateCounts = 1;
                    break;
                case LOAD_NEXT:
                    Log.e(TAG, "加载下一页刷新成功");//提示用户
                    postRecylerViewAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    updateCounts++;
                    isLoading = false;
                    showToast("加载下一页刷新完成");
                    break;
                default:
                    showToast("请检查网络");
                    swipeRefreshLayout.setRefreshing(false);
                    isLoading = false;
                    break;
            }
        }
    };

    @Override
    public void setupViews(Bundle savedInstanceState) {

    }

    @Override
    protected void initViews(View mContentView) {
        findId();
        initEvent();
    }


    /**
     * 设置布局文件
     *
     * @return
     */
    @Override
    public int getLayoutId() {
        return R.layout.fragment1_son2;
    }


    /**
     * 初始化界面
     */
    private void initEvent() {
        swipeRefreshLayout.setOnRefreshListener(this);
        fab.setOnClickListener(this);
        posts = new ArrayList<>();
        postRecylerViewAdapter = new PostRecylerViewAdapter(getActivity(), posts);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(postRecylerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new MyOnScrollListener());
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        postRecylerViewAdapter.setOnItemClickListener(new PostRecylerViewAdapter.OnItemClickListener() {
            @Override
            public void OnCommentClick(View view, int positision) {
                Post post = posts.get(positision);
                Intent intent = new Intent(getActivity(), Activity_PostDetail.class);
                //把帖子的信息和当前用户昵称传递过去。
                intent.putExtra("name", post.getUserName());
                intent.putExtra("text", post.getMainArticle());
                intent.putExtra("id", post.getObjectId());
                startActivity(intent);
            }

            @Override
            public void OnItemClick(View view, int position) {
                Post post = posts.get(position);
                Intent intent = new Intent(getActivity(), Activity_PostDetail.class);
                //把帖子的信息和当前用户昵称传递过去。
                intent.putExtra("name", post.getUserName());
                intent.putExtra("text", post.getMainArticle());
                intent.putExtra("id", post.getObjectId());
                startActivity(intent);
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });

    }

    /**
     * 监听器
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        Log.e(TAG, "" + v.getId());
        switch (v.getId()) {
            case R.id.btn_post:
                startActivity(new Intent(getActivity(), Activity_Post.class)); //切换至发帖界面
                break;

        }
    }

    /**
     * 获取控件ID
     */
    public void findId() {
        recyclerView = (RecyclerView) findViewById(R.id.list_posts);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        fab = (FloatingActionButton) findViewById(R.id.btn_post);
    }

    /**
     * 实现刷新的方法
     */
    @Override
    public void onRefresh() {
        //清空原有数据；
        posts.clear();
        getDate(0);
    }

    private void getDate(int updateCounts) {
        final int counts = updateCounts;
        isLoading = true;
        //建立子线程访问网络
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    updateDate(counts);
                } catch (Exception e) {
                    showToast("网络错误");
                }
            }
        }).start();
    }

    private void updateDate(final int counts) {
        BmobQuery<Post> query = new BmobQuery<>();
        query.setSkip(counts*8); // 忽略前N页数据
        query.setLimit(8);//限制最多8条数据
        // 根据时间升序显示数据
        query.order("-updatedAt");
        query.findObjects(getActivity(), new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> object) {
                // TODO Auto-generated method stub
                int i = 0;
                for (Post post :object) {//从服务端得到数据
                    posts.add(post);
                    i++;
                }
                if (i!=8) NO_MORE = true;
                Message message = new Message();
                if(counts==0){
                    message.what = REFESH;
                }
                else message.what = LOAD_NEXT;
                refeshHandler.sendMessage(message);
            }

            @Override
            public void onError(int code, String msg) {
                Log.e(TAG,"error:"+msg);
                Message message = new Message();
                message.what = REFESH_FAILED;
                refeshHandler.sendMessage(message);
            }
        });
    }

    private class MyOnScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Log.d("test", "StateChanged = " + newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemPosition +1 == postRecylerViewAdapter.getItemCount()) {
                Log.e(TAG, "loading executed");
                boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                if (isRefreshing) {
                    postRecylerViewAdapter.notifyItemRemoved(postRecylerViewAdapter.getItemCount());
                    return;
                }
                if(NO_MORE){
                    showToast("没有更多了");
                }
                else if (!isLoading) {
                    isLoading = true;
                    getDate(updateCounts);
                }
            }
        }
        }
}
