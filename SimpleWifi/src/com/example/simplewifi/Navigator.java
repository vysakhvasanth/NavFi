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

import com.example.ToolBox.Inventory;
import com.example.ToolBox.SensorData;
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
	SensorData sensorData;

	public Navigator(Context context, AttributeSet attr) {
		super(context, attr);
		this.ctx = context;
		this.holder = getHolder();
		holder.addCallback(this); // register event to the
									// SurfaceHolder.Callback
		map = Inventory.getMap(context);
		arrow = Inventory.getArrow(context);

		// hook up wifi sensor
		wify = (WifiManager) context.getSystemService(ctx.WIFI_SERVICE);
		wifiSamp = new wifiSampler(wify, context);

		// register accelerometer & magnetic sensor;
		sensorData = new SensorData(context);
		sensorData.loadAcc();
		sensorData.loadMagnetic();
		sensorData.loadCompass();

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
		if (sensorData != null) {
			sensorData.unloadAcc();
			sensorData.unloadComp();
			sensorData.unloadMagnetic();
		}

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

		c.drawText("Steps :" + SensorData.stepCounter, 50, 1500,
				Inventory.text50BLUE());
		c.drawText("Acc data X:" + SensorData.lastAcc[0] + ", ", 50, 1550,
				Inventory.text50MAG());
		c.drawText("Acc data Y:" + SensorData.lastAcc[1] + ", ", 50, 1600,
				Inventory.text50MAG());
		c.drawText("Acc data Z:" + SensorData.lastAcc[2] + ", ", 50, 1650,
				Inventory.text50());
	}

	void drawMap(Canvas c) {
		if (map == null) {

			map = Inventory.getMap(ctx);
		}

		c.drawBitmap(map, 0, 0, null);
	}

	void drawArrow(Canvas c) {

		// log values of sensor s
		c.drawText(String.valueOf(SensorData.getCompassValue()), 50, 50,
				Inventory.text50());
		c.drawText(String.valueOf(SensorData.getMagneticValue()), 50, 100,
				Inventory.text50());
		c.drawText("X: " + String.valueOf(Inventory.X), 50, 150,
				Inventory.text50BLUE());
		c.drawText("Y: " + String.valueOf(Inventory.Y), 50, 250,
				Inventory.text50BLUE());

		// setting boundary
		if (Inventory.X >= 1080)
			Inventory.X = 1080 - arrow.getWidth();
		if (Inventory.Y >= 1720)
			Inventory.Y = 1720 - arrow.getHeight();

		if (Inventory.X <= 0)
			Inventory.X = 0 + arrow.getWidth();
		if (Inventory.Y <= 0)
			Inventory.Y = 0 + arrow.getHeight();

		Matrix m = new Matrix();
		m.setRotate((float) (SensorData.getCompassValue()),
				arrow.getWidth() / 2.0f, arrow.getHeight() / 2.0f);

		m.postTranslate(Inventory.X - arrow.getWidth() / 2.0f, Inventory.Y
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
		Inventory.toast("Reading data", context, true);
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
