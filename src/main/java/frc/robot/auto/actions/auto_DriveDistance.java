/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto.actions;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.RobotConfig;
import frc.robot.lib.EncoderLib;



  /**
   * auto_DriveDistance is a basic auto action. It should drive in a straight-ish line, as it uses 
   * nested PID loops to correct for errors caused by differing coefficients of friction. 
   * @param distance
   * @param targetSpeed
   * @param timeout
   */
public class auto_DriveDistance extends Command {
  double targetDistance;
  double targetSpeed;
  boolean isDone = false;
  double timeout;
  double forward_kp;
  double targetSpeedRaw;
  double startingDistanceLeft;
  double startingDistanceRight;
  double endDistanceLeft;

  // things that change
  double forward_speed;
  double left_speed_raw;
  double right_speed_raw;

  /**
   * auto_action_DRIVE is a basic auto action. It should drive in a straight-ish line, as it uses 
   * nested PID loops to correct for errors caused by differing coefficients of friction. 
   * @param distance
   * @param targetSpeed
   * @param timeout
   */
  public auto_DriveDistance(double distance, double targetSpeed, double timeout) {
    this.targetDistance = distance;
    this.targetSpeed = targetSpeed;
    this.timeout = timeout;
    // Use requires() here to declare subsystem dependencies
    requires(Robot.drivetrain);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    setTimeout(timeout); // set the timeout
    System.out.println("Auto action drive init!");
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  public void execute() {
    // double new_target_pos = currentDistance + targetDistance;
    double forward_speed = Robot.drivetrain.shitty_P_loop(RobotConfig.m_left_position_kp_high, 
      endDistanceLeft, // target distance in feet 
      Robot.drivetrain.getLeftDistance(), 
      RobotConfig.drive_auto_forward_velocity_min, 
      RobotConfig.drive_auto_forward_velocity_max);
    double left_speed_raw = EncoderLib.distanceToRaw(forward_speed, RobotConfig.left_wheel_effective_diameter / 12, RobotConfig.POSITION_PULSES_PER_ROTATION) / 10;
    double right_speed_raw = EncoderLib.distanceToRaw(forward_speed, RobotConfig.right_wheel_effective_diameter / 12, RobotConfig.POSITION_PULSES_PER_ROTATION) / 10;

    SmartDashboard.putNumber("Forward speed pid output", forward_speed);
    SmartDashboard.putNumber("Raw left speed auto meme", left_speed_raw);
    SmartDashboard.putNumber("distance setpoint is currently set to",  startingDistanceLeft + targetDistance);
    SmartDashboard.putNumber("Current distance setpoint for auto is: ", Robot.drivetrain.getLeftDistance());


    Robot.drivetrain.setLeftSpeedRaw(left_speed_raw);
    Robot.drivetrain.setRightSpeedRaw(right_speed_raw);

    System.out.println("target forward speed: " + forward_speed);
    System.out.println("Left speed raw/right speed raw: " + left_speed_raw + "/" + right_speed_raw);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if ( ((Math.abs(Robot.drivetrain.getRightDistance() - this.targetDistance) < RobotConfig.drive_auto_position_tolerence) 
        && (Math.abs(Robot.drivetrain.getLeftDistance() - this.targetDistance) < RobotConfig.drive_auto_position_tolerence) 
        && (Math.abs(Robot.drivetrain.getLeftVelocity()) < RobotConfig.drive_auto_velocity_tolerence) 
        && (Math.abs(Robot.drivetrain.getRightVelocity()) < RobotConfig.drive_auto_position_tolerence))
        || (isTimedOut()) ){
      return true;}
    else { return false; }
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.drivetrain.setLeftSpeedRaw(0);
    Robot.drivetrain.setRightSpeedRaw(0);
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    Robot.drivetrain.setLeftSpeedRaw(0);
    Robot.drivetrain.setRightSpeedRaw(0);
  }
}
