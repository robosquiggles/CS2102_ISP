package Package;

import java.util.Arrays;

import lejos.hardware.lcd.LCD;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;

public class GridNavigator {
	
	private LineTracker tracker;
	
	private int currentX = 0;
	private int currentY = 0;
	private int currentHeading = 1;
	
	//headings (constant)
	private final int xp = 1;
	private final int yp = 2;
	private final int xn = 3;
	private final int yn = 4;
	
	public static void main(String[] args) {
		GridNavigator navGuy = new GridNavigator(MotorPort.B, MotorPort.C, SensorPort.S1, SensorPort.S4, 400);
		navGuy.navigateTo(4,4);
		navGuy.navigateTo(1,7);
		navGuy.navigateTo(0,7);
		navGuy.navigateTo(0,6);
		navGuy.navigateTo(1,6);
		navGuy.navigateTo(3,3);
		navGuy.navigateTo(0, 0);
	}
	
	public GridNavigator(Port leftMotorPort, Port rightMotorPort, Port leftSensorPort, Port rightSensorPort, int motorMaxSpeed) {
		tracker = new LineTracker(leftMotorPort, rightMotorPort, leftSensorPort, rightSensorPort, motorMaxSpeed);
		currentHeading = xp;
		tracker.Calibrate(false);
		
	}
	
	
	public void turnTo(int desHeading) {
//		xp = 1
//		yp = 2
//		xn = 3
//		yn = 4
		int changeInHeading = currentHeading - desHeading;
		
		if (changeInHeading < 0) changeInHeading = changeInHeading + 4;
		
		switch (changeInHeading) {
		case 1:
			tracker.turnRightToLine();
			break;
		case 2:
			tracker.turnAround();
			break;
		case 3:
			tracker.turnLeftToLine();
			break;
		}
		currentHeading = desHeading;
	}
	
	
	public void navigateTo(int destX, int destY) {
		int moveX = destX - currentX;			//how many rows to move
		int moveY = destY - currentY;			//how many columns to move
		
		//ROW MOVEMENT
		if (moveX < 0) {						//handles moving in the negative direction
			moveX = moveX * -1;
			turnTo(xn);
		} else if (moveX > 0){
			turnTo(xp);
		} else {}
		while (moveX > 0) {
			tracker.trackUntilCross();
			tracker.driveOverCross();
			tracker.leftMotor.rotate(60, true);
			tracker.rightMotor.rotate(60, false);
			tracker.flt();
			moveX--;
		}
		
		
		//COLUMN MOVEMENT
		if (moveY < 0) {						//handles moving in the negative direction
			moveY = moveY * -1;
			turnTo(yn);
		} else if (moveY > 0){
			turnTo(yp);
		} else {}
		
		while (moveY > 0) {
			tracker.trackUntilCross();
			tracker.driveOverCross();
			tracker.leftMotor.rotate(60, true);
			tracker.rightMotor.rotate(60, false);
			tracker.flt();
			moveY--;
		}
		
		tracker.flt();							//stop the bot
		currentX = destX;						//set current position
		currentY = destY;
		LCD.drawString(Integer.toString(currentX),0,3);			//DEBUG
		LCD.drawString(Integer.toString(currentY),0,4);
	}
}
