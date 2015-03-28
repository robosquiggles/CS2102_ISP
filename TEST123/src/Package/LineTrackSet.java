package Package;

import lejos.hardware.port.*;
import lejos.hardware.sensor.*;
import lejos.utility.Delay;

public class LineTrackSet {
	
	EV3ColorSensor left;
	EV3ColorSensor right;
	
	float[] leftBounds = new float[2];										// [ min, max ]	these are the lowest and highest possible values for the line
	float[] rightBounds = new float[2];										// [ min, max ]
	
	public float[] sensorVals = new float[2];										// [ left, right ]	to write fetched sensor values to
	
	public LineTrackSet(Port leftSensorPort, Port rightSensorPort) {
		left = new EV3ColorSensor(leftSensorPort);
		right = new EV3ColorSensor(rightSensorPort);
		
		leftBounds[0] = 10;													//set ludicrously high/low values so that they WILL get written over
		leftBounds[1] = -10;
		rightBounds[0] = 10;
		rightBounds[1] = -10;
	}
	
	//calibrate method
	public void Calibrate() {
		float[] tempArray = new float[2];									// [ left, right ]	to write fetched sensor values to
		
		left.getRedMode().fetchSample(tempArray, 0);						//get the sensor values
		right.getRedMode().fetchSample(tempArray, 1);
		
		if (tempArray[0] < leftBounds[0]) leftBounds[0] = tempArray[0];		//update lowest/highest value
		if (tempArray[0] > leftBounds[1]) leftBounds[1] = tempArray[0];
		if (tempArray[1] < rightBounds[0]) rightBounds[0] = tempArray[1];
		if (tempArray[1] > rightBounds[1]) rightBounds[1] = tempArray[1];
	}
	
	//getSensorValues: puts the values into the sensorVals array.  Values are percents of full sensor range (float)
	private void getSensorValues() {
		
		Delay.msDelay(2);
		
		left.getRedMode().fetchSample(sensorVals, 0);						//get the sensor values
		right.getRedMode().fetchSample(sensorVals, 1);
		Calibrate();
		
		sensorVals[0] = (sensorVals[0] - leftBounds[0]) / (leftBounds[1] - leftBounds[0]);	//set the left value to a normalized % 
																							// of the sensor's whole range (float)
		sensorVals[1] = (sensorVals[1] - rightBounds[0]) / (rightBounds[1] - rightBounds[0]);//set the right value to a normalized
																							// of the sensor's whole range (float)
	}
	
	//getValue method: returns a value (in rps) to change the speeds with
	public int getValue(int maxSpeed) {
		
		int turnValue;
		
		getSensorValues();			//load the array
		
		turnValue = (int) ((sensorVals[0] - sensorVals[1]) * maxSpeed);
		
		return turnValue;
	}
	
	
	//onCross method: returns boolean, true if on cross
	public boolean onCross() {
		
		float percentDB = (float) .4;										// if the sensor is above this percent of the range, it is "on black"
		
		getSensorValues();			//load the array
		
		return ((sensorVals[0] < 1 - (percentDB * leftBounds[1])) && (sensorVals[1] < 1 - (percentDB * rightBounds[1])));

	}

}
