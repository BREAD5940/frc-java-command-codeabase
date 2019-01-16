package frc.robot.lib.motion;

import frc.robot.lib.motion.Point;
import jaci.pathfinder.Trajectory;

public class Odometer /* extends LogBase */ {
	private double accumulativeDistance;
	private double x;
	private double y;
	private double theta;

	private double prevLeftEncoderValue;
	private double prevRightEncoderValue;
	private static Odometer instance;

	public static Odometer getInstance() {
		if (instance == null)
			instance = new Odometer();
		return instance;
	}

	public Odometer() {
		// setLogSenderName("Odometer");
		reset();
		// log("Started");
		// System.out.println("Odometer started");

	}

	public void setOdometryForPathfinder(Trajectory trajectory){
		setX(trajectory.get(0).x);
		setY(trajectory.get(0).y);
		setTheta(trajectory.get(0).heading);
	}

	public void update(double leftEncoder, double rightEncoder, double gyroAngle) {
		this.theta = gyroAngle;
		gyroAngle = Math.toRadians(gyroAngle + 90);

		gyroAngle = Math.PI - gyroAngle;

		double deltaLeftEncoder = leftEncoder - prevLeftEncoderValue;
		double deltaRightEncoder = rightEncoder - prevRightEncoderValue;
		double distance = (deltaLeftEncoder + deltaRightEncoder) / 2;

		x += distance * Math.cos(gyroAngle);
		y += distance * Math.sin(gyroAngle);

		// x -= distance * Math.cos(gyroAngle);
		// y += distance * Math.sin(gyroAngle);

		accumulativeDistance += Math.abs(distance);
		prevLeftEncoderValue = leftEncoder;
		prevRightEncoderValue = rightEncoder;
	}

	public void reset() {
		accumulativeDistance = x = y = prevLeftEncoderValue = prevRightEncoderValue = 0;
		// log("reset");
	}

	public double getAccumulativeDistance() {
		return accumulativeDistance;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getTheta() {
		return theta % (2 * Math.PI);
	}

	public synchronized void setTheta(double theta){
		this.theta = theta;
	}

	@Deprecated
	public void setX(double x) {
		this.x = x;
	}

	@Deprecated
	public void setY(double y) {
		this.y = y;
	}

	public Point getPoint() {
		return new Point(x, y);
	}

	@Override
	public String toString() {
		return "Odometer: accumulativeDistance=" + accumulativeDistance + ", x=" + x + ", y=" + y;
	}

}