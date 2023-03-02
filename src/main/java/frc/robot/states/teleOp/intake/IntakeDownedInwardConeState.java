package frc.robot.states.teleOp.intake;

import frc.robot.Event;
import frc.robot.RobotStateManager;
import frc.robot.Controllers.Action;
import frc.robot.subsystems.IntakeSubsystem;

public class IntakeDownedInwardConeState implements frc.robot.State{
  private String name, parent;
  private IntakeSubsystem intakeSub;
  //TODO: write me
  public IntakeDownedInwardConeState(String name, String parent){
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
      case A:
        if(event.action == Action.RELEASED){
          rs.setState("deployIntake");
          return true;
        }
        break;
    //load piece
      case RB:
        if(event.action == Action.PRESSED){
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
