package com.example.ToolBox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.simplewifi.R;
import com.example.simplewifi.R.drawable;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class own_snippet extends SurfaceView implements Runnable {

	final int maxHeight = 1720;
	final int maxWidth = 1080;
	SurfaceHolder mholder;
	Bitmap map;
	public float tx = 0;
	public float ty = 0;
	boolean isrunning;
	boolean data_proc = false;
	Thread t = null;
	Context ctx = null;
	WifiManager wm;
	WifiReceiver wr;
	File file;

	enum State {
		mapping, navigate
	}

	AlertDialog.Builder builder, builder2;
	State st = State.mapping; // for recording data or using map

	public own_snippet(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctx = context;
		map = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.mp);
		mholder = getHolder();
		if (new File("sdcard/slog.txt").exists())
			new File("sdcard/slog.txt").delete();
		startdialogCreate(context);
		enddialogCreate(context);

	}

	// public Map(Context context) {
	//
	// }

	public void startdialogCreate(final Context context) {

		builder = new AlertDialog.Builder(context);
		builder.setTitle("Start...");
		builder.setCancelable(true);

		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				// call to record data
				wm = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				wr = new WifiReceiver(wm);
				context.registerReceiver(wr, new IntentFilter(
						WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				Log.i("FingerPrint", "Registered WifiReceiver");
				wm.startScan();

				data_proc = true;
			}
		});

		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();

			}
		});

		builder.setIcon(R.drawable.ic_launcher);

	}

	public void enddialogCreate(final Context context) {

		builder2 = new AlertDialog.Builder(context);
		builder2.setTitle("Stop reading data...");
		builder2.setCancelable(true);

		builder2.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						Toast.makeText(context, "Saving data...",
								Toast.LENGTH_LONG).show();
						// call to stop data
						context.unregisterReceiver(wr);
						data_proc = false;
					}
				});

		builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();

			}
		});

		builder2.setIcon(R.drawable.ic_launcher);
	}

	public void resume() {
		isrunning = true;
		t = new Thread(this);
		t.start();
	}

	public void pause() {

		boolean retry = true;
		isrunning = false;

		while (retry) {
			try {
				t.join();
				retry = false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public void run() {

		while (isrunning) {
			if (mholder.getSurface().isValid()) {

				Canvas c = mholder.lockCanvas();
				drawMap(c);
				c.drawText(tx + ", " + ty, 50, 50, new Paint());
				c.drawCircle(tx, ty, 10, new Paint());
				mholder.unlockCanvasAndPost(c);

			}
		}
	}

	public void drawMap(final Canvas c) {

		Bitmap mapscale = Bitmap.createScaledBitmap(map, maxWidth, maxHeight,
				true);
		c.drawBitmap(mapscale, 0, 0, null);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		tx = event.getX();
		ty = event.getY();

		if (st == State.mapping) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				builder.setMessage("@ x:" + tx + " y:" + ty);
				builder2.setMessage("@ x:" + tx + " y:" + ty);
				break;
			case MotionEvent.ACTION_UP:
				if (!data_proc) {
					AlertDialog dg = builder.create();
					builder.setMessage("@ x:" + tx + " y:" + ty);
					dg.show();
				} else {
					AlertDialog dg = builder2.create();
					builder2.setMessage("@ x:" + tx + " y:" + ty);
					dg.show();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			default:
				break;
			}
		}
		return true;

	}

	/* Handling the wifi mesurments */
	public class WifiReceiver extends BroadcastReceiver {

		WifiManager wify;

		List<String> ssid = new ArrayList<String>();

		public WifiReceiver(WifiManager wm01) {
			this.wify = wm01;

		}

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			List<ScanResult> results = wify.getScanResults();
			Toast.makeText(arg0, "Reading data...", Toast.LENGTH_LONG).show();

			for (ScanResult result : results) {
				Write(result.SSID, result.level);
			}

			wify.startScan();
		}

		private void Write(String ssid, int level) {

			file = new File("sdcard/slog.txt");

			if (!file.exists()) {
				try {
					file.createNewFile();

				} catch (Exception e) {

					e.printStackTrace();
				}
			}

			try {

				// bufferedwriter for performance
				BufferedWriter buf = new BufferedWriter(new FileWriter(file,
						true));

				buf.append(ssid + ": " + level);
				buf.newLine();
				buf.close();

			} catch (IOException e) {

				e.printStackTrace();
			}

		}

	}

}

// write object
// OutputStream file;
// try {
// file = new FileOutputStream("sdcard/file.ser");
// OutputStream buffer = new BufferedOutputStream(file);
// ObjectOutput output = new ObjectOutputStream(buffer);
// try {
// output.writeObject(data);
// } finally {
// output.close();
//
// }
// return true;
// } catch (IOException e) {
// e.printStackTrace();
// }

// void dontneed() {
// // turn the map to the compass
// Matrix mapm = new Matrix();
// mapm.setRotate((float) (compassData.getCompassValue()),
// this.getWidth() / 2.0f, this.getWidth() / 2.0f);
// mapm.postTranslate(150 - mapscale.getWidth() / 2.0f,
// 150 - mapscale.getHeight() / 2.0f);
// c.drawBitmap(mapscale, mapm, null);

// @Override
// public boolean onTouchEvent(MotionEvent event) {
//
// switch (event.getAction()) {
// case MotionEvent.ACTION_DOWN:
// x = event.getX();
// y = event.getY();
// break;
// case MotionEvent.ACTION_MOVE:
// break;
// case MotionEvent.ACTION_UP:
// x = event.getX();
// y = event.getY();
// break;
// case MotionEvent.ACTION_CANCEL:
// break;
// default:
// break;
// }
//
// return true;
// }
// }