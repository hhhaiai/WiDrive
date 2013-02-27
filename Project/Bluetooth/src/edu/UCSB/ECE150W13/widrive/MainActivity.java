package edu.UCSB.ECE150W13.widrive;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ToggleButton;
//import ioio.examples.hello.MainActivity.Looper;
//import ioio.examples.hello.MainActivity.Looper;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

public class MainActivity extends IOIOActivity implements SensorEventListener {
	private ToggleButton OnOffButton;
	private SensorManager sensormanager;
	private	Sensor accelerometer;
	private Sensor magneticsensor;
	float[] accelovalues = new float[3];
	float[] magnetvalues = new float[3];
	float[] rotmatrix = new float[9];
	float[] tiltvalues = new float[3];
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OnOffButton = (ToggleButton) findViewById(R.id.onoff);
        
        //setup sensors
        sensormanager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticsensor = sensormanager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
        
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	sensormanager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    	sensormanager.registerListener(this, magneticsensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	sensormanager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			System.arraycopy(event.values, 0, accelovalues, 0, 3);
			//Log.d("accelovalues", accelovalues[0] + " "+ accelovalues[1] + " " + accelovalues[3]);
		}
		else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			System.arraycopy(event.values, 0, magnetvalues, 0, 3);
		}
		
	}
    
    class Looper extends BaseIOIOLooper {
    	//Define inputs and outputs
    	private DigitalOutput led;
    
    	@Override
    	protected void setup() throws ConnectionLostException {
    		//Assign pins
    		led = ioio_.openDigitalOutput(0);
    	}
    	
    	@Override
    	public void loop() throws ConnectionLostException {
    		led.write(!OnOffButton.isChecked());
    		if (OnOffButton.isChecked()) {
    			SensorManager.getRotationMatrix(rotmatrix, null, accelovalues, magnetvalues);
    			SensorManager.getOrientation(rotmatrix, tiltvalues);
    			//CW azimuth more positive ? 
    			//pointing down makes pitch more positive
    			//tilting right makes roll more positive
    			//flat isn't quite 0, 0, 0
    			Log.d("tilt calc", "azimuth " + tiltvalues[0] + "pitch " + tiltvalues[1] + "roll " + tiltvalues[2]);
    		}
    		
    		
    	}
    	
    	
    }
    
    @Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
    
}