package cn.xietong.healthysportsexperts.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 
 * @author 林思旭,2016.4.9
 *
 */
public class BmobTextView extends TextView{

	

	public BmobTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public BmobTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public BmobTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void setText(CharSequence text, BufferType type) {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(text)) {
			super.setText(replace(text.toString()), type);
		} else {
			super.setText(text, type);
		}
	}

	private Pattern buildPattern() {
		//一个正则表达式，用来识别传过来的字符串是否是表情字符串
		return Pattern.compile("\\\\ue[a-z0-9]{3}", Pattern.CASE_INSENSITIVE);
	}
    
	
	private CharSequence replace(String text) {
		try {
			//这类文本的内容是不变的,而是标记对象可以被附加和分离。可变的文本,见SpannableStringBuilder。
			SpannableString spannableString = new SpannableString(text);
			int start = 0;
			Pattern pattern = buildPattern();
			Matcher matcher = pattern.matcher(text);
			//匹配text中的表情符号
			while (matcher.find()) {
				String faceText = matcher.group();
				String key = faceText.substring(1);
				BitmapFactory.Options options = new BitmapFactory.Options();
				//根据ID得到相应的表情信息
				Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),
						getContext().getResources().getIdentifier(key, "drawable", getContext().getPackageName()), options);
				ImageSpan imageSpan = new ImageSpan(getContext(), bitmap);
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0)
					spannableString.setSpan(imageSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				start = (endIndex - 1);
			}
			return spannableString;
		} catch (Exception e) {
			return text;
		}
	}
}
