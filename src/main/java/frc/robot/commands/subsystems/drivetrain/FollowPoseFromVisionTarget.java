package frc.robot.commands.subsystems.drivetrain;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class FollowPoseFromVisionTarget extends Command {

	boolean hadTarget;

	double[] visionData, rangeData;

	public FollowPoseFromVisionTarget() {
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {

		// We first try to aquire a vision target
		// this part doesn't need to run once you've found one
		// for now the robot just sits and checks for a target,
		// in the future make it active and try to find them,
		// based on odometry?

		if (!hadTarget) {
			if (Robot.limelight.getData()[0] == 1) {
				hadTarget = true;
			}
		}

	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return false;
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {}
}
