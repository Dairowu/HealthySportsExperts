package cn.xietong.healthysportsexperts.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**@author Administrator 林思旭，2016.4.9
 *
 */
public class ViewPagerAdapter extends PagerAdapter {
	
	private List<View> views;
	private Context context;
	
	public ViewPagerAdapter(Context context, List<View> views) {
		// TODO Auto-generated constructor stub
		super();
		this.views = views;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}


	@Override
	public Object instantiateItem(View container, int position) {
		// TODO Auto-generated method stub
		((ViewPager) container).addView(views.get(position));
		return views.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager)container).removeView(views.get(position));	
	}
	
	
}
