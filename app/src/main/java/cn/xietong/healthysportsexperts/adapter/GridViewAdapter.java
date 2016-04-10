package cn.xietong.healthysportsexperts.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import cn.xietong.healthysportsexperts.R;
import cn.xietong.healthysportsexperts.utils.FaceText;

/**
 * 
 * @author 林思旭，2016.4.9
 *
 */
public class GridViewAdapter extends BaseAdapter {

	private String TAG = "GridViewAdapter";
	private Context context;
	private List<FaceText> datas;
	
	public GridViewAdapter(Context context,List<FaceText> datas){
		this.context = context;
		this.datas = datas;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_face_text, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.iv_face_text);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		FaceText faceText = (FaceText)getItem(position);//选取哪个表情
		String key = faceText.text.substring(1);
		Drawable drawable = context.getResources().getDrawable(
				context.getResources().getIdentifier(key,"drawable", 
						context.getPackageName()));
//		Log.i(TAG, "drawable"+drawable);
		holder.image.setImageDrawable(drawable);
		return convertView;
	}

	class ViewHolder{
		ImageView image;
	}
}
