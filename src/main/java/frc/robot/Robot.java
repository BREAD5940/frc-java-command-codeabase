/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.arcade_drive;
// import frc.robot.commands.drivetrain_shift_high;
// import frc.robot.commands.drivetrain_shift_low;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
// import frc.robot.commands.arcade_drive;
import frc.robot.subsystems.*;
// import frc.robot.commands.*;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  public static final drivetrain drivetrain  = new drivetrain();
  public static final intake intake = new intake();
  public static final elevator elevator = new elevator();
  public static final wrist wrist = new wrist();
  public static final robotconfig robotconfig = new robotconfig();
  public static OI m_oi;

  public static double elevator_setpoint = 0;
  public static double wrist_setpoint = 0;
  public static DoubleSolenoid shifterDoubleSolenoid = new DoubleSolenoid(9, 7, 3);
  public static DoubleSolenoid intakeDoubleSolenoid = new DoubleSolenoid(9, 0, 6);

  Command m_autonomousCommand;
  SendableChooser<Command> m_chooser = new SendableChooser<>();

  public static void drivetrain_solenoid_shift_high(){ shifterDoubleSolenoid.set(DoubleSolenoid.Value.kForward); }
  public static void drivetrain_solenoid_shift_low(){ shifterDoubleSolenoid.set(DoubleSolenoid.Value.kReverse); }
  public static void intake_close(){ intakeDoubleSolenoid.set(DoubleSolenoid.Value.kForward); }
  public static void intake_open(){ intakeDoubleSolenoid.set(DoubleSolenoid.Value.kReverse); }


  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_oi = new OI();
    Compressor compressor = new Compressor(9);
    compressor.setClosedLoopControl(true);
    
    drivetrain.init();
    elevator.init();
    wrist.init();

    // m_chooser.addDefault("Default Auto", new ExampleCommand());
    // chooser.addObject("My Auto", new MyAutoCommand());
    // SmartDashboard.putString("test", "Hellothere!!! intake clamp: ");



  }

  /**
   * This function is called every robot packet, no matter the mode. Use
  //  * this for items like diagnostics that you want ran during disabled,
  //  * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Fore speed axis value", m_oi.getForwardAxis());
    SmartDashboard.putNumber("Turn speed axis value", m_oi.getTurnAxis());
    SmartDashboard.putString("Drivetrain gear", drivetrain.current_gear);
    // SmartDashboard.putNumber("5 feet per second is this many raw: ", encoderlib.distanceToRaw(5, robotconfig.POSITION_PULSES_PER_ROTATION, robotconfig.left_wheel_effective_diameter));

    SmartDashboard.putNumber("Left talon speed", drivetrain.m_left_talon.getSelectedSensorVelocity(0));
    SmartDashboard.putNumber("Left talon error", drivetrain.m_left_talon.getClosedLoopError(0));
    SmartDashboard.putNumber("Right talon speed", drivetrain.m_right_talon.getSelectedSensorVelocity(0));
    SmartDashboard.putNumber("Right talon error", drivetrain.m_right_talon.getClosedLoopError(0));

    // SmartDashboard.putNumber("Throttle output", throttle.getRawAxis(1));
    // SmartDashboard.putNumber("Elevator setpoint", elevator_setpoint);
    // SmartDashboard.putNumber("Elevator height", elevator.getHeight());
    // SmartDashboard.putNumber("Elevator error", elevator.elevator_talon.getClosedLoopError(0));

    // SmartDashboard.putNumber("Wrist angle setpoint", wrist_setpoint); 
    // SmartDashboard.putNumber("Wrist talon pos", elevator.elevator_talon.getSelectedSensorPosition(0));
    // SmartDashboard.putNumber("Wrist error", elevator.elevator_talon.getClosedLoopError(0));
    // SmartDashboard.putNumber("Wrist angle (deg)", wrist.getAngle());
    // SmartDashboard.putNumber("Wrist angular velocity (deg/s)", wrist.getAngularVelocity());
    
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   * You can use it to reset any subsystem information you want to clear when
   * the robot is disabled.
   */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString code to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional commands to the
   * chooser code above (like the commented example) or additional comparisons
   * to the switch structure below with additional strings & commands.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_chooser.getSelected();

    /*
     * String autoSelected = SmartDashboard.getString("Auto Selector",
     * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
     * = new MyAutoCommand(); break; case "Default Auto": default:
     * autonomousCommand = new ExampleCommand(); break; }
     */

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.start();
    }

 

  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void teleopInit() {
    // drivetrain.init();
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }

  }

  /**
   * This function is called periodically during operator control.
   * 
   */
  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
    
    drivetrain_solenoid_shift_low();

    /**
     * Update the arcade drivetrain method
     */

    // new arcade_drive(m_oi.getForwardAxis(), m_oi.getTurnAxis());
    // drivetrain.m_left_talon.set(ControlMode.PercentOutput, 0.25);
    // drivetrain.m_right_talon.set(ControlMode.PercentOutput, 0.25);

    // drivetrain_shift_low();
    
    /**
     * Update the elevator PID method
     */
    // elevator_setpoint = elevator_setpoint+throttle.getRawAxis(1)*10;
    // elevator.setHeight(elevator_setpoint);

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
