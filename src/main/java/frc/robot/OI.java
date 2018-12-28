/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
// import frc.robot.commands.close_clamp;
// import edu.wpi.first.wpilibj.command.Command;
// import frc.robot.subsystems.intake;
import frc.robot.commands.*;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
  //// CREATING BUTTONS
  // One type of button is a joystick button which is any button on a
  //// joystick.
  // You create one by telling it which joystick it's on and which button
  // number it is.
  
  private Joystick primaryJoystick = new Joystick(RobotConfig.controls.primary_joystick_port);
  private Joystick secondaryJoystick = new Joystick(RobotConfig.controls.secondary_joystick_port);

  Button shift_up_button = new JoystickButton(primaryJoystick, RobotConfig.controls.shift_up_button);
  Button shift_down_button = new JoystickButton(primaryJoystick, RobotConfig.controls.shift_down_button);
  Button open_clamp_button = new JoystickButton(primaryJoystick, xboxmap.Buttons.A_BUTTON);
  Button close_clamp_button = new JoystickButton(primaryJoystick, xboxmap.Buttons.Y_BUTTON);

  public OI(){
    shift_up_button.whenPressed(new DriveShiftHigh());
    shift_down_button.whenPressed(new DriveShiftLow());
    open_clamp_button.whenPressed(new OpenClamp());
    close_clamp_button.whenPressed(new CloseClamp());
  }

  public double getForwardAxis(){ return -1 * primaryJoystick.getRawAxis(RobotConfig.controls.forward_axis); }
  public double getTurnAxis(){ return primaryJoystick.getRawAxis(RobotConfig.controls.turn_axis); }
  public double getIntakeAxis(){ return primaryJoystick.getRawAxis(RobotConfig.controls.intakeAxis); }
  public double getOuttakeAxis(){ return primaryJoystick.getRawAxis(RobotConfig.controls.outtakeAxis); }
  /**
   * Get intake speed is the difference between intake and outtake axis speeds
   */
  public double getIntakeSpeed(){ return getIntakeAxis() - getOuttakeAxis(); }
  public double getElevatorAxis(){ return secondaryJoystick.getRawAxis(RobotConfig.controls.xbox_elevator_axis) * -1; }
  public double getThrottleAxis() { return secondaryJoystick.getRawAxis(RobotConfig.controls.throttle_elevator_axis); }

  // There are a few additional built in buttons you can use. Additionally,
  // by subclassing Button you can create custom triggers and bind those to
  // commands the same as any other Button.

  //// TRIGGERING COMMANDS WITH BUTTONS
  // Once you have a button, it's trivial to bind it to a button in one of
  // three ways:

  // Start the command when the button is pressed and let it run the command
  // until it is finished as determined by it's isFinished method.
  // button.whenPressed(new ExampleCommand());

  // Run the command while the button is being held down and interrupt it once
  // the button is released.
  // button.whileHeld(new ExampleCommand());

  // Start the command when the button is released and let it run the command
  // until it is finished as determined by it's isFinished method.
  // button.whenReleased(new ExampleCommand());
}
