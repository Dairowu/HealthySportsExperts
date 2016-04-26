package cn.xietong.healthysportsexperts.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.xietong.healthysportsexperts.R;

/**
 * Created by 林思旭 on 2016/4/14.
 */
public class DeleteDialog extends Dialog implements View.OnClickListener{
    private TextView txt_delete,txt_unread;
    private DeleteListener listener_delete;

    public interface DeleteListener{
        public void onClick(View view);
    }

    public DeleteDialog(Context context , DeleteListener listener) {
        super(context);
        listener_delete = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_son1_delete_diaolog);
        txt_delete = (TextView)findViewById(R.id.txt_delete_recent_message);
        txt_unread = (TextView)findViewById(R.id.txt_set_unread_message);
        txt_delete.setOnClickListener(this);
        txt_unread.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        listener_delete.onClick(v);
    }
}
