package cn.xietong.healthysportsexperts.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.utils.Post;

//import cn.xietong.healthysportsexperts.adpter.PostListViewAdapter;
//import cn.xietong.healthysportsexperts.ui.activity.PostActivity;

/**
 * 发帖界面
 * Created by Administrator on 2015/10/18.
 */
public class FragmentPagePosition_son2 extends BaseFragment {
    private ListView mlistView;
    private List<Post> posts;
//    private PostListViewAdapter postListViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final int REFRESH_COMPLETE = 0X110;


    @Override
    public int getLayoutId() {
        return R.layout.fragment1_son2;
    }

    protected void initViews(View mContentView) {
        mlistView = (ListView) mContentView.findViewById(R.id.list_posts);
        swipeRefreshLayout = (SwipeRefreshLayout) mContentView.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new LSListener());
        posts = new ArrayList<>();
        posts.add(new Post("hahah"));
        posts.add(new Post("hahah"));
        posts.add(new Post("hahah"));
//        postListViewAdapter = new PostListViewAdapter(mContentView.getContext(), R.layout.post_item, posts);
//        mlistView.setAdapter(postListViewAdapter);
        mlistView.setOnItemClickListener(new ListViewListener());
    }

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    posts.add(new Post("hahah"));
//                    postListViewAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

    class LSListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            myHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
        }
    }

    @Override
    public void setupViews(Bundle savedInstanceState) {

    }
    class ListViewListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(view.getId()!=0){
//                Intent intent = new Intent(getContext(), PostActivity.class);
//                startActivity(intent);
            }
        }
    }
}
