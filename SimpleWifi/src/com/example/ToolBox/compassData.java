package com.example.ToolBox;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class compassData {

	public double compValue[] = new double[] { 0.0, 0.0, 0.0 };
	private Context context;
	private static double compassValue = -1.0;
	private SensorManager sensorManager;
	List<Sensor> l_sensors;

	public static double getCompassValue() {
		return compassValue;
	}

	public compassData(Context context) {
		this.context = context;
	}

	public void load() {
		// Sensors
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		l_sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

		for (int i = 0; i < l_sensors.size(); i++) {
			// Register only compass and accelerometer
			if (l_sensors.get(i).getType() == Sensor.TYPE_ORIENTATION) {
				sensorManager.registerListener(mySensorEventListener,
						l_sensors.get(i), SensorManager.SENSOR_DELAY_FASTEST);
			}
		}
	}

	public void unload() {

		sensorManager.unregisterListener(mySensorEventListener);
	}

	public SensorEventListener mySensorEventListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// Auto-generated method stub
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ORIENTATION:
				// st.dataHookComp(System.currentTimeMillis(), event.values[0],
				// event.values[1], event.values[2]);

				compassValue = inventory.lowpassFilter(compassValue,
						event.values[0], 0.7); // pass only the x component of
												// the compass
				compValue[0] = event.values[0];
				compValue[1] = event.values[1];
				compValue[2] = event.values[2];
				break;
			default:
			}// switch (event.sensor.getType())
		}
	};

}
