package cn.xietong.healthysportsexperts.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;

import com.lyft.android.scissors.CropView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.config.BmobConstants;
import cn.xietong.healthysportsexperts.ui.view.TopBar;
import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

/**选择用户照片的Activity
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

        cropView.extensions()
                .load(uri.toString());

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
                        .quality(87)
                        .format(Bitmap.CompressFormat.JPEG)
                        .into(croppedFile))
                        .subscribeOn(io())
                        .observeOn(mainThread());

;
                subscription.add(onSave.subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Intent intent = getIntent();
                        intent.setData(Uri.fromFile(croppedFile));
                        ChoosePhotoActivity.this.setResult(BmobConstants.REQUESTCODE_CHOOSE_PHOTO,intent);
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
