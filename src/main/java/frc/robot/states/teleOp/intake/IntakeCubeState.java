package frc.robot.states.teleOp.intake;

import frc.robot.Event;
import frc.robot.RobotStateManager;
import frc.robot.Controllers.Action;
import frc.robot.subsystems.IntakeSubsystem;

public class IntakeCubeState implements frc.robot.State{
  private String name, parent;
  private boolean isFinished;
  private IntakeSubsystem intakeSub;
  //TODO: write the state
  public IntakeCubeState(String name, String parent){
    this.name = name;
    this.parent = parent;
    intakeSub = IntakeSubsystem.getInstance();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getParent() {
    return parent;
  }

  @Override
  public void Enter() {
    System.out.println("entered " + name);
    isFinished = false;
    intakeSub.setRollerPow(0.2); //TODO: set me
    intakeSub.setLeverPow(0.2); //TODO: set me
  }

  @Override
  public void Leave() {
    System.out.println("left " + name);
  }

  @Override
  public void Periodic(RobotStateManager rs) {
    
  }
  
  @Override
  public boolean handleEvent(Event event, RobotStateManager rs) {
    switch(event.controllerName){
    //Manip Controller
    case MANIP:
      switch(event.button){
    //back
      case Y:
        if(event.action == Action.RELEASED){
          intakeSub.setRollerPow(0);
          rs.setState(parent);
          return true;
        }
        break;
    //load piece
      case RB:
        if(event.action == Action.PRESSED && isFinished){
          rs.setState("loadPiece");
          return true;
        }
        break;
      default:
      }
    default:
    }
    return false;
  }
}
