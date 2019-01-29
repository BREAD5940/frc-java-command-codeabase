package frc.robot.lib;

import edu.wpi.first.wpilibj.command.PIDSubsystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import org.ghrobotics.lib.mathematics.units.Rotation2d;
import org.ghrobotics.lib.mathematics.units.TimeUnitsKt;
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnit;
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnitKt;
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnitRotationModel;
import org.ghrobotics.lib.wrappers.ctre.FalconSRX;

public abstract class AbstractRotatingArm extends PIDSubsystem {

  private ArrayList<FalconSRX<Rotation2d>> motors = new ArrayList<FalconSRX<Rotation2d>>();

  private PIDSettings pidSettings;

  private NativeUnitRotationModel mRotationModel;

  // public AbstractRotatingArm(PIDSettings settings, int motorPort) {
  //   this(settings, motorPort, null, 0);
  // }

  /**
   * Create an abstract rotating arm using PIDSettings, a List of ports and a FeedbackDevice.
   * as of right now this only supports mag encoders. Remember to also have PID input and 
   * output methods, as this is a PIDSubsystem
   * @param PIDSettigns for the PIDSubsystem to use
   * @param motorPort on the CAN Bus (for single talon arms)
   * @param sensor for the arm to use (ONLY MAG ENCODER TO USE)
   */
  public AbstractRotatingArm(PIDSettings settings, int motorPort, FeedbackDevice sensor) {
    this(settings, Arrays.asList(motorPort), sensor);
  }

  /**
   * Create an abstract rotating arm using PIDSettings, a List of ports and a FeedbackDevice.
   * as of right now this only supports mag encoders. Remember to also have PID input and 
   * output methods, as this is a PIDSubsystem
   * @param PIDSettigns for the PIDSubsystem to use
   * @param ports of talon CAN ports as a List
   * @param sensor for the arm to use (ONLY MAG ENCODER TO USE)
   */
  public AbstractRotatingArm(PIDSettings settings, List<Integer> ports, FeedbackDevice sensor) {
    super(settings.kp, settings.ki, settings.kd, settings.kf, 0.01f);
    
    NativeUnit unitsPerRotation = NativeUnitKt.getSTU(0);

    // TODO add support for more sensors
    if(sensor == FeedbackDevice.CTRE_MagEncoder_Relative) {
      unitsPerRotation = NativeUnitKt.getSTU(4096);
    }

    // add all of our talons to the list
    for( Integer i : ports ) {
      motors.add(new FalconSRX<Rotation2d>(i.intValue(), mRotationModel, TimeUnitsKt.getMillisecond(10)));
    }
  }

  /**
   * Get the master talon of the rotating arm
   */
  public FalconSRX<Rotation2d> getMaster() {
    return motors.get(0);
  }

  /**
   * Return an ArrayList of all the falconSRXes
   */
  public ArrayList<FalconSRX<Rotation2d>> getAllMotors() {
    return motors;
  }

  /**
   * Get the Rotation2d of the encoder of the master talon
   */
  public Rotation2d getRotation() {
    return getMaster().getSensorPosition();
  }

  /**
   * Set the position of the sensor to the given Rotation2d pos_
   * @param pos_ of the sensor as a Rotation2d
   */
  public void setRotation(Rotation2d pos_) {
    getMaster().setSensorPosition(pos_);
  }

}