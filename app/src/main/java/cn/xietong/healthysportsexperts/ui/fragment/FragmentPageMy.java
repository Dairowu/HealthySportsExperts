package cn.xietong.healthysportsexperts.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.model.MyUser;
import cn.xietong.healthysportsexperts.ui.activity.Activity_Login;
import cn.xietong.healthysportsexperts.ui.activity.Activity_Remind;

/**我的界面，该界面用于显示用户个人信息等
 * Created by Administrator on 2015/10/14.
 */
public class FragmentPageMy extends BaseFragment implements View.OnClickListener{

    RelativeLayout rl_my;
    ImageView iv_head_photo;
    TextView tv_name,tv_signature;

    Button btn_id,btn_remind,btn_about,btn_exit;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_4;
    }

    @Override
    protected void initViews(View mContentView) {

        rl_my = (RelativeLayout) findViewById(R.id.relativeLayout);
        iv_head_photo = (ImageView) findViewById(R.id.iv_head_photo);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_signature = (TextView) findViewById(R.id.tv_signature);


        btn_id = (Button) findViewById(R.id.btn_id);
        btn_remind = (Button) findViewById(R.id.btn_remind);
        btn_about = (Button) findViewById(R.id.btn_about);
        btn_exit = (Button) findViewById(R.id.btn_exit);

        Log.i("info",btn_id+""+btn_about+"");
        btn_id.setOnClickListener(this);
        btn_remind.setOnClickListener(this);
        btn_about.setOnClickListener(this);
        btn_exit.setOnClickListener(this);

    }

    @Override
    public void setupViews(Bundle savedInstanceState) {

        MyUser mUser =  mUserManager.getCurrentUser(MyUser.class);
        tv_name.setText(mUser.getNick());
        tv_signature.setText(mUser.getSignature());
        btn_id.setText(mUser.getUsername());

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.relativeLayout:

                break;
            case R.id.btn_id:

                break;
            case R.id.btn_remind:
                Log.i("info","跳转");
                Intent intent = new Intent(getActivity(), Activity_Remind.class);
                startActivity(intent);
                break;
            case R.id.btn_about:

                break;
            case R.id.btn_exit:
                mApplication.logout();
                getActivity().finish();
                startActivity(new Intent(getActivity(), Activity_Login.class));
                break;
            default:
                break;
        }

    }
}
