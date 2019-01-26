package frc.robot.commands.subsystems.drivetrain;

import java.util.function.Supplier;

import com.team254.lib.physics.DifferentialDrive;
import com.team254.lib.physics.DifferentialDrive.ChassisState;
import com.team254.lib.physics.DifferentialDrive.WheelState;

import org.ghrobotics.lib.debug.LiveDashboard;
import org.ghrobotics.lib.localization.Localization;
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryTracker;
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d;
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature;
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedEntry;
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory;
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TrajectorySamplePoint;
import org.ghrobotics.lib.mathematics.units.TimeUnitsKt;
import org.ghrobotics.lib.subsystems.drive.TrajectoryTrackerOutput;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotConfig;
import frc.robot.subsystems.DriveTrain;

// @SuppressWarnings({"WeakerAccess", "unused"})
public class TrajectoryTrackerCommand extends Command {
  private TrajectoryTracker trajectoryTracker;
  private Supplier<TimedTrajectory<Pose2dWithCurvature>> trajectorySource;
  private DriveTrain driveBase;
  private Localization localization;
  private boolean reset;
  private TrajectoryTrackerOutput output;
  private DifferentialDrive mModel;

  public TrajectoryTrackerCommand(DriveTrain driveBase, Supplier<TimedTrajectory<Pose2dWithCurvature>> trajectorySource){
      this(driveBase, trajectorySource, false);
  }

  public TrajectoryTrackerCommand(DriveTrain driveBase, Supplier<TimedTrajectory<Pose2dWithCurvature>> trajectorySource, boolean reset){
      this(driveBase, Robot.drivetrain.getTrajectoryTracker(), trajectorySource, reset);
  }

  public TrajectoryTrackerCommand(DriveTrain driveBase, TrajectoryTracker trajectoryTracker, Supplier<TimedTrajectory<Pose2dWithCurvature>> trajectorySource, boolean reset){
      requires(driveBase);
      this.driveBase = driveBase;
      this.trajectoryTracker = trajectoryTracker;
      this.trajectorySource = trajectorySource;
      this.reset = reset;
      this.mModel = driveBase.getDifferentialDrive();
  }

  @Override
  protected void initialize(){
      trajectoryTracker.reset(trajectorySource.get());

      if(reset) {
          localization.reset(trajectorySource.get().getFirstState().component1().getPose());
      }

      LiveDashboard.INSTANCE.setFollowingPath(true);
  }

  @Override
  protected void execute(){
    output = trajectoryTracker.nextState(driveBase.getRobotPosition(), TimeUnitsKt.getMillisecond(System.currentTimeMillis()));

    TrajectorySamplePoint<TimedEntry<Pose2dWithCurvature>> referencePoint = trajectoryTracker.getReferencePoint();
    if(referencePoint != null){
      Pose2d referencePose = referencePoint.getState().getState().getPose();

      LiveDashboard.INSTANCE.setPathX(referencePose.getTranslation().getX().getFeet());
      LiveDashboard.INSTANCE.setPathY(referencePose.getTranslation().getY().getFeet());
      LiveDashboard.INSTANCE.setPathHeading(referencePose.getRotation().getRadian());
    }

    driveBase.setOutput(output);
  }

  @Override
  protected void end(){
      driveBase.stop();
      LiveDashboard.INSTANCE.setFollowingPath(false);
  }

  @Override
  protected boolean isFinished() {
      return trajectoryTracker.isFinished();
  }
}