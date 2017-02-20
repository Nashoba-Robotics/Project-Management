package edu.nr.robotics;

import edu.nr.lib.NRMath;
import edu.nr.lib.Units;
import edu.nr.lib.network.NetworkingDataTypeListener;
import edu.nr.lib.network.TCPServer;
import edu.nr.lib.units.Angle;
import edu.nr.lib.units.AngularSpeed;
import edu.nr.lib.units.Angle.Unit;
import edu.nr.lib.units.Time;

public class GearAlignCalculation implements NetworkingDataTypeListener {

	/**
	 * The distance parallel to drive direction that the camera is from the center of rotation of the robot.
	 * In inches
	 */
	public static final double CAMERA_TO_CENTER_OF_ROBOT_DIST_Y = 0;
	
	/**
	 * The distance to stop away from the tape of the gear
	 */
	public static final double DISTANCE_TO_STOP_FROM_GEAR = 0;
	
	Angle turnAngle = Angle.ZERO;
	double driveDistance = 0;
	
	private Angle lastSeenAngle;
	private double lastSeenDistance;
	
	private Time timeOfLastData;
	
	private static GearAlignCalculation singleton;
	
	public synchronized static void init() {
		if (singleton == null)
			singleton = new GearAlignCalculation();
	}
	
	public static GearAlignCalculation getInstance() {
		if (singleton == null) {
			init();
		}
		return singleton;
	}
	
	@Override
	public void updateDataType(TCPServer.NetworkingDataType type, double value) {
		if(type.identifier == 'a') {
			lastSeenAngle = new Angle(value, Angle.Unit.DEGREE);
		} else if(type.identifier == 'd') {
			lastSeenDistance = value;
		}
		timeOfLastData = Time.getCurrentTime();
	
		driveDistance = Math.hypot(lastSeenDistance * lastSeenAngle.cos() + CAMERA_TO_CENTER_OF_ROBOT_DIST_Y, lastSeenDistance * lastSeenAngle.sin()) - DISTANCE_TO_STOP_FROM_GEAR;
		turnAngle = NRMath.atan2(lastSeenDistance * lastSeenAngle.sin(),lastSeenDistance * lastSeenAngle.cos() + CAMERA_TO_CENTER_OF_ROBOT_DIST_Y);
	}
	
	public double getDistToDrive() {
		return driveDistance;
	}
	
	public Angle getAngleToTurn() {
		return turnAngle;
	}
	
	public boolean canSeeTarget() {
		return Time.getCurrentTime().sub(timeOfLastData).lessThan(AutoTrackingCalculation.MIN_TRACKING_WAIT_TIME);
	}
}