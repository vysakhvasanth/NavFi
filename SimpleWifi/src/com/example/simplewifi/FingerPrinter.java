/*
 * Activity for recording data
 * 
 * written by Vas
 * */
package com.example.simplewifi;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.PointF;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.ToolBox.Inventory;
import com.example.ToolBox.wifiData;

public class FingerPrinter extends Activity {

	private WifiManager wify;
	private wifiData wifidata;
	private Button bstart;
	private Button bstop;
	private FingerPrinter fingerPrinter;
	private Capture nav;
	private Button bfind;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.capture);
		this.fingerPrinter = this;

		// inventory.startAllSensors(this);// hook up all three sensors

		nav = (Capture) findViewById(R.id.surfaceView);

		// add clicklistener to buttons
		bstart = (Button) this.findViewById(R.id.start);
		bstop = (Button) this.findViewById(R.id.stop);
		bfind = (Button) this.findViewById(R.id.findme);

		bfind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (nav != null) {
					Inventory.mode = Inventory.Mode.navigate;
					nav.registerReciever();
				}
			}
		});

		bstart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (nav != null) {
					nav.writeMappingtoFile();
					Inventory.toast("Mapping saved...", fingerPrinter, true);
				}
			}
		});

		bstop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// clear all plotted points
				Inventory.drawnpoints = new ArrayList<PointF>();
				Inventory.mode = Inventory.Mode.scanning;
			}
		});

	}

}
