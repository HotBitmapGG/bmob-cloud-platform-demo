package com.hotbitmapgg.geekcommunity.widget.emojicon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.hotbitmapgg.geekcommunity.R;

import java.util.List;


/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
class EmojiAdapter extends ArrayAdapter<Emojicon>
{
	private boolean mUseSystemDefault = false;

	public EmojiAdapter(Context context, List<Emojicon> data)
	{
		super(context, R.layout.emojicon_item, data);
		mUseSystemDefault = false;
	}

	public EmojiAdapter(Context context, List<Emojicon> data, boolean useSystemDefault)
	{
		super(context, R.layout.emojicon_item, data);
		mUseSystemDefault = useSystemDefault;
	}

	public EmojiAdapter(Context context, Emojicon[] data)
	{
		super(context, R.layout.emojicon_item, data);
		mUseSystemDefault = false;
	}

	public EmojiAdapter(Context context, Emojicon[] data, boolean useSystemDefault)
	{
		super(context, R.layout.emojicon_item, data);
		mUseSystemDefault = useSystemDefault;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;
		if (v == null)
		{
			v = View.inflate(getContext(), R.layout.emojicon_item, null);
			ViewHolder holder = new ViewHolder();
			holder.icon = (EmojiconTextView) v.findViewById(R.id.emojicon_icon);
			holder.icon.setUseSystemDefault(mUseSystemDefault);
			v.setTag(holder);
		}
		Emojicon emoji = getItem(position);
		ViewHolder holder = (ViewHolder) v.getTag();
		holder.icon.setText(emoji.getEmoji());
		return v;
	}

	static class ViewHolder
	{
		EmojiconTextView icon;
	}
}