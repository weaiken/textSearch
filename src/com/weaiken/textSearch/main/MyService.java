package com.weaiken.textSearch.main;

import java.util.Timer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.weaiken.textSearch.R;
import com.weaiken.textSearch.main.Request.Response;
import com.weaiken.textSearch.util.StringUtil;
import com.weaiken.textSearch.widget.ToastFV;

public class MyService extends Service {

	ClipboardManager mClipboardManager;
	Context mContext;
	Timer mTimer;
	WindowManager mWindowManager;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = this;

		initFloatWindows();

		/**
		 * 以下是剪切板复制翻译功能
		 */
		ToastFV.init(mContext);
		mClipboardManager = (ClipboardManager) getSystemService(this.CLIPBOARD_SERVICE);
		mClipboardManager.addPrimaryClipChangedListener(new OnPrimaryClipChangedListener() {

			@Override
			public void onPrimaryClipChanged() {
				// TODO Auto-generated method stub
				String clipBoardString = mClipboardManager.getText().toString().trim();
				if (!TextUtils.isEmpty(clipBoardString) && !clipBoardString.equals(saveString) && StringUtil.isAllEnglish(clipBoardString)) {
					saveString = clipBoardString;
					new Request().request(clipBoardString, new Response() {

						@Override
						public void success(String msg) {
							// TODO Auto-generated method stub
							Message message = Message.obtain();
							message.what = 0;
							message.obj = msg;
							mHandler.sendMessage(message);

						}

						@Override
						public void fail(int errorNum, String msg) {
							// TODO Auto-generated method stub
							Message message = Message.obtain();
							message.what = errorNum;
							message.obj = msg;
							mHandler.sendMessage(message);
						}
					});

				}
			}
		});
		// mTimer = new Timer();
		// mTimer.schedule(mTimerTask, 200);

	}

	View floatManagerView;
	WindowManager.LayoutParams mLayoutParams;

	private void initFloatWindows() {
		// TODO Auto-generated method stub
		mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		floatManagerView = LayoutInflater.from(mContext).inflate(R.layout.layout_float_window, null);

		// int viewWidth = floatManagerView.getLayoutParams().width;
		// int viewHeight = floatManagerView.getLayoutParams().height;

		floatManagerView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				/* 开启Pictures画面Type设定为image */
				intent.setType("image/*");
				/* 使用Intent.ACTION_GET_CONTENT这个Action */
				intent.setAction(Intent.ACTION_GET_CONTENT);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				/* 取得相片后返回本画面 */
				mContext.startActivity(intent);
			}
		});

		DisplayMetrics metrics = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(metrics);

		mLayoutParams = new WindowManager.LayoutParams();
		mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		mLayoutParams.format = PixelFormat.RGBA_8888;
		mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		mLayoutParams.width = 120;
		mLayoutParams.height = 120;
		mLayoutParams.x = metrics.widthPixels;
		mLayoutParams.y = metrics.heightPixels / 2 - (int) (105 * metrics.density);

		mWindowManager.addView(floatManagerView, mLayoutParams);

	}
	
	
	
	

	// TimerTask mTimerTask = new TimerTask() {
	// @SuppressLint("NewApi")
	// @Override
	// public void run() {
	//
	// while (true) {
	// // Log.e("mClipboardManager.getText()",
	// // mClipboardManager.getText().toString() );
	// // Log.e("mClipboardManager.mClipboardManager.getText()()",
	// // mClipboardManager.getPrimaryClip().toString());
	//
	// String clipBoardString = mClipboardManager.getText().toString().trim();
	// if (!TextUtils.isEmpty(clipBoardString) &&
	// !clipBoardString.equals(saveString) &&
	// StringUtil.isAllEnglish(clipBoardString)) {
	// saveString = clipBoardString;
	// new Request().request(clipBoardString, new Response() {
	//
	// @Override
	// public void success(String msg) {
	// // TODO Auto-generated method stub
	// Message message = Message.obtain();
	// message.what = 0;
	// message.obj = msg;
	// mHandler.sendMessage(message);
	//
	// }
	//
	// @Override
	// public void fail(int errorNum, String msg) {
	// // TODO Auto-generated method stub
	// Message message = Message.obtain();
	// message.what = errorNum;
	// message.obj = msg;
	// mHandler.sendMessage(message);
	// }
	// });
	//
	//
	//
	// }
	// try {
	// Thread.sleep(200);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// }
	// };

	String saveString;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				// Log.e("saveString", msg.obj + "");
				ToastFV.show(msg.obj + "");
			} else {
				ToastFV.show("request fail :" + msg.obj + msg.what);
			}
		}
	};

	// PopupWindow popupWindow;
	// private void showPopUp(View v) {
	// LinearLayout layout = new LinearLayout(this);
	// layout.setBackgroundColor(Color.GRAY);
	// TextView tv = new TextView(this);
	// tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
	// LayoutParams.WRAP_CONTENT));
	// tv.setText("I'm a pop -----------------------------!");
	// tv.setTextColor(Color.WHITE);
	// layout.addView(tv);
	//
	// popupWindow = new PopupWindow(layout,120,120);
	//
	// popupWindow.setFocusable(true);
	// popupWindow.setOutsideTouchable(true);
	// popupWindow.setBackgroundDrawable(new BitmapDrawable());
	//
	// int[] location = new int[2];
	// v.getLocationOnScreen(location);
	// popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0],
	// location[1]-popupWindow.getHeight());
	// }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
