package cn.xietong.healthysportsexperts.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.config.BmobConstants;
import cn.xietong.healthysportsexperts.ui.view.TopBar;

/**
 * Created by mr.deng on 2016/5/4
 */
public class ChangeInfoActivity extends BaseActivity{

    private EditText mEditText;

    @Override
    public int getLayoutId() {
        return getIntent().getIntExtra("layout",0);
    }

    @Override
    public void initViews() {
        mEditText = (EditText) findViewById(R.id.et_change_info);
        final Intent intent = getIntent();
        initTopbarForBoth(intent.getStringExtra("title"), "",new TopBar.topbarClickListener() {
            @Override
            public void leftClick() {
                onBackPressed();
            }

            @Override
            public void rightClick() {
                intent.putExtra("et_content",String.valueOf(mEditText.getText()));
                ChangeInfoActivity.this.setResult(BmobConstants.REQUESTCODE_CHANGE_INFO,intent);
                onBackPressed();
            }
        });

        mEditText.setHint(intent.getStringExtra("et_hint"));
        mEditText.setText(intent.getStringExtra("et_content"));

        topBar.setButtonEnable(1,false);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                topBar.setButtonEnable(1,true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public void setupViews() {

    }

    public static void actionStart(Context context,int layoutId,String title,String et_content){
        Intent intent = new Intent(context,ChangeInfoActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("layout",layoutId);
        intent.putExtra("et_content",et_content);
        context.startActivity(intent);
    }
}
