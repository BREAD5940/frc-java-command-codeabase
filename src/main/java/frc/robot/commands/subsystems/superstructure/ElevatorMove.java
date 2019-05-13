package frc.robot.commands.subsystems.superstructure;

import org.ghrobotics.lib.mathematics.units.Length;

import org.team5940.pantry.exparimental.command.SendableCommandBase;
import frc.robot.states.ElevatorState;
import frc.robot.subsystems.superstructure.SuperStructure;

public class ElevatorMove extends SendableCommandBase {

	private final ElevatorState mGoal;

	/**
	 * Move
	 * @param goal the goal state of the elevator
	 */
	public ElevatorMove(ElevatorState goal) {
		this(goal, "ElevatorMove to " + goal.toString());
	}

	public ElevatorMove(Length goal) {
		this(new ElevatorState(goal));
	}

	public ElevatorMove(ElevatorState goal, String name) {
		this.mGoal = goal;
		addRequirements(SuperStructure.getElevator());
		setName(name);
	}

	@Override
	public void initialize() {}

	@Override
	public void execute() {
		// como se dice "how should we do dis because elevator gravity feed forward sucks" en espa~nol?\
		// yo no sé, pero lo haré de todos modos
		// solución: periodic() otra vez 

		// double elevatorPercentVbusGravity = Elevator.getVoltage(SuperStructure.getInstance().updateState()) / 12;
		SuperStructure.getElevator().setPositionSetpoint(mGoal);

	}

	@Override
	public boolean isFinished() {
		return Math.abs(SuperStructure.getElevator().getHeight().getInch() - mGoal.height.getInch()) <= 3;
	}

}
