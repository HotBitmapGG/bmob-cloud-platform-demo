package com.hotbitmapgg.geekcommunity.pick;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.activity.ParentActivity;
import com.hotbitmapgg.geekcommunity.alumuns.imageutils.CompressImage;
import com.hotbitmapgg.geekcommunity.utils.DisplayUtil;
import com.hotbitmapgg.geekcommunity.utils.NetWorkUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ViewHolder;
import com.hotbitmapgg.geekcommunity.utils.WidgetController;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.ypy.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomGalleryActivity extends ParentActivity implements OnClickListener, GalleryAdapter.OnItemCheckListener
{

	GridView gridGallery;

	Handler handler;

	GalleryAdapter adapter;

	ImageView imgNoMedia;

	Button btnGalleryOk;

	Button btnPre;

	private Button mBtnUpload;

	public static int limit = 9;

	String action;

	private ImageButton leftBtn;

	private Button rightBtn2;

	private TextView title;

	private TextView tv_floder;

	private View view_title, gallery_container, llBottomContainer;

	private ArrayList<ImageItem> mCustomGallery = new ArrayList<ImageItem>();
	private List<Album> mAllAlbums;
	private Album key;

	private AlertDialog mDialog;

	private static final int TYPE_UPLOAD_FROM_TEACHER = 0;

	private static final int TYPE_UPLOAD_FROM_STUDENT = 1;

	private int mUploadType = -1;

	private int mCurrentSelectedPos = 0;

	private boolean mInterrupt = false; // 是否终止上传

	private boolean mLoading = false;

	private GalleryFolderAdapter mGalleryFolderAdapter;

	private ArrayList<String> photos = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gallery);
		
		//设置状态栏颜色
		StatusBarCompat.compat(this);

		// sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
		// Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
		ImageUtils.sImageItems.clear();
		initTitle();
		ImageLoader.getInstance().clearMemoryCache();
		action = getIntent().getAction();
		limit = getIntent().getIntExtra("limit", limit);
		if (action == null)
		{
			finish();
		}
		init();
	}

	private void initTitle()
	{
		leftBtn = (ImageButton) findViewById(R.id.leftBtn);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.title);
		title.setText("选择图片");
		leftBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				keyBackClicked();
			}
		});

		rightBtn2 = (Button) findViewById(R.id.rightBtn2);
		rightBtn2.setText("完成");
		rightBtn2.setOnClickListener(mOkClickListener);
	}

	private void init()
	{

		handler = new Handler();
		view_title = findViewById(R.id.view_title);
		tv_floder = (TextView) findViewById(R.id.tv_floder);
		btnPre = (Button) findViewById(R.id.btnPre);
		gallery_container = findViewById(R.id.gallery_container);
		llBottomContainer = findViewById(R.id.llBottomContainer);
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(false);
		adapter = new GalleryAdapter(getApplicationContext(), ImageLoader.getInstance());
		adapter.setOnItemCheckListener(this);
		PauseOnScrollListener listener = new PauseOnScrollListener(ImageLoader.getInstance(), true, true);
		gridGallery.setOnScrollListener(listener);

		btnGalleryOk = (Button) findViewById(R.id.btnPre);
		btnGalleryOk.setOnClickListener(this);
		mBtnUpload = (Button) findViewById(R.id.btnUpload);
		mBtnUpload.setOnClickListener(this);

		if (action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK))
		{
			btnGalleryOk.setVisibility(View.VISIBLE);
			rightBtn2.setVisibility(View.VISIBLE);
			mBtnUpload.setVisibility(View.GONE);

			llBottomContainer.setVisibility(View.VISIBLE);
			gridGallery.setOnItemClickListener(mItemMulClickListener);
			adapter.setMultiplePick(true);
		}
		else if (action.equalsIgnoreCase(Action.ACTION_PICK))
		{
			btnGalleryOk.setVisibility(View.VISIBLE);
			rightBtn2.setVisibility(View.VISIBLE);
			mBtnUpload.setVisibility(View.GONE);

			llBottomContainer.setVisibility(View.GONE);
			gridGallery.setOnItemClickListener(mItemSingleClickListener);
			adapter.setMultiplePick(false);
		}
		else if (action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK_TO_TEACHER_UPLOAD))
		{
			btnGalleryOk.setVisibility(View.GONE);
			rightBtn2.setVisibility(View.GONE);
			mBtnUpload.setVisibility(View.VISIBLE);
			setUploadNum();

			llBottomContainer.setVisibility(View.VISIBLE);
			gridGallery.setOnItemClickListener(mItemMulClickListener);
			adapter.setMultiplePick(true);

			mUploadType = TYPE_UPLOAD_FROM_TEACHER;
		}
		else if (action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK_TO_STUDENT_UPLOAD))
		{
			btnGalleryOk.setVisibility(View.GONE);
			rightBtn2.setVisibility(View.GONE);
			mBtnUpload.setVisibility(View.VISIBLE);
			setUploadNum();

			llBottomContainer.setVisibility(View.VISIBLE);
			gridGallery.setOnItemClickListener(mItemMulClickListener);
			adapter.setMultiplePick(true);

			mUploadType = TYPE_UPLOAD_FROM_STUDENT;
		}

		gridGallery.setAdapter(adapter);
		imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);

		new AsyncTask<Void, Void, List<Album>>()
		{

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				// showDialog();
			}

			@Override
			protected List<Album> doInBackground(Void... params)
			{
				return ImageUtils.getAlbums(CustomGalleryActivity.this);
			}

			@Override
			protected void onPostExecute(List<Album> result)
			{
				super.onPostExecute(result);

				initAlbum(result);
				checkImageStatus();
			}

		}.execute();

		if (ImageUtils.sImageItems.size() == 0)
		{
			rightBtn2.setEnabled(false);
			btnPre.setEnabled(false);
			mBtnUpload.setEnabled(false);
		}
		else
		{
			rightBtn2.setEnabled(true);
			btnPre.setEnabled(true);
			mBtnUpload.setEnabled(true);
		}
	}

	/**
	 * 
	 */
	private void initPopMenu()
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.gallery_actionsheet, null);
		// layout.setBackgroundResource(R.drawable.pop_bg);
		int height = DisplayUtil.getScreenHeight(CustomGalleryActivity.this) - WidgetController.getHeight(llBottomContainer) - WidgetController.getHeight(view_title) - getStatutsBarHeight();
		mPopupWindow = new PopupWindow(layout, LayoutParams.MATCH_PARENT, height + DisplayUtil.dip2px(getBaseContext(), 5));
		mPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		ListView galleryListView = (ListView) layout.findViewById(R.id.listView1);
		// LayoutParams params = (LayoutParams)
		// galleryListView.getLayoutParams();
		// int height = DisplayUtil.getScreenHeight(CustomGalleryActivity.this);
		// params.height = (int) (height*0.75);
		// galleryListView.setLayoutParams(params);

		mGalleryFolderAdapter = new GalleryFolderAdapter(CustomGalleryActivity.this, mAllAlbums);
		galleryListView.setAdapter(mGalleryFolderAdapter);

		galleryListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				mCurrentSelectedPos = position;
				key = (Album) parent.getAdapter().getItem(position);
				mGalleryFolderAdapter.updateCheck(key);
				loadByFloder(key);
				tv_floder.setText(key.getName());
				if (mPopupWindow != null && mPopupWindow.isShowing())
				{
					mPopupWindow.dismiss();
				}
			}
		});
		layout.setOnKeyListener(new View.OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if ((keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK) && event.getRepeatCount() == 0)
				{
					if (mPopupWindow != null && mPopupWindow.isShowing())
					{
						mPopupWindow.dismiss();
					}
				}
				return false;
			}
		});
		layout.setOnTouchListener(new OnTouchListener()
		{

			public boolean onTouch(View v, MotionEvent event)
			{
				View view = layout.findViewById(R.id.listView1);
				int x = (int) event.getX();
				int y = (int) event.getY();
				Rect viewRect = new Rect();
				view.getHitRect(viewRect);
				if (event.getAction() == MotionEvent.ACTION_UP)
				{
					if (!viewRect.contains(x, y))
					{
						if (mPopupWindow != null && mPopupWindow.isShowing())
						{
							mPopupWindow.dismiss();
						}
					}
				}
				return true;
			}
		});
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		mPopupWindow.update();
	}

	private int getStatutsBarHeight()
	{
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try
		{
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = getResources().getDimensionPixelSize(x);
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
		return sbar;
	}

	private void loadByFloder(Album floder)
	{
		mCustomGallery = floder.getImages();
		adapter.addAll(mCustomGallery);
	}

	private void checkImageStatus()
	{
		if (adapter.isEmpty())
		{
			imgNoMedia.setVisibility(View.VISIBLE);
		}
		else
		{
			imgNoMedia.setVisibility(View.GONE);
		}
	}

	OnClickListener mOkClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			List<ImageItem> selected = ImageUtils.sImageItems;

			if (selected.size() > limit)
			{
				Toast.makeText(getBaseContext(), getString(R.string.zone_choose_pic_num, limit), Toast.LENGTH_SHORT).show();
				return;
			}
			String[] allPath = new String[selected.size()];
			for (int i = 0; i < allPath.length; i++)
			{
				allPath[i] = selected.get(i).getPath();
			}

			Intent data = new Intent().putExtra("all_path", allPath);
			setResult(RESULT_OK, data);
			// showDialog(); //0414 wbt
			finish();

		}
	};
	OnItemClickListener mItemMulClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id)
		{
			// adapter.changeSelection(v, position);
			Intent intent = new Intent(CustomGalleryActivity.this, CustomGalleryPreActivity.class);
			intent.putExtra(Album.class.getSimpleName(), key);
			intent.putExtra("p", position);
			intent.putExtra("limit", limit);
			intent.putExtra("action", action);
			startActivityForResult(intent, 100);
		}
	};

	OnItemClickListener mItemSingleClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id)
		{
			ImageItem item = adapter.getItem(position);
			Intent data = new Intent().putExtra("single_path", item.getPath());
			setResult(RESULT_OK, data);
			finish();
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btnPre:
			Intent intent = new Intent(CustomGalleryActivity.this, CustomGalleryPreActivity.class);
			Album album = new Album();
			album.addImage(ImageUtils.sImageItems);
			intent.putExtra(Album.class.getSimpleName(), album);
			intent.putExtra("p", 0);
			intent.putExtra("limit", limit);
			startActivityForResult(intent, 100);
			break;
		case R.id.btnUpload:
			if (NetWorkUtil.isAvailable(this))
			{
				// upLoadPics();
			}
			else
			{
				Toast.makeText(this, R.string.network_offline, Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.layout_floder:
			showFloder();
			break;
		default:
			break;
		}
	}

	private int mCurrentUploadCount = 1;

	private String compressedPath(String path)
	{
		String newPath = path;
		photos.clear();
		photos.add(path);
		ArrayList<String> compressPath = CompressImage.compressedImagesPaths(photos);
		if (compressPath != null && compressPath.size() > 0)
		{
			newPath = compressPath.get(0);
		}

		return newPath;
	}

	private String getNextImgPath(String preImgPath)
	{
		int size = ImageUtils.sImageItems.size();
		for (int i = 0; i < size; i++)
		{
			ImageItem imageItem = ImageUtils.sImageItems.get(i);
			if (preImgPath.equals(imageItem.getPath()))
			{
				ImageUtils.sImageItems.remove(imageItem);
				break;
			}
		}

		if (ImageUtils.sImageItems.size() == 0)
		{
			return "";
		}

		String nextPath = ImageUtils.sImageItems.get(0).getPath();

		return nextPath;
	}

	private void sendStudentCommitSuccessMsg(String id)
	{
		Bundle bundle = new Bundle();
		bundle.putBoolean("SubmitSuccess", true);
		bundle.putString("ResId", id);
		EventBus.getDefault().post(bundle);
	}

	@Override
	public void onBackPressed()
	{
		if (mPopupWindow != null && mPopupWindow.isShowing())
		{
			mPopupWindow.dismiss();
		}
		else
		{
			finish();
		}
	}

	private PopupWindow mPopupWindow;

	/**
	 * 
	 */
	private void showFloder()
	{
		int height = WidgetController.getHeight(llBottomContainer);
		int[] location = new int[2];
		llBottomContainer.getLocationOnScreen(location);
		mPopupWindow.showAtLocation(llBottomContainer, Gravity.NO_GRAVITY, location[0], location[1] - mPopupWindow.getHeight());
	}

	private void initAlbum(List<Album> result)
	{
		List<Album> albums = result;
		Album allAlbum = new Album();
		allAlbum.setId("-1");
		allAlbum.setName("全部相册");
		allAlbum.setCheck(true);
		List<ImageItem> imageItems = new ArrayList<ImageItem>();
		for (int i = 0; i < albums.size(); i++)
		{
			Album album = albums.get(i);
			imageItems.addAll(album.getImages());
			Collections.reverse(album.getImages());
		}
		Collections.reverse(imageItems);
		allAlbum.addImage(imageItems);

		key = allAlbum;

		mAllAlbums = new ArrayList<Album>();
		mAllAlbums.add(allAlbum);
		Collections.reverse(albums);
		mAllAlbums.addAll(albums);

		loadByFloder(key);
		initPopMenu();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK && requestCode == 100)
		{
			boolean complete = intent.getBooleanExtra("complete", false);
			updateAdapter();
			if (complete)
			{
				List<ImageItem> selected = ImageUtils.sImageItems;
				String[] allPath = new String[selected.size()];
				for (int i = 0; i < allPath.length; i++)
				{
					allPath[i] = selected.get(i).getPath();
				}
				Intent data = new Intent().putExtra("all_path", allPath);
				setResult(RESULT_OK, data);
				finish();
			}
			else
			{
				rightBtn2.setText("完成(" + ImageUtils.sImageItems.size() + "/" + limit + ")");
				setUploadNum();
			}
		}
	}

	/**
	 * 
	 */
	private void updateAdapter()
	{
		adapter.selectAll(false);
		for (int i = 0; i < ImageUtils.sImageItems.size(); i++)
		{
			ImageItem imageItem = ImageUtils.sImageItems.get(i);
			adapter.updateSelection(imageItem);
		}
		adapter.notifyDataSetChanged();

	}

	class GalleryFolderAdapter extends ArrayAdapter<Album>
	{

		private LayoutInflater mInflater;

		/**
		 * @param context
		 */
		public GalleryFolderAdapter(Context context, List<Album> album)
		{
			super(context, 0, album);
			mInflater = LayoutInflater.from(context);
		}

		public void updateCheck(Album folder)
		{
			for (int i = 0; i < getCount(); i++)
			{
				Album album = getItem(i);
				if (album.getId().equals(folder.getId()))
				{
					album.setCheck(true);
				}
				else
				{
					album.setCheck(false);
				}
			}
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			Album album = getItem(position);
			if (null == convertView)
			{
				convertView = mInflater.inflate(R.layout.item_gallery_pick, null);
			}
			ImageView iv_logo = ViewHolder.get(convertView, R.id.iv_logo);
			TextView tv_folder_name = ViewHolder.get(convertView, R.id.tv_folder_name);
			ImageView iv_check = ViewHolder.get(convertView, R.id.iv_check);
			try
			{
				ImageLoader.getInstance().displayImage("file://" + album.getThumbPath(), iv_logo, ImageUtils.displayOptions());
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			tv_folder_name.setText(album.getName());
			if (album.isCheck())
			{
				iv_check.setImageResource(R.drawable.icon_folder_check);
			}
			else
			{
				iv_check.setImageResource(R.color.transparent);
			}
			return convertView;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.whty.app.eyu.pick.GalleryAdapter.OnItemCheckListener#onCheck(int,
	 * net.whty.app.eyu.pick.ImageItem)
	 */
	@Override
	public void onCheck(int position, ImageItem imageItem)
	{
		if (ImageUtils.sImageItems.contains(imageItem))
		{
			ImageUtils.sImageItems.remove(imageItem);
			if (ImageUtils.sImageItems.size() == 0)
			{
				rightBtn2.setEnabled(false);
				btnPre.setEnabled(false);
				mBtnUpload.setEnabled(false);
			}
		}
		else
		{
			if (ImageUtils.sImageItems.size() >= limit)
			{
				Toast.makeText(getBaseContext(), getString(R.string.zone_choose_pic_num, limit), Toast.LENGTH_SHORT).show();
				return;
			}
			ImageUtils.sImageItems.add(imageItem);
			if (ImageUtils.sImageItems.size() > 0)
			{
				rightBtn2.setEnabled(true);
				btnPre.setEnabled(true);
				mBtnUpload.setEnabled(true);
			}
		}
		// adapter.changeSelection(position);
		if (ImageUtils.sImageItems.size() == 0)
		{
			rightBtn2.setText("完成");
		}
		else
		{
			rightBtn2.setText("完成(" + ImageUtils.sImageItems.size() + "/" + limit + ")");
		}
		setUploadNum();
	}

	private void setUploadNum()
	{
		mBtnUpload.setText("上传(" + ImageUtils.sImageItems.size() + "/" + limit + ")");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			keyBackClicked();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void keyBackClicked()
	{
		if (mLoading)
		{
			// showInterruptDialog();
		}
		else if (mCurrentSelectedPos != 0)
		{
			goBackAllGallery();
		}
		else
		{
			finish();
		}
	}

	private void goBackAllGallery()
	{
		key = (Album) mGalleryFolderAdapter.getItem(0);
		mGalleryFolderAdapter.updateCheck(key);
		loadByFloder(key);
		tv_floder.setText(key.getName());
		if (mPopupWindow != null && mPopupWindow.isShowing())
		{
			mPopupWindow.dismiss();
		}
		mCurrentSelectedPos = 0;
	}

}
