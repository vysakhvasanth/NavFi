/*
 * Data class used to store location information
 * 
 * written by Vas
 * */
package com.example.ToolBox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Location implements Serializable {

	public int ID;
	public String _BSSID;
	public int _RSSI;
	public float x = 0;
	public float y = 0;
	public List<String> _BSSIDs = new ArrayList<String>();

}
