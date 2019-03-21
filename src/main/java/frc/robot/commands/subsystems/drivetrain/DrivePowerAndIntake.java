/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.subsystems.drivetrain;

import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Intake;

public class DrivePowerAndIntake extends TimedCommand {
	double power, time, reqEndTime, intake;

	/**
	 * So I fear no man. But this, this scares me. Literally drive forward for a couple seconds.
	 * @param power
	 * @param time
	 * 
	 * @author Matthew Morley
	 */
	public DrivePowerAndIntake(double drive, double intake, double time) {
		super(time);
		// Use requires() here to declare subsystem dependencies
		requires(DriveTrain.getInstance());
		requires(Intake.getInstance());
		this.power = drive;
		this.intake = intake;
		this.time = time;
		// setTimeout(time);
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		// System.out.println("hi!");
		DriveTrain.getInstance().arcadeDrive(power, 0, false);
		Intake.getInstance().setHatchSpeed(intake);
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
		DriveTrain.getInstance().stop();
		Intake.getInstance().setHatchSpeed(0);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
		end();
	}
}
