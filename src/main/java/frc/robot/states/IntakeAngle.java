package frc.robot.states;

import frc.robot.lib.obj.RoundRotation2d;
import frc.robot.subsystems.superstructure.RotatingJoint.RotatingArmState;

/**
 * right now this is basically just a pair of doubles, but maybe
 * in the future it could also have different constants about the 
 * superstructure based on the angles
 * 
 * this all assumes that '0' is straight forwards on both joints
 */
public class IntakeAngle {

	public RotatingArmState elbowAngle;
	public RotatingArmState wristAngle;
	// TODO maybe have sanity checking on the angles to make sure they're not out of bounds in the context of the intake?

	public IntakeAngle(RotatingArmState elbowAngle, RotatingArmState wristAngle) {
		this.wristAngle = wristAngle;
		this.elbowAngle = elbowAngle;
	}

	public IntakeAngle(RoundRotation2d elbow, RoundRotation2d wrist) {
		this(new RotatingArmState(elbow), new RotatingArmState(wrist));
	}

	public double getMinHeight() {
		double min = 0; //TODO remove instan.

		if (elbowAngle.angle.getDegree() > 0) {
			min = 0;
		}
		return min;
	}

	public RotatingArmState getElbow() {
		return elbowAngle;
	}

	public RotatingArmState getWrist() {
		return wristAngle;
	}

	public boolean isEqualTo(IntakeAngle other) {
		return (this.elbowAngle.angle.isEqualTo(other.elbowAngle.angle)
				&& this.wristAngle.angle.isEqualTo(other.wristAngle.angle));
	}

	public String toString() {
		return elbowAngle.toString() + ", " + wristAngle.toString();
	}
}
