package Package;


import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;

public class Circle {
	
	public static void main(String[] args) {
		LineTracker tracker = new LineTracker(MotorPort.B, MotorPort.C, SensorPort.S1, SensorPort.S4, 400);
		tracker.Calibrate(false);
//		tracker.trackUntilCross();
		for (int i=0; i<6; i++) {		//pass 6 lines = 2 circuits
			tracker.trackUntilCross();
			tracker.driveOverCross();
		}
		
		tracker.turnAround();
		
		for (int i=0; i<6; i++) {		//pass 6 lines = 2 circuits
			tracker.trackUntilCross();
			tracker.driveOverCross();
		}
		
		tracker.flt();
	}
		
}
