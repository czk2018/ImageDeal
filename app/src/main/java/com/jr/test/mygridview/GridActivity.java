package com.jr.test.mygridview;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jr.R;
import com.jr.test.mygridview.adapter.DragAdapter;
import com.jr.test.mygridview.view.DragGrid;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class GridActivity extends Activity implements OnItemClickListener {
	/** 用户栏目的GRIDVIEW */
	private DragGrid userGridView;
	/** 用户栏目列表 */
	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	/** 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。 */
	boolean isMove = false;
	/** 用户栏目对应的适配器，可以拖动 */
	DragAdapter userAdapter;

	boolean isEditState = false;
	Button bt_change;
	private String[] ursl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid);
		ursl = Photoinit();
		initData();
		initView();

	}

	private String[] Photoinit() {
		String[] urls = {

				"http://tabletpcssource.com/wp-content/uploads/2011/05/android-logo.png",
				"http://simpozia.com/pages/images/stories/windows-icon.png",
				"http://radiotray.sourceforge.net/radio.png",
				"http://www.bandwidthblog.com/wp-content/uploads/2011/11/twitter-logo.png",
				"http://weloveicons.s3.amazonaws.com/icons/100907_itunes1.png",
				"http://weloveicons.s3.amazonaws.com/icons/100929_applications.png",
				"http://www.idyllicmusic.com/index_files/get_apple-iphone.png",
				"http://www.frenchrevolutionfood.com/wp-content/uploads/2009/04/Twitter-Bird.png",
				"http://3.bp.blogspot.com/-ka5MiRGJ_S4/TdD9OoF6bmI/AAAAAAAAE8k/7ydKtptUtSg/s1600/Google_Sky%2BMaps_Android.png",
				"http://www.desiredsoft.com/images/icon_webhosting.png",
				"http://goodereader.com/apps/wp-content/uploads/downloads/thumbnails/2012/01/hi-256-0-99dda8c730196ab93c67f0659d5b8489abdeb977.png",
				"http://1.bp.blogspot.com/-mlaJ4p_3rBU/TdD9OWxN8II/AAAAAAAAE8U/xyynWwr3_4Q/s1600/antivitus_free.png",
				"http://cdn3.iconfinder.com/data/icons/transformers/computer.png",
				"http://cdn.geekwire.com/wp-content/uploads/2011/04/firefox.png?7794fe",
				"https://ssl.gstatic.com/android/market/com.rovio.angrybirdsseasons/hi-256-9-347dae230614238a639d21508ae492302340b2ba",
				"http://androidblaze.com/wp-content/uploads/2011/12/tablet-pc-256x256.jpg",
				"http://www.theblaze.com/wp-content/uploads/2011/08/Apple.png",
				"http://1.bp.blogspot.com/-y-HQwQ4Kuu0/TdD9_iKIY7I/AAAAAAAAE88/3G4xiclDZD0/s1600/Twitter_Android.png",
				"http://3.bp.blogspot.com/-nAf4IMJGpc8/TdD9OGNUHHI/AAAAAAAAE8E/VM9yU_lIgZ4/s1600/Adobe%2BReader_Android.png",
				"http://cdn.geekwire.com/wp-content/uploads/2011/05/oovoo-android.png?7794fe",
				"http://icons.iconarchive.com/icons/kocco/ndroid/128/android-market-2-icon.png",
				"http://thecustomizewindows.com/wp-content/uploads/2011/11/Nicest-Android-Live-Wallpapers.png",
				"http://c.wrzuta.pl/wm16596/a32f1a47002ab3a949afeb4f",
				"http://macprovid.vo.llnwd.net/o43/hub/media/1090/6882/01_headline_Muse.jpg",
				// Special cases
				"http://cdn.urbanislandz.com/wp-content/uploads/2011/10/MMSposter-large.jpg", // Very
																								// large
																								// image
				"http://www.ioncannon.net/wp-content/uploads/2011/06/test9.webp", // WebP
																					// image
				"http://4.bp.blogspot.com/-LEvwF87bbyU/Uicaskm-g6I/AAAAAAAAZ2c/V-WZZAvFg5I/s800/Pesto+Guacamole+500w+0268.jpg", // Image
																																// with
																																// "Mark has been invalidated"
																																// problem
				"http://upload.wikimedia.org/wikipedia/ru/b/b6/Как_кот_с_мышами_воевал.png", // Link
																								// with
																								// UTF-8
				"https://www.eff.org/sites/default/files/chrome150_0.jpg", // Image
																			// from
																			// HTTPS
				"http://bit.ly/soBiXr", // Redirect link
				"http://img001.us.expono.com/100001/100001-1bc30-2d736f_m.jpg", // EXIF
				"", // Empty link
				"http://wrong.site.com/corruptedLink", // Wrong link };
		};

		return urls;
	}

	private void initView() {
		userGridView = (DragGrid) findViewById(R.id.userGridView);
		// userAdapter = new DragAdapter(this, userChannelList);
		userAdapter = new DragAdapter(this, list);
		userGridView.setAdapter(userAdapter);
		userGridView.setOnItemClickListener(this);

		userGridView.setOnChangeListener(new DragGrid.OnChanageListener() {

			@Override
			public void isEdit(boolean isEdit) {
				if (isEdit) {
					Toast.makeText(getApplicationContext(), "进入编辑模式",
							Toast.LENGTH_SHORT).show();
					setEditis(true);
				}

			}
		});

		bt_change = (Button) findViewById(R.id.bt_change);
		bt_change.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isEditState) {
					setEditis(false);
				} else {
					setEditis(true);
				}
			}
		});
	}

	private void initData() {
		list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < 20; i++) {
			HashMap map = new HashMap<String, String>();
			map.put("name", "位置" + i);
			map.put("url", ursl[i]);
			list.add(map);
		}
		ImageInit();
	}

	private void ImageInit() {
		DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.chat_voice_call_normal) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.chat_voice_call_normal) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.chat_voice_call_normal) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory().cacheOnDisc().build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, final View view,
			final int position, long id) {
	}

	/**
	 * 点击ITEM移动动画
	 * 
	 * @param moveView
	 * @param startLocation
	 * @param endLocation
	 * @param moveChannel
	 * @param clickGridView
	 */
	private void MoveAnim(View moveView, int[] startLocation,
			int[] endLocation, final GridView clickGridView) {
		int[] initLocation = new int[2];
		// 获取传递过来的VIEW的坐标
		moveView.getLocationInWindow(initLocation);
		// 得到要移动的VIEW,并放入对应的容器中
		final ViewGroup moveViewGroup = getMoveViewGroup();
		final View mMoveView = getMoveView(moveViewGroup, moveView,
				initLocation);
		// 创建移动动画
		TranslateAnimation moveAnimation = new TranslateAnimation(
				startLocation[0], endLocation[0], startLocation[1],
				endLocation[1]);
		moveAnimation.setDuration(100L);// 动画时间
		// 动画配置
		AnimationSet moveAnimationSet = new AnimationSet(true);
		moveAnimationSet.setFillAfter(false);// 动画效果执行完毕后，View对象不保留在终止的位置
		moveAnimationSet.addAnimation(moveAnimation);
		mMoveView.startAnimation(moveAnimationSet);
		moveAnimationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				isMove = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				moveViewGroup.removeView(mMoveView);
				isMove = false;
			}
		});
	}

	/**
	 * 创建移动的ITEM对应的ViewGroup布局容器
	 */
	private ViewGroup getMoveViewGroup() {
		ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
		LinearLayout moveLinearLayout = new LinearLayout(this);
		moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		moveViewGroup.addView(moveLinearLayout);
		return moveLinearLayout;
	}

	/**
	 * 获取点击的Item的对应View，
	 * 
	 * @param view
	 * @return
	 */
	private ImageView getView(View view) {
		view.destroyDrawingCache();
		view.setDrawingCacheEnabled(true);
		Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(false);
		ImageView iv = new ImageView(this);
		iv.setImageBitmap(cache);
		return iv;
	}

	/**
	 * 获取移动的VIEW，放入对应ViewGroup布局容器
	 * 
	 * @param viewGroup
	 * @param view
	 * @param initLocation
	 * @return
	 */
	private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
		int x = initLocation[0];
		int y = initLocation[1];
		viewGroup.addView(view);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLayoutParams.leftMargin = x;
		mLayoutParams.topMargin = y;
		view.setLayoutParams(mLayoutParams);
		return view;
	}

	/**
	 * 设置是否可编辑
	 * 
	 * @param b
	 */
	public void setEditis(boolean b) {
		if (b) {
			isEditState = true;
			userGridView.setEditState(true);
			userAdapter.setEdit(true);
		} else {
			userGridView.setEditState(false);
			userAdapter.setEdit(false);
			isEditState = false;
		}
	}

	@Override
	public void onBackPressed() {
		if (isEditState) {
			Toast.makeText(getApplicationContext(), "退出编辑模式",
					Toast.LENGTH_SHORT).show();
			setEditis(false);
		} else {
			finish();
		}
	}
}
