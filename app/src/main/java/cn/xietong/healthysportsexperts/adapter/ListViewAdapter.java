package cn.xietong.healthysportsexperts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.model.Comment;


/**
 * Created by gundam on 2015/11/16.、
 * 评论ListView的适配器
 */
public class ListViewAdapter extends ArrayAdapter<Comment> {
        private int resource;

    public ListViewAdapter(Context context, int resource, List<Comment> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Comment comment = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resource, null);
            viewHolder = new ViewHolder();
            viewHolder.userName = (TextView) view.findViewById(R.id.userName_comment);
            viewHolder.comment = (TextView) view.findViewById(R.id.content_comment);
            viewHolder.index_comment = (TextView) view.findViewById(R.id.index_comment);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.userName.setText(comment.getUsername());
        viewHolder.comment.setText(comment.getComment());
        viewHolder.index_comment.setText(comment.getIndex()+"楼");
        return view;
    }

    class ViewHolder {
        TextView userName;
        TextView comment;
        TextView index_comment;
    }
}
