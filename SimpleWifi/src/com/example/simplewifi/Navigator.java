package com.example.simplewifi;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.ToolBox.acceleratorData;
import com.example.ToolBox.compassData;
import com.example.ToolBox.inventory;
import com.example.stepDetection.StepTrigger;

/*
 * Class implements path finding and algorithms for 
 * positioning. Need to do acc & gyroscope measurements to 
 * calculate user movement. 
 * 
 * Disect footpath application to make use of its
 * step counter
 * */

public class Navigator extends SurfaceView implements SurfaceHolder.Callback,
		StepTrigger {

	private PaintThread painter;
	private Bitmap map;
	private SurfaceHolder holder;
	private Context ctx;
	private wifiSampler wifiSamp;
	private WifiManager wify;
	private Bitmap arrow;

	public Navigator(Context context, AttributeSet attr) {
		super(context, attr);
		this.ctx = context;
		this.holder = getHolder();
		holder.addCallback(this); // register event to the
									// SurfaceHolder.Callback
		map = inventory.getMap(context);
		arrow = inventory.getArrow(context);

		// hook up wifi sensor
		wify = (WifiManager) context.getSystemService(ctx.WIFI_SERVICE);
		wifiSamp = new wifiSampler(wify, context);

		// registerReciever();

		acceleratorData accdata = new acceleratorData(context);
		accdata.load();

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// call the painter thread
		painter = new PaintThread(holder, this);
		painter.setRun(true);
		painter.start();
		setWillNotDraw(false);// this is done to prevent the surfaceview
								// from ignoring postinvalidate() call

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		boolean retry = true;
		painter.setRun(false);
		map = null;

		while (retry) {
			try {
				painter.join();
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
		}
	}

	@Override
	public void onDraw(Canvas c) {

		drawMap(c);
		drawArrow(c);

		c.drawText("Steps :" + acceleratorData.stepCounter, 50, 1500,
				inventory.text50BLUE());
		c.drawText("Acc data X:" + acceleratorData.lastAcc[0] + ", ", 50, 1550,
				inventory.text50MAG());
		c.drawText("Acc data Y:" + acceleratorData.lastAcc[1] + ", ", 50, 1600,
				inventory.text50MAG());
		c.drawText("Acc data Z:" + acceleratorData.lastAcc[2] + ", ", 50, 1650,
				inventory.text50());
	}

	void drawMap(Canvas c) {
		if (map == null) {

			map = inventory.getMap(ctx);
		}

		c.drawBitmap(map, 0, 0, null);
	}

	void drawArrow(Canvas c) {

		// log values of compass sensor
		c.drawText(String.valueOf(compassData.getCompassValue()), 50, 50,
				inventory.text50());

		Matrix m = new Matrix();
		m.setRotate((float) (compassData.getCompassValue()),
				arrow.getWidth() / 2.0f, arrow.getHeight() / 2.0f);

		m.postTranslate(inventory.X - arrow.getWidth() / 2.0f, inventory.Y
				- arrow.getHeight() / 2.0f);
		c.drawBitmap(arrow, m, null);

	}

	// register wifi function
	public void registerReciever() {
		ctx.registerReceiver(wifiSamp, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wify.startScan();
	}

	// unregister wifi function
	public void unregisterReciever() {
		ctx.unregisterReceiver(wifiSamp);// stops reading data
	}

	@Override
	public void trigger(long now_ms, double compDir) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dataHookAcc(long now_ms, double x, double y, double z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dataHookComp(long now_ms, double x, double y, double z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void timedDataHook(long now_ms, double[] acc, double[] comp) {
		// TODO Auto-generated method stub

	}
}

class wifiSampler extends BroadcastReceiver {

	int Strongest;
	String StrongestAP;
	Context ctx;
	WifiManager wm;

	public wifiSampler(WifiManager wifi, Context context) {
		this.wm = wifi;
		this.ctx = context;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		List<ScanResult> results = wm.getScanResults();
		inventory.toast("Reading data", context, true);
		for (ScanResult result : results) {

			// compute the strongest ap
			if (Math.abs(result.level) > Strongest) {
				Strongest = result.level;
				StrongestAP = result.BSSID;
			}
		}

		wm.startScan();
	}

}
