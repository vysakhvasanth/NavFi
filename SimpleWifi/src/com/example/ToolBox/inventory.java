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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.widget.Toast;

public class inventory {

	public static float X = 150;
	public static float Y = 150;

	public enum Mode {
		scanning, navigate
	}

	public static Mode mode = Mode.scanning;

	public static double lowpassFilter(double old_value, double new_value,
			double a) {
		return old_value + a * (new_value - old_value);
	}

	public static HashMap<String, Location> locationPoints = new HashMap<String, Location>();

	public static void toast(String msg, Context context, boolean longer) {
		if (longer)
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		else
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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
