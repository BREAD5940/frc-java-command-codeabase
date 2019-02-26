/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.subsystems.drivetrain;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.DriveTrain;

public class FollowVisonTargetTheSecond extends Command {
	public FollowVisonTargetTheSecond() {
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		Update_Limelight_Tracking();
		if (m_LimelightHasValidTarget) {
			DriveTrain.getInstance().arcadeDrive(m_LimelightDriveCommand, m_LimelightSteerCommand);
		} else {
			DriveTrain.getInstance().stop();
		}
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return m_isDone;
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {}

	private boolean m_LimelightHasValidTarget = false;
	private double m_LimelightDriveCommand = 0.0;
	private double m_LimelightSteerCommand = 0.0;
	private boolean m_isDone = false;

	/**
	 * This function implements a simple method of generating driving and steering commands
	 * based on the tracking data from a limelight camera.
	 */
	public void Update_Limelight_Tracking() {
		// These numbers must be tuned for your Robot!  Be careful!
		final double STEER_K = 0.05;                    // how hard to turn toward the target
		final double DRIVE_K = 0.26;                    // how hard to drive fwd toward the target
		final double DESIRED_TARGET_AREA = 6;        // Area of the target when the robot reaches the wall
		final double MAX_DRIVE = 0.7;                   // Simple speed limit so we don't drive too fast

		double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
		double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
		double ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);
		double ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0);

		if (Math.abs(DESIRED_TARGET_AREA - ta) < 0.3)
			m_isDone = true;

		if (tv < 1.0) {
			m_LimelightHasValidTarget = false;
			m_LimelightDriveCommand = 0.0;
			m_LimelightSteerCommand = 0.0;
			return;
		}

		m_LimelightHasValidTarget = true;

		// Start with proportional steering
		double steer_cmd = tx * STEER_K;
		m_LimelightSteerCommand = steer_cmd;

		// try to drive forward until the target area reaches our desired area
		double drive_cmd = (DESIRED_TARGET_AREA - ta) * DRIVE_K;

		// don't let the robot drive too fast into the goal
		if (drive_cmd > MAX_DRIVE) {
			drive_cmd = MAX_DRIVE;
		}
		m_LimelightDriveCommand = drive_cmd;
	}
}
