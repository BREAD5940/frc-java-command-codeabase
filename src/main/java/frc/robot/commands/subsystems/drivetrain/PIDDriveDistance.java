/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.subsystems.drivetrain;

import org.ghrobotics.lib.mathematics.units.Length;
import org.ghrobotics.lib.mathematics.units.LengthKt;
import org.ghrobotics.lib.mathematics.units.derivedunits.VelocityKt;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.lib.motion.Util;
import frc.robot.subsystems.DriveTrain;

public class PIDDriveDistance extends Command {
	double targetEnd;
	double rawDelta;
	double maxSpeed;
	boolean isWithinDelta;
	double ticksToMax = 3;
	double lastCommand = 0;

	public PIDDriveDistance(Length distance, double maxSpeedFt) {
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		// super(20, 0, 0);
		requires(DriveTrain.getInstance());
		this.rawDelta = distance.getFeet();
		setTimeout(4);
		this.maxSpeed = maxSpeedFt;
	}

	public PIDDriveDistance(double distance, double maxSpeedFt, double timeout) {
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		// super(20, 0, 0);
		requires(DriveTrain.getInstance());
		this.rawDelta = distance;
		setTimeout(timeout);
		this.maxSpeed = maxSpeedFt;
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		this.targetEnd = rawDelta + DriveTrain.getInstance().getLeft().getFeet();
		// super.setSetpoint(targetEnd);
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		SmartDashboard.putData(this);
		final double kp = 6;

		var delta = targetEnd - DriveTrain.getInstance().getLeft().getDistance().getFeet();

		var power = Util.limit(delta * kp, maxSpeed);
		power = Util.limit(power, lastCommand - (1 / ticksToMax), lastCommand + (1 / ticksToMax));

		DriveTrain.getInstance().setClosedLoop(VelocityKt.getVelocity(LengthKt.getFeet(power)), VelocityKt.getVelocity(LengthKt.getFeet(power)));

		lastCommand = power;

		isWithinDelta = delta < (3f / 12f);
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return isWithinDelta;
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {}

	// @Override
	// protected double returnPIDInput() {
	//   return DriveTrain.getInstance().getLeft().getFeet();
	// }

	// @Override
	// protected void usePIDOutput(double arg0) {
	//   arg0 = Util.limit(arg0, 10);
	//   DriveTrain.getInstance().setClosedLoop(VelocityKt.getVelocity(LengthKt.getFeet(arg0)), VelocityKt.getVelocity(LengthKt.getFeet(arg0)));
	// }
}
