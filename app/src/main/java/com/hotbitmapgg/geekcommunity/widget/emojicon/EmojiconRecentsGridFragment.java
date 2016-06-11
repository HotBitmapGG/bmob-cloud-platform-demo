package com.hotbitmapgg.geekcommunity.widget.emojicon;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.hotbitmapgg.geekcommunity.R;


/**
 * @author Daniele Ricci
 */
public class EmojiconRecentsGridFragment extends EmojiconGridFragment implements EmojiconRecents
{
	private EmojiAdapter mAdapter;
	private boolean mUseSystemDefault = false;

	private static final String USE_SYSTEM_DEFAULT_KEY = "useSystemDefaults";

	protected static EmojiconRecentsGridFragment newInstance()
	{
		return newInstance(false);
	}

	protected static EmojiconRecentsGridFragment newInstance(boolean useSystemDefault)
	{
		EmojiconRecentsGridFragment fragment = new EmojiconRecentsGridFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean(USE_SYSTEM_DEFAULT_KEY, useSystemDefault);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
			mUseSystemDefault = getArguments().getBoolean(USE_SYSTEM_DEFAULT_KEY);
		}
		else
		{
			mUseSystemDefault = false;
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		EmojiconRecentsManager recents = EmojiconRecentsManager.getInstance(view.getContext());

		mAdapter = new EmojiAdapter(view.getContext(), recents, mUseSystemDefault);
		GridView gridView = (GridView) view.findViewById(R.id.Emoji_GridView);
		gridView.setAdapter(mAdapter);
		gridView.setOnItemClickListener(this);
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		mAdapter = null;
	}

	@Override
	public void addRecentEmoji(Context context, Emojicon emojicon)
	{
		EmojiconRecentsManager recents = EmojiconRecentsManager.getInstance(context);
		recents.push(emojicon);

		// notify dataset changed
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();
	}

}
