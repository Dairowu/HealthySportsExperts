package cn.xietong.healthysportsexperts.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import com.lyft.android.scissors.CropView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.ui.view.TopBar;
import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static rx.schedulers.Schedulers.io;
import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * Created by 邓贺文 on 2016/4/4.
 */
public class ChoosePhotoActivity extends BaseActivity{

    @Bind(R.id.crop_view)
    CropView cropView;

    CompositeSubscription subscription = new CompositeSubscription();

    @Override
    public int getLayoutId() {
        return R.layout.activity_choosephoto;
    }

    @Override
    public void initViews() {

        ButterKnife.bind(this);

        Uri uri = getIntent().getData();
        Log.i("info",uri.toString());

        cropView.extensions()
                .load(uri.toString());

        Log.i("info",cropView.getHeight()+" width "+ cropView.getWidth());

        initTopbarForBoth("", "", new TopBar.topbarClickListener() {
            @Override
            public void leftClick() {
                finish();
                showToast("取消选择");
            }

            @Override
            public void rightClick() {
                final File croppedFile = new File(getCacheDir(), "cropped.jpg");

                Observable<Void> onSave = Observable.from(cropView.extensions()
                        .crop()
                        .quality(100)
                        .format(Bitmap.CompressFormat.JPEG)
                        .into(croppedFile))
                        .subscribeOn(io())
                        .observeOn(mainThread());

;
                subscription.add(onSave.subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        final BmobFile bmobFile = new BmobFile(croppedFile);
                        bmobFile.upload(ChoosePhotoActivity.this, new UploadFileListener() {
                            @Override
                            public void onSuccess() {
                                showToast("头像设置成功");
                                String url = bmobFile.getFileUrl(ChoosePhotoActivity.this);
                                mApplication.getSharedPreferencesUtil().setAvatarUrl(url);
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                showToast("头像上传失败"+s);
                            }
                        });
                        finish();
                    }
                }));
            }
        });

        topBar.setBackgroundColor(Color.BLACK);
    }

    @Override
    public void setupViews() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription = null;
    }
}
