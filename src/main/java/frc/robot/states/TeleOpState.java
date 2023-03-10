package frc.robot.states;

import frc.robot.Controllers;
import frc.robot.Controllers.*;
import frc.robot.states.auto.TurretState;
import frc.robot.Event;
import frc.robot.RobotStateManager;
import frc.robot.subsystems.*;
import frc.robot.subsystems.TurretvatorSubsystem.ElevatorStage;

public class TeleOpState implements frc.robot.State{

  private String name, parent;
  private DriveSubsystem driveSub;
  private TurretvatorSubsystem tvSub;
  private Controllers controllers;
  
  public TeleOpState(String name, String parent){
    this.name = name;
    this.parent = parent;
    driveSub = DriveSubsystem.getInstance();
    tvSub = TurretvatorSubsystem.getInstance();
    controllers = Controllers.getInstance();
    
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
    double xSpeed = controllers.get(ControllerName.DRIVE, Axis.LY) * (1-controllers.get(ControllerName.DRIVE, Axis.RT)/2.0);
    double zRotation = controllers.get(ControllerName.DRIVE, Axis.RX) * (1-controllers.get(ControllerName.DRIVE, Axis.RT)/2.0);
    driveSub.arcadeDrive(xSpeed,zRotation);

    if(controllers.getDPAD(ControllerName.MANIP) == 180) {
      tvSub.elevatorSet(ElevatorStage.DOWN);
    } else if ( controllers.getDPAD(ControllerName.MANIP) == 270){
      tvSub.elevatorSet(ElevatorStage.MIDDLE);
    } else if ( controllers.getDPAD(ControllerName.MANIP) == 0){
      tvSub.elevatorSet(ElevatorStage.HIGH);
    }
  }

  @Override
  public boolean handleEvent(Event event, RobotStateManager rs) {
    switch(event.controllerName){
    //Drive Controller
    case DRIVE:
      switch(event.button){
      //Shift low
      case RB:
        switch(event.action){
        case PRESSED:
          System.out.println("Shifted LOW");
          driveSub.shift(false);
          return true;
        default:
        }
        break;
      //Shift high
      case LB:
        switch(event.action){
        case PRESSED:
          System.out.println("Shifted HIGH");
          driveSub.shift(true);
          return true;
        default:
        }
        break;
      // Goes to the balance state
      case B:
        switch (event.action) {
        case PRESSED:
          rs.setState("balancePlatform");
          return true;
        default:
        }
        break;
      default:
      }
    //Manip Controller
    case MANIP:
            switch(event.button){
                case B:
                    if(event.action == Action.PRESSED){
                        tvSub.setGripper(false);
                        System.out.println("gripper set false");
                        return true;
                    }
                    break;
                case X:
                    if(event.action == Action.PRESSED){
                        tvSub.setGripper(true);
                        System.out.println("gripper set true");
                        return true;
                    }
                    break;
            }
            break;

        }
        return false;
  }
}