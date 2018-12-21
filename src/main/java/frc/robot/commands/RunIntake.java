/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;



/**
 * Shifter command to shift to high gear
 */
public class RunIntake extends Command {
  public RunIntake() {
    // Use requires() here to declare subsystem dependencies
    requires(Robot.intake); 
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    System.out.println("Run intake init!");
    Robot.intake.setSpeed(Robot.m_oi.getIntakeAxis() );
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    // System.out.println("Run intake execute!");
    Robot.intake.setSpeed(Robot.m_oi.getIntakeSpeed());
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
