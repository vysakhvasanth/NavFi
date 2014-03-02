package com.example.simplewifi;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PaintThread extends Thread {

	SurfaceHolder holder;
	boolean isRunning;
	SurfaceView view;

	public PaintThread(SurfaceHolder holder, SurfaceView view) {
		this.holder = holder;
		isRunning = false;
		this.view = view;

	}

	@Override
	public void run() {

		Canvas c;
		while (isRunning) {
			c = null;
			try {
				c = holder.lockCanvas(null);
				view.postInvalidate();
			} finally {
				// do this in a finally so that if an exception is thrown
				// during the above, we don't leave the Surface in an
				// inconsistent state
				if (c != null) {
					holder.unlockCanvasAndPost(c);
				}
			}
		}

	}

	public void setRun(boolean run) {
		isRunning = run;
	}

}
