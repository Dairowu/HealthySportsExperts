package cn.xietong.healthysportsexperts.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.model.Post;


/**
 * Created by gundam on 2015/10/25.
 * 主界面RecyclerView的适配器
 */
public class PostRecylerViewAdapter extends RecyclerView.Adapter<ViewHolder> {
    private List<Post> mDatas;
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    public interface OnItemClickListener {
        void OnItemClick(View view, int positision);

        void OnItemLongClick(View view, int positision);

        void OnCommentClick(View view, int positision);
    }


    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mDatas.size() == 0 ? 0 : mDatas.size() + 1;
    }

    public PostRecylerViewAdapter(Context context, List<Post> datas) {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new MyViewHolder(mInflater.inflate(R.layout.item_post, parent,
                    false));
        } else if (viewType == TYPE_FOOTER) {

            return new FootViewHolder(mInflater.inflate(R.layout.item_footer_post, parent,
                    false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).userName.setText(mDatas.get(position).getUserName());
            ((MyViewHolder) holder).item_main_article.setText(mDatas.get(position).getMainArticle());
            // 如果设置了回调，则设置点击事件
            if (mOnItemClickListener != null) {
                ((MyViewHolder) holder).item_action_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.OnCommentClick(((MyViewHolder) holder).item_action_comment, pos);
                    }
                });
                ((MyViewHolder) holder).userName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.OnItemClick(((MyViewHolder) holder).userName, pos);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.OnItemLongClick(holder.itemView, pos);
                        return false;
                    }
                });
            }
        }
    }


    static class MyViewHolder extends ViewHolder {
        TextView userName;
        TextView item_main_article;
        TextView item_action_love;
        TextView item_action_share;
        TextView item_action_comment;
        ImageView item_user_logo;
        ImageView item_action_fav;
        ImageView content_image;

        public MyViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.item_user_name);
            item_main_article = (TextView) view.findViewById(R.id.item_main_article);
            item_action_comment = (TextView) view.findViewById(R.id.item_action_comment);
        }
    }

    class FootViewHolder extends ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }
}

