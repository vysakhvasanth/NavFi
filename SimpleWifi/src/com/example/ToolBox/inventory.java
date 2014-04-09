/*
 * External stub funtions
 * Used by all classes to call simple methods
 * 
 * written by Vas
 * */
package com.example.ToolBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.widget.Toast;

import com.example.simplewifi.R;

public class Inventory {

	public static float X = 540;
	public static float Y = 1060;
	static int maxHeight = 1720;
	static int maxWidth = 1080;
	public static boolean compass_hooked = false;

	public enum Mode {
		scanning, navigate
	}

	public static Mode mode = Mode.scanning;

	public static double lowpassFilter(double old_value, double new_value,
			double a) {
		return old_value + a * (new_value - old_value);
	}

	// public static double lowpassFilter(double old_value, double new_value,
	// double a) {
	// return old_value * a + ((1 - a) * (new_value));
	// }

	public static HashMap<String, Location> locationPoints = new HashMap<String, Location>();

	public static void toast(String msg, Context context, boolean longer) {
		if (longer)
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		else
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static Bitmap getMap(Context context) {
		Bitmap map = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.mp);
		Bitmap mapscale = Bitmap.createScaledBitmap(map, maxWidth, maxHeight,
				true);

		return mapscale;

	}

	public static Bitmap getArrow(Context context) {

		Bitmap arrow = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.arrow);

		return arrow;
	}

	public static Paint redPaint() {
		Paint p = new Paint();
		p.setColor(Color.RED);
		return p;
	}

	public static Paint bluePaint() {
		Paint p = new Paint();
		p.setColor(Color.BLUE);
		return p;
	}

	public static Paint cyanPaint() {
		Paint p = new Paint();
		p.setColor(Color.CYAN);
		return p;
	}

	public static Paint text50() {
		Paint p = new Paint();
		p.setTextSize(50.0f);
		return p;
	}

	public static Paint text50MAG() {
		Paint p = new Paint();
		p.setTextSize(50.0f);
		p.setColor(Color.MAGENTA);
		return p;
	}

	public static Paint text50BLUE() {
		Paint p = new Paint();
		p.setTextSize(50.0f);
		p.setColor(Color.BLUE);
		return p;
	}

	public static List<PointF> drawnpoints = new ArrayList<PointF>();

	public static AlertDialog.Builder START_DIALOG(Context context) {
		return null;

	}

	public static int inc_id = 0;

	public static int createID() {

		return inc_id++;

	}

	public static int returnID() {
		return inc_id;
	}

}
