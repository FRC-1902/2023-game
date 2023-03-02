package frc.robot.states.teleOp.intake;

import frc.robot.Event;
import frc.robot.RobotStateManager;
import frc.robot.Controllers.Action;

public class IntakeCubeState implements frc.robot.State{
  private String name, parent;
  //TODO: write me
  public IntakeCubeState(String name, String parent){
    this.name = name;
    this.parent = parent;
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
      case A:
        if(event.action == Action.RELEASED){
          rs.setState("deployIntake");
          return true;
        }
        break;
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
