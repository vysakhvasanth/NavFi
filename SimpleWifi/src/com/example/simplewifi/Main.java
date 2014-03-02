/*
 * Main activity
 * 
 * written by Vas
 * */
package com.example.simplewifi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity {

	Button capture, navigate;

	OnClickListener cap = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(),
					FingerPrinter.class);
			startActivity(intent);
		}

	};

	OnClickListener nav = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(),
					Navigation.class);
			startActivity(intent);
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_sniff);

		capture = (Button) findViewById(R.id.ado);
		navigate = (Button) findViewById(R.id.back);
		navigate.setOnClickListener(nav);
		capture.setOnClickListener(cap);

	}
}
