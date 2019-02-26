package frc.robot.planners;

import java.util.Optional;

import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d;
import org.ghrobotics.lib.mathematics.units.LengthKt;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.SuperStructureConstants;
import frc.robot.commands.auto.routines.passthrough.PassThroughForward;
import frc.robot.commands.auto.routines.passthrough.PassThroughReverse;
import frc.robot.commands.subsystems.superstructure.ArmMove;
import frc.robot.commands.subsystems.superstructure.ArmWaitForElevator;
import frc.robot.commands.subsystems.superstructure.ElevatorMove;
import frc.robot.lib.obj.RoundRotation2d;
import frc.robot.states.SuperStructureState;
import frc.robot.subsystems.superstructure.SuperStructure;

/**
 * Instructions for using this mutant command-thing:
 *  - Do NOT call the constructor
 *  - Call the planner
 *  - Call SuperstructureMotion.getInstance().start();
 *    - This will iterate through the planned commandQueue
 *    - It'll end when it's done
 */
public class SuperstructureMotion extends Command {
	/* RELEVANT COMMANDS:
	  - ElevatorMove
	  - ArmMove
	  - ArmWaitForElevator
	 */

	private SuperstructureMotion() {
		requires(SuperStructure.getInstance());

		requires(SuperStructure.getInstance().getWrist());
		requires(SuperStructure.getInstance().getElbow());
		requires(SuperStructure.getElevator());
	}

	private static SuperstructureMotion instance_;
	protected CommandGroup queue = new CommandGroup();
	// protected CommandGroup eleQueue = new CommandGroup();
	// protected CommandGroup armQueue = new CommandGroup();
	protected Optional<Command> current;

	public static SuperstructureMotion getInstance() {
		if (instance_ == null) {
			instance_ = new SuperstructureMotion();
		}
		return instance_;
	}

	public boolean plan(SuperStructureState gsIn, SuperStructureState currentState) {
		var goalState = new SuperStructureState(gsIn);

		//CHECK if the current and goal match
		if (goalState.isEqualTo(currentState)) {
			return true;
		}
		//SAFE illegal inputs
		if (goalState.getElevatorHeight().getInch() > SuperStructureConstants.Elevator.top.getInch()) {
			goalState.getElevator().setHeight(SuperStructureConstants.Elevator.top); // constrain elevator to max height
		} else if (goalState.getElevatorHeight().getInch() < SuperStructureConstants.Elevator.bottom.getInch()) {
			goalState.getElevator().setHeight(SuperStructureConstants.Elevator.bottom); // constrain elevator to min height
		}

		if (goalState.getElbowAngle().getDegree() > SuperStructureConstants.Elbow.kElbowMax.getDegree()) {
			goalState.getElbow().setAngle(SuperStructureConstants.Elbow.kElbowMax); // Constrain elbow to max
		} else if (goalState.getElbowAngle().getDegree() < SuperStructureConstants.Elbow.kElbowMin.getDegree()) {
			goalState.getElbow().setAngle(SuperStructureConstants.Elbow.kElbowMin); // Constrain elbow to min
		}

		// FIXME so the issue here is that the maximum position of the wrist depends on the proximal (elbow) angle. So we have to measure it somehow yay. Also the sprocket on there means that the wrist will slowly rotate as the proximal joint rotates
		if (goalState.getWristAngle().getDegree() > SuperStructureConstants.Wrist.kWristMax.getDegree()) {
			goalState.getWrist().setAngle(SuperStructureConstants.Wrist.kWristMax); // constrain wrist to max
		} else if (goalState.getWristAngle().getDegree() < SuperStructureConstants.Wrist.kWristMin.getDegree()) {
			goalState.getWrist().setAngle(SuperStructureConstants.Wrist.kWristMin); // constrain wrist to min
		}

		//DEFINE the three goal points -- elevator, wrist, and end of intake
		var GPelevator = new Translation2d(LengthKt.getInch(0), goalState.getElevatorHeight());
		var GPwrist = new Translation2d(LengthKt.getInch(goalState.getElbowAngle().getCos() * SuperStructureConstants.Elbow.carriageToIntake.getInch()),
				LengthKt.getInch(goalState.getElbowAngle().getSin() * SuperStructureConstants.Elbow.carriageToIntake.getInch()).plus(GPelevator.getY()));
		var GPeoi = new Translation2d(LengthKt.getInch(goalState.getWristAngle().getCos() * SuperStructureConstants.Wrist.intakeOut.getInch()).plus(GPwrist.getX()),
				LengthKt.getInch(goalState.getWristAngle().getSin() * SuperStructureConstants.Wrist.intakeOut.getInch()).plus(GPwrist.getY()));

		//DEFINE the three start points -- elevator, wrist, and end of intake
		var SPelevator = new Translation2d(LengthKt.getInch(0), currentState.getElevatorHeight());
		var SPwrist = new Translation2d(LengthKt.getInch(currentState.getElbowAngle().getCos() * SuperStructureConstants.Elbow.carriageToIntake.getInch()),
				LengthKt.getInch(currentState.getElbowAngle().getSin() * SuperStructureConstants.Elbow.carriageToIntake.getInch()).plus(SPelevator.getY()));
		var SPeoi = new Translation2d(LengthKt.getInch(currentState.getWristAngle().getCos() * SuperStructureConstants.Wrist.intakeOut.getInch()).plus(SPwrist.getX()),
				LengthKt.getInch(currentState.getWristAngle().getSin() * SuperStructureConstants.Wrist.intakeOut.getInch()).plus(SPwrist.getY()));

		//SAFE potential crashes on the end state

		if (GPeoi.getY().getInch() < SuperStructureConstants.electronicsHeight.getInch()) {
			var tempTheta = goalState.getWristAngle();
			tempTheta = RoundRotation2d.getRadian(Math.asin((GPeoi.getY().getInch() - GPwrist.getY().getInch()) / SuperStructureConstants.Wrist.intakeOut.getInch()));
			goalState.getWrist().setAngle(tempTheta);
			GPeoi = new Translation2d(GPeoi.getX(), LengthKt.getInch(Math.sin(tempTheta.getRadian()) * SuperStructureConstants.Wrist.intakeOut.getInch()).plus(GPwrist.getY()));
		}

		if (GPwrist.getY().getInch() < SuperStructureConstants.electronicsHeight.getInch()) {
			var tempTheta = goalState.getElbowAngle();
			tempTheta = RoundRotation2d.getRadian(Math.asin((GPwrist.getY().getInch() - GPelevator.getY().getInch()) / SuperStructureConstants.Elbow.carriageToIntake.getInch()));
			goalState.getElbow().setAngle(tempTheta);
			GPwrist = new Translation2d(GPwrist.getX(), LengthKt.getInch(Math.sin(tempTheta.getRadian()) * SuperStructureConstants.Elbow.carriageToIntake.getInch()).plus(GPelevator.getY()));

		}

		if (GPwrist.getX().getInch() > 0 && SPwrist.getX().getInch() < 0) {
			queue.addSequential(new PassThroughReverse());
		} else if (GPwrist.getX().getInch() < 0 && SPwrist.getX().getInch() > 0) {
			queue.addSequential(new PassThroughForward());
		}

		//CLEAR the queue
		queue = new CommandGroup();

		//CHECK the position of the intake -- hatch or cargo
		// IF it's a long climb
		boolean isLongClimb = Math.abs(goalState.getElevatorHeight().minus(currentState.getElevatorHeight()).getInch()) >= SuperStructureConstants.Elevator.longClimb.getInch();

		if (isLongClimb) {
			queue.addParallel(new ArmMove(SuperStructure.iPosition.STOWED));
		}
		//CHECK if the elevator point is in proximity to the crossbar

		if ((GPelevator.getY().getInch() < SuperStructureConstants.Elevator.crossbarBottom.getInch() && SPelevator.getY().getInch() > SuperStructureConstants.Elevator.crossbarBottom.getInch())
				|| (GPelevator.getY().getInch() > SuperStructureConstants.Elevator.crossbarBottom.getInch() && SPelevator.getY().getInch() < SuperStructureConstants.Elevator.crossbarBottom.getInch())
				|| (GPelevator.getY().getInch() < SuperStructureConstants.Elevator.crossbarBottom.plus(SuperStructureConstants.Elevator.crossbarWidth).getInch()
						&& GPelevator.getY().getInch() > SuperStructureConstants.Elevator.crossbarBottom.getInch())) {
			queue.addSequential(new ArmMove(SuperStructure.iPosition.STOWED));
		}

		queue.addParallel(new ArmWaitForElevator(goalState.getAngle(), goalState.getElevatorHeight(), LengthKt.getInch(3),
				goalState.getElevatorHeight().getInch() < currentState.getElevatorHeight().getInch()));
		queue.addSequential(new ElevatorMove(goalState.getElevator()));

		return true;
	}

	@Override
	protected void initialize() {
		queue.start();
	}

	@Override
	protected boolean isFinished() {
		return queue.isCompleted();
	}

}
