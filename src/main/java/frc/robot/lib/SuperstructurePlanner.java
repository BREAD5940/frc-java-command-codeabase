package frc.robot.lib;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.RobotConfig;
import frc.robot.commands.auto.AutoMotion.mHeldPiece;
import frc.robot.commands.subsystems.superstructure.wrist.SetWrist;
import frc.robot.states.SuperstructureState;
import frc.robot.subsystems.superstructure.SuperStructure.iPosition;

/**
 * Plans the best motion of the superstructure based on the inputted current
 * SuperstructureState and a goal SuperstructureState. General idea is to get
 * from point A to point B without breaking anything. TODO find the actual
 * values of the angles/heights. this will probably have to wait until the robot
 * is done
 * 
 * @author Jocelyn McHugo
 */
public class SuperstructurePlanner{

  public SuperstructurePlanner(){}

  //TODO add values for certain elevator positions (ex. the wrist can be <0 if the elevator is >10)

  //TODO get actual irl angles TODO make the names less horrible
  
  static final double minUnCrashHeight=5; //min elevator height + how much intake is below the bottom of the elevator

  static final double crossbarHeight = 20;

  static final double maxHeight = RobotConfig.elevator.elevator_maximum_height;

  boolean intakeCrashable = false; //intake capable of hitting the ground
  boolean intakeAtRisk = false; //intake at risk of hitting the crossbar
  int errorCount; //number of errors in motion
  int corrCount; //number of corrected items in motion
  SuperstructureState currentPlannedState;

  /**
   * Creates a command group of superstructure motions that will prevent any damage to the intake/elevator
   * @param goalStateIn
   *    the desired SuperstructureState
   * @param currentState
   *    the current SuperstructureState
   * @return
   *    the ideal command group to get from the currentState to the goalState
   */
  public CommandGroup plan(SuperstructureState goalStateIn, SuperstructureState currentState){
    CommandGroup toReturn = new CommandGroup();
    SuperstructureState goalState = new SuperstructureState(goalStateIn);
    errorCount=corrCount=0;
    boolean defAngle=false;

    if(iPosition.presets.contains(goalState.getAngle())){
      defAngle=true;
    }

    if(goalState==currentState){
      System.out.println("MOTION UNNECESSARY -- Goal and current states are same. Exiting planner.");
      this.currentPlannedState=goalState;
      return toReturn;
    }

    if(goalState.getHeldPiece()!=currentState.getHeldPiece()){
      System.out.println("MOTION IMPOSSIBLE -- Superstructure motion cannot change heldPiece. Resolving error.");
      errorCount++;
      corrCount++;
      goalState.setHeldPiece(currentState.getHeldPiece());
    }

    if(!defAngle){
      System.out.println("MOTION UNSAFE -- Wrist position is wildcard. Setting to default position for movement.");
      errorCount++;
      if(currentState.getHeldPiece()==mHeldPiece.HATCH){
        //TODO change this so it only happens if the intake will ACTUALLY pass through the elevator
        System.out.println("MOTION UNSAFE -- Cannot move wrist to wildcard position while holding hatch. Aborting wrist movement.");
        errorCount++;
        corrCount++;
        goalState.setAngle(iPosition.CARGO_GRAB);
      }else{
        toReturn.addSequential(new SetWrist(iPosition.CARGO_GRAB));
        intakeAtRisk=false;
        intakeCrashable=false;
      }
    }else{
      // Checks if the intake will ever be inside the elevator
      if((currentState.getAngle()==iPosition.HATCH) || (goalState.getAngle()==iPosition.HATCH)){
            intakeAtRisk=true;
      }

      //checks if the intake will tilt/is tilted below the bottom of the elevator
      if((goalState.getAngle()==iPosition.CARGO_DOWN) ||(currentState.getAngle()==iPosition.CARGO_DOWN)){
        intakeCrashable=true;
      }
    }

    //checks if the elevator will go to high
    if(goalState.getElevatorHeight()>maxHeight){
      System.out.println("MOTION IMPOSSIBLE -- Elevator will pass maximum height. Setting to maximum height.");
      errorCount++;
      corrCount++;
      goalState.setElevatorHeight(maxHeight);
    }

    //checks if the elevator will move past the crossbar
    if(intakeAtRisk&&(goalState.getElevatorHeight()>=crossbarHeight&&currentState.getElevatorHeight()<=crossbarHeight)
        || (goalState.getElevatorHeight()<=crossbarHeight&&currentState.getElevatorHeight()>=crossbarHeight)){
      System.out.println("MOTION UNSAFE -- Intake will hit crossbar. Setting to default intake position for movement.");
      errorCount++;
      toReturn.addSequential(new SetWrist(iPosition.CARGO_GRAB)); //Keeps intake outside the elevator so it doesn't hit the crossbar
    }else{
      intakeAtRisk=false;
    }
    
    //checks if the elevator will move low enough to crash the intake
    if (goalState.getElevatorHeight()<=minUnCrashHeight&&intakeCrashable){
      System.out.println("MOTION UNSAFE -- Intake will hit ground. Setting to default intake position.");
      errorCount++;
      corrCount++;
      goalState.setAngle(iPosition.CARGO_GRAB);
    }else{
      intakeCrashable=false;
    }

    //move to corrected state
    // toReturn.addSequential(new SetElevatorHeight(goalState.getElevatorHeight())); //FIXME so this makes the whole thing die
    currentState.setElevatorHeight(goalState.getElevatorHeight());
    toReturn.addSequential(new SetWrist(goalState.getAngle()));
    currentState.setAngle(goalState.getAngle());

    System.out.println("MOTION COMPLETED -- "+Integer.valueOf(errorCount)+" error(s) and "
      +Integer.valueOf(corrCount)+" final correction(s)");
    this.currentPlannedState = currentState;
    return toReturn;
  }

  public SuperstructureState getPlannedState(SuperstructureState goalStateIn, SuperstructureState currentState){
    this.plan(goalStateIn, currentState);
    return this.currentPlannedState;
  }
}