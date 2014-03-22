/*
 * Wifi sensor class
 * Reads wifi data and store the finger-prints persistently
 * 
 * written by Vas
 * */
package com.example.ToolBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.simplewifi.Capture;

public class wifiData extends BroadcastReceiver {

	File file;
	final String FILE_NAME = "sdcard/file.ser";
	LocationManager loc;
	private String MS_AP;
	private List<String> bssid = new ArrayList<String>();
	private List<Integer> rssiAverage = new ArrayList<Integer>();
	private int Strongest = 0;
	private String StrongestAP;
	private Context ctx;

	WifiManager wm;

	public wifiData(WifiManager wifi, Context context) {

		this.wm = wifi;
		this.ctx = context;
		file = new File(FILE_NAME);

		// test : reading data
		try {
			if (new File(FILE_NAME).exists())
				readFromFile();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onReceive(Context context, Intent intent) {

		List<ScanResult> results = wm.getScanResults();
		inventory.toast("Reading data", context, true);
		for (ScanResult result : results) {

			if (!bssid.contains(result.BSSID)) {
				bssid.add(result.BSSID);
				rssiAverage.add(result.level);
			}

			// compute the strongest ap
			if (Math.abs(result.level) > Strongest) {
				Strongest = result.level;
				StrongestAP = result.BSSID;
			}
		}

		if (inventory.mode == inventory.Mode.navigate) {

			LocationFinder(StrongestAP);
			return;
		}

		wm.startScan();
	}

	/*
	 * Simple Location finder
	 */
	public void LocationFinder(String StrogestAP) {
		List<Location> al = new ArrayList<Location>();
		try {
			al = readFromFile();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (al == null) {
			inventory.toast("No data in list", ctx, false);
			return;
		}

		/* Check against the loop */
		for (Location l : al) {
			inventory.toast("Strongest ap: " + StrogestAP + "/ Current AP: "
					+ l._BSSID, ctx, true);
			if (l._BSSID.equals(StrogestAP)) {
				inventory.X = l.x;
				inventory.Y = l.y;
				inventory.toast("Location found!", ctx, true);
			}
		}
		wm.startScan();

	}

	public void addLocationToList() {

		// calculate the average rssi
		int rssiaverage = 0;
		int total = 0;
		for (Integer ia : rssiAverage) {

			total += ia;
		}
		rssiaverage = total / rssiAverage.size();

		Location l = new Location();
		l._BSSID = StrongestAP;
		l._RSSI = rssiaverage;
		l._BSSIDs = bssid;
		l.ID = inventory.createID();
		l.x = Capture.x;
		l.y = Capture.y;
		inventory.locationPoints.put(l._BSSID, l);// store in list

	}

	/*
	 * Serialize data and write to file access point details
	 */
	public boolean writetofile(HashMap<String, Location> loc)
			throws IOException {

		if (!fileExists()) {
			try {
				file.createNewFile();

			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		FileOutputStream f = new FileOutputStream(file);
		ObjectOutputStream s = new ObjectOutputStream(f);
		try {
			s.writeObject(loc);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			f.close();
			s.close();
		}

		return false;
	}

	/*
	 * De-serialize lists, and read data
	 */
	public List<Location> readFromFile() throws StreamCorruptedException,
			IOException {

		HashMap<String, Location> loc = new HashMap<String, Location>();
		List<Location> areaInfo = new ArrayList<Location>();
		if (!fileExists())
			return null;

		FileInputStream f = new FileInputStream(file);
		ObjectInputStream s = new ObjectInputStream(f);
		try {
			loc = (HashMap<String, Location>) s.readObject();

			Iterator<Entry<String, Location>> iterator = loc.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Map.Entry pair = (Map.Entry) iterator.next();
				Log.d("Data:", String.valueOf(((Location) pair.getValue()).ID)
						+ ", X:" + ((Location) pair.getValue()).x + ", Y:"
						+ ((Location) pair.getValue()).y + ", RSSI_AV: "
						+ ((Location) pair.getValue())._RSSI + ", BSSID: "
						+ ((Location) pair.getValue())._BSSID);
				Location l = (Location) pair.getValue();
				areaInfo.add(l);

				for (String ser : l._BSSIDs) {
					Log.d("Data:", ser);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			f.close();
			s.close();
		}

		return areaInfo;

	}

	public boolean fileExists() {
		if (file.exists()) {
			return true;
		}

		return false;
	}

}

// Location l = new Location();
// l._BSSID = result.BSSID;
// l._RSSI = result.level;
// l.x = 0;
// l.y = 0;
// l.ID = (int) (l._RSSI * (2 * Math.exp(20)));
// if (!ids.containsKey(l._BSSID))
// ids.put(l._BSSID, Math.abs(l._RSSI));
//
// accessPoints.put(l._BSSID, l);