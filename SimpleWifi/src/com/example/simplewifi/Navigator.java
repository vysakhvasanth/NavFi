package com.example.simplewifi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/*
 * Class implements path finding and algorithms for 
 * positioning. Need to do acc & gyroscope measurements to 
 * calculate user movement. 
 * 
 * Disect footpath application to make use of its
 * step counter
 * */

public class Navigator extends SurfaceView implements SurfaceHolder.Callback {

	public Navigator(Context context, AttributeSet attr) {
		super(context, attr);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// call the thread here

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

}
