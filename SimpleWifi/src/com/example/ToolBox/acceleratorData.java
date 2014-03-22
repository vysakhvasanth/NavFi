package com.example.ToolBox;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class acceleratorData {
	public static double[] lastAcc = new double[] { 0.0, 0.0, 0.0 };
	private static final int sizeFV = 6;
	private double[] five_values = new double[sizeFV];
	private int fv_iterator = 0; // iterator for five_values
	double a = 0.5;
	private SensorManager sensorManager;
	private Context context;
	private List<Sensor> l_sensors;
	private Timer timer;
	public final long INTERVAL = (1000 / 30); // 33.33 ms
	private final long timeout = 500;
	private long lastStepDetectedTime = 0;
	public static int stepCounter = 0;
	public final double differential = 2.6; // experimental value
	double[] oldAcc = new double[3];

	public SensorEventListener mySensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				// st.dataHookAcc(System.currentTimeMillis(), event.values[0],
				// event.values[1], event.values[2]);
				// just update the oldest z value
				lastAcc[0] = inventory.lowpassFilter(lastAcc[0],
						event.values[0], a);
				lastAcc[1] = inventory.lowpassFilter(lastAcc[1],
						event.values[1], a);
				lastAcc[2] = inventory.lowpassFilter(lastAcc[2],
						event.values[2], a);
				break;
			default:
			}// switch (event.sensor.getType())

		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}
	};

	public acceleratorData(Context ctx) {
		this.context = ctx;

	}

	public void load() {
		// Sensors
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		l_sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

		for (int i = 0; i < l_sensors.size(); i++) {
			// Register only compass and accelerometer
			if (l_sensors.get(i).getType() == Sensor.TYPE_ACCELEROMETER) {
				sensorManager.registerListener(mySensorEventListener,
						l_sensors.get(i), SensorManager.SENSOR_DELAY_FASTEST);
			}
		}

		// Register timer
		timer = new Timer("UpdateData", false);
		TimerTask task = new TimerTask() {

			@Override
			public void run() {

				Update();
			}
		};
		timer.schedule(task, 0, INTERVAL);
	}

	protected void Update() {

		long currentime = System.currentTimeMillis();
		double[] oldAcc = new double[3];
		System.arraycopy(lastAcc, 0, oldAcc, 0, 3);
		storeZdata(oldAcc[2]);

		// if current time - last step detected time > timeout range
		if (((currentime - lastStepDetectedTime) > timeout)
				&& checkStep(differential)) {
			lastStepDetectedTime = currentime;
			stepCounter++;
		}
	}

	// stores previous values of acc's Z component
	private void storeZdata(double value) {
		five_values[fv_iterator % sizeFV] = value;
		fv_iterator++;
		fv_iterator = fv_iterator % sizeFV; // fv_iterator never > sizeFV
	}

	private boolean checkStep(double peak) {

		int itr = 5;

		double val = five_values[(fv_iterator - 1 + sizeFV) % sizeFV];

		for (int u = 1; u < itr; u++) {

			double val_delta = five_values[(fv_iterator - 1 - u + sizeFV + sizeFV)
					% sizeFV];
			if ((val - val_delta) > peak) {
				return true;
			}
		}

		return false;
	}
}
