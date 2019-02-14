package frc.robot.commands.subsystems.superstructure;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.states.SuperStructureState;
import frc.robot.subsystems.superstructure.SuperStructure;

public class SuperstructureGoToState extends Command {
	SuperStructureState mRequState;

	public SuperstructureGoToState(SuperStructureState requState) {
		requires(SuperStructure.getInstance());
		mRequState = requState;
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		SuperStructure.getInstance().setReqState(mRequState);
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		//do nothing? i guess?
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return (mRequState.getElevatorHeight().getInch()-SuperStructure.getInstance().updateState().getElevatorHeight().getInch())<=2;
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {}
}
