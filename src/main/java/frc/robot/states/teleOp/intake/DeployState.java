package frc.robot.states.teleOp.intake;

import frc.robot.Event;
import frc.robot.RobotStateManager;
import frc.robot.Controllers.Action;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.IntakeSubsystem.DeployStage;

public class DeployState implements frc.robot.State{
  private String name, parent;
  private IntakeSubsystem intakeSub;
  
  public DeployState(String name, String parent){
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
    intakeSub.deployIntake(DeployStage.DOWN);
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
        if(event.action == Action.PRESSED && intakeSub.isDeployed()){
          rs.setState("intakeCube");
          return true;
        }
        break;
      case B:
        if(event.action == Action.PRESSED && intakeSub.isDeployed()){
          rs.setState("intakeDownedCone");
          return true;
        }
        break;
      case X:
        if(event.action == Action.PRESSED && intakeSub.isDeployed()){
          rs.setState("intakeDownedInwardCone");
          return true;
        }
        break;
      case RB:
        if(event.action == Action.PRESSED){
          rs.setState(parent);
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
