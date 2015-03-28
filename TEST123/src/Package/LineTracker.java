package Package;

import java.util.Arrays;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.*;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class LineTracker {
	
	RegulatedMotor leftMotor;
	RegulatedMotor rightMotor;
	LineTrackSet trackerSet;
	
	private int maxSpeed;						//max speed of motors in rotations/sec
	
	//LineTracker Constructor
	public LineTracker(Port leftMotorPort, Port rightMotorPort, Port leftSensorPort, Port rightSensorPort, int motorMaxSpeed) {
		
		leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
		rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);
		
		trackerSet = new LineTrackSet(leftSensorPort, rightSensorPort);
		
		maxSpeed = motorMaxSpeed;
		
	}
	
	public void turnLeftToLine() {			//fix dis
		leftMotor.setSpeed((int) (maxSpeed/1.5));
		rightMotor.setSpeed((int) (maxSpeed/1.5));
		leftMotor.backward();				//set to turn left
		rightMotor.forward();
		Delay.msDelay(250);
		while (!(trackerSet.getValue(maxSpeed) < -20)) {}		//until left hits line
		rightMotor.setSpeed(trackerSet.getValue(maxSpeed)*4);
		leftMotor.setSpeed(trackerSet.getValue(maxSpeed)*4);
		while (!((trackerSet.getValue(maxSpeed) < 10) && (trackerSet.getValue(maxSpeed) > -10))) {}		//center on line
	}
	
	
	public void turnRightToLine() {
		leftMotor.setSpeed((int) (maxSpeed/1.5));
		rightMotor.setSpeed((int) (maxSpeed/1.5));
		leftMotor.forward();				//set to turn right
		rightMotor.backward();
		Delay.msDelay(250);
		while (!(trackerSet.getValue(maxSpeed) > 20)) {}		//until left hits line
		rightMotor.setSpeed(trackerSet.getValue(maxSpeed)*4);
		leftMotor.setSpeed(trackerSet.getValue(maxSpeed)*4);
		while (!((trackerSet.getValue(maxSpeed) < 10) && (trackerSet.getValue(maxSpeed) > -10))) {}		//center on line
	}
	
	public void turnAround() {
		turnLeftToLine();
		turnLeftToLine();
	}
	
	//calibrate method
	public void Calibrate(boolean print) {
		
		leftMotor.rotate(100,true);			//rotate a bit right
		rightMotor.rotate(-100,true);
		while(leftMotor.isMoving()) {		//poll, record lowest/highest
			trackerSet.Calibrate();
			if (print) {			//print if you want
				LCD.clear();
				LCD.drawString(Arrays.toString(trackerSet.leftBounds),0,3);
				LCD.drawString(Arrays.toString(trackerSet.rightBounds),0,4);
			}
		}
		
		
		leftMotor.rotate(-200,true);		//rotate left the same amount
		rightMotor.rotate(200,true);
		while(leftMotor.isMoving()) {		//poll, record lowest/highest
			trackerSet.Calibrate();
			if (print) {			//print if you want
				LCD.clear();
				LCD.drawString(Arrays.toString(trackerSet.leftBounds),0,3);
				LCD.drawString(Arrays.toString(trackerSet.rightBounds),0,4);
			}
		}
		
		
		leftMotor.rotate(100,true);			//rotate back to line
		rightMotor.rotate(-100,true);
		while(leftMotor.isMoving()) {		//poll, record lowest/highest
			trackerSet.Calibrate();
			if (print) {			//print if you want
				LCD.clear();
				LCD.drawString(Arrays.toString(trackerSet.leftBounds),0,3);
				LCD.drawString(Arrays.toString(trackerSet.rightBounds),0,4);
			}
		}
	}
	
	
	//trackUntilCross method: tracks until cross!
	public void trackUntilCross() {
		double Kp = 150.0 / (double) maxSpeed;
		leftMotor.setSpeed(maxSpeed + (int) (trackerSet.getValue(maxSpeed)  * Kp));
		rightMotor.setSpeed(maxSpeed - (int) (trackerSet.getValue(maxSpeed) * Kp));
		leftMotor.forward();				//drive forward
		rightMotor.forward();
		while (!trackerSet.onCross()) {		//set speed
			leftMotor.setSpeed(maxSpeed + (int) (trackerSet.getValue(maxSpeed)  * Kp));
			rightMotor.setSpeed(maxSpeed - (int) (trackerSet.getValue(maxSpeed) * Kp));
		}
		flt();
		
	}
	
	
	//driveOverCross method: drives past a cross
	public void driveOverCross() {
		leftMotor.setSpeed(maxSpeed/2);		//drive forward
		rightMotor.setSpeed(maxSpeed/2);
		rightMotor.forward();
		leftMotor.forward();
		while (trackerSet.onCross()) {}		//until off of cross
	}
	
	
	//flt method: flt the motors
	public void flt() {
		leftMotor.flt(true);
		rightMotor.flt(false);
	}
}
