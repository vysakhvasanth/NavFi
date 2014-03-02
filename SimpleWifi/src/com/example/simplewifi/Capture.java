/*
 * Surface view class
 * All drawing and UI is done in this class
 * 
 * written by Vas
 * */
package com.example.simplewifi;

import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import com.example.ToolBox.compassData;
import com.example.ToolBox.inventory;
import com.example.ToolBox.wifiData;

public class Capture extends SurfaceView implements SurfaceHolder.Callback,
		OnTouchListener {

	SurfaceHolder holder;
	private int tmp = 1;
	private PaintThread draw;
	private Bitmap arrow;
	public Bitmap map;
	final int maxHeight = 1720;
	final int maxWidth = 1080;
	Bitmap mapscale;

	private Context ctx;
	private Bitmap cp_map;
	public Canvas ca;
	private PointF latest;
	public static float x;
	public static float y;
	private WifiManager wify;
	public wifiData wifidata;
	private boolean wifi_registered = false;
	public static boolean scan_end = false;
	private boolean data_proc = false;
	private AlertDialog.Builder dialogStart, dialogEnd;

	public Capture(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctx = context;
		this.setOnTouchListener(this);

		holder = getHolder();
		holder.addCallback(this);
		map = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.mp);
		arrow = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.arrow);

		// hook up wifi sensor
		wify = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
		wifidata = new wifiData(wify, context);

		// initialize AlertDialogs
		createStartDialog();
		createStopDailog();

	}

	/*
	 * Draw everything here; make other classes accessible to this
	 */
	@Override
	public void onDraw(Canvas c) {

		this.ca = c;

		drawMap(c);
		drawArrow(c);
		c.drawText(String.valueOf(compassData.getCompassValue()), 50, 50,
				inventory.text50());
		drawPoints(inventory.drawnpoints, c);

	}

	// plot the rectangle around areas scanned
	public void drawPoints(List<PointF> points, Canvas c) {

		// loop through points and plot circles
		if (points != null && points.size() != 0) {

			int size = points.size();

			for (int i = 0; i <= (size - 1); i++) {

				float px = points.get(i).x;
				float py = points.get(i).y;

				// c.drawCircle(points.get(i).x, points.get(i).y, 10, pa);
				RectF r = new RectF();
				r.set(px, py, px + 40.0f, py + 40.0f);
				c.drawRect(r, inventory.redPaint());

				// draw line; draw from previous to current
				if (size > 1 && i != 0) {
					float x = points.get(i - 1).x;
					float y = points.get(i - 1).y;
					c.drawLine(x, y, points.get(i).x, points.get(i).y,
							inventory.bluePaint());

					if (scan_end) {
						// midpoint
						float xmid = (points.get(i).x + x) / 2;
						float ymid = (points.get(i).y + y) / 2;
						c.drawText("A" + String.valueOf(inventory.returnID()),
								xmid, ymid, inventory.text50MAG());
					}

				}
			}

		}
	}

	// draw the arrow position
	private void drawArrow(Canvas c) {

		Matrix m = new Matrix();
		m = new Matrix();
		m.setRotate((float) (compassData.getCompassValue()),
				arrow.getWidth() / 2.0f, arrow.getHeight() / 2.0f);

		m.postTranslate(inventory.X - arrow.getWidth() / 2.0f, inventory.Y
				- arrow.getHeight() / 2.0f);
		c.drawBitmap(arrow, m, null);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		/* empty */

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		draw = new PaintThread(holder, this);
		draw.setRun(true);
		draw.start();
		setWillNotDraw(false);// this is done to prevent the surfaceview from
								// ignoring postinvalidate() call

	}

	void drawMap(Canvas c) {

		/* Reload map if its when user resumes app from pause */
		if (map == null) {

			map = BitmapFactory.decodeResource(ctx.getResources(),
					R.drawable.mp);

		}
		mapscale = Bitmap.createScaledBitmap(map, maxWidth, maxHeight, true);
		c.drawBitmap(mapscale, 0, 0, null);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		boolean retry = true;
		draw.setRun(false);
		map = null;

		while (retry) {
			try {
				draw.join();
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x = event.getX();
			y = event.getY();
			if (!data_proc) {
				AlertDialog dg = dialogStart.create();
				dg.setMessage("@ x:" + x + " y:" + y);
				dg.show();
			} else {
				AlertDialog dg = dialogEnd.create();
				dg.setMessage("@ x:" + x + " y:" + y);
				dg.show();
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		}
		return true;
	}

	/* Start dialouge box when user press on screen */
	public void createStartDialog() {

		dialogStart = new AlertDialog.Builder(ctx);
		dialogStart.setTitle("Start...");
		dialogStart.setCancelable(true);

		dialogStart.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						// call to record data
						registerReciever();

						// add the points to the list
						inventory.drawnpoints.add(new PointF(x, y));
						wifi_registered = true;
						data_proc = true;
						scan_end = false;
					}
				});

		dialogStart.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				});

		dialogStart.setIcon(R.drawable.ic_launcher);

	}

	public void createStopDailog() {

		dialogEnd = new AlertDialog.Builder(ctx);
		dialogEnd.setTitle("Stop reading data...");
		dialogEnd.setCancelable(true);

		dialogEnd.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						inventory.toast("Stopped reading...", ctx, false);

						// call to stop data if registered
						// if (wifi_registered) {}
						inventory.drawnpoints.add(new PointF(x, y));
						unregisterReciever();
						data_proc = false;
						scan_end = true;
						wifidata.saveData();
					}
				});

		dialogEnd.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				});

		dialogEnd.setIcon(R.drawable.ic_launcher);
	}

	// register wifi function
	public void registerReciever() {
		ctx.registerReceiver(wifidata, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wify.startScan();
	}

	// unregister wifi function
	public void unregisterReciever() {
		ctx.unregisterReceiver(wifidata);// stops reading data
	}

	public void writeMappingtoFile() {
		try {
			wifidata.writetofile(inventory.locationPoints);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
