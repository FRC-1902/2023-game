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
  private double turretOffset;
  private boolean isDpadHeld;
  private boolean wasDpadHeld;
  
  public TeleOpState(String name, String parent){
    this.name = name;
    this.parent = parent;
    driveSub = DriveSubsystem.getInstance();
    tvSub = TurretvatorSubsystem.getInstance();
    controllers = Controllers.getInstance();
    turretOffset = 0;
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
    turretOffset = 0;
  }

  @Override
  public void Leave() {
    System.out.println("left " + name);
  }

  @Override
  public void Periodic(RobotStateManager rs) {
    double xSpeed = controllers.get(ControllerName.DRIVE, Axis.LY) * (1-controllers.get(ControllerName.DRIVE, Axis.RT)/2.0);
    double zRotation = controllers.get(ControllerName.DRIVE, Axis.RX) / 2.0 * (1-controllers.get(ControllerName.DRIVE, Axis.RT)/2.0);
    driveSub.arcadeDrive(xSpeed,zRotation);

    tvSub.addElevator(-controllers.get(ControllerName.MANIP, Axis.RY)/50.0);

    if(controllers.getDPAD(ControllerName.MANIP) == -1){
      isDpadHeld = false;
    }else{
      isDpadHeld = true;
    }

    if(isDpadHeld != wasDpadHeld && isDpadHeld == true){
      switch(controllers.getDPAD(ControllerName.MANIP)){
        case 0:
          turretOffset = 0;
          break;
        case 270:
          if(turretOffset != 0){
            turretOffset = 0;
          }else{
            turretOffset = 90;
          }
          break;
        case 90:
          if(turretOffset != 0){
            turretOffset = 0;
          }else{
            turretOffset = -90;
          }
          break;
        default:
      }
    }

    wasDpadHeld = isDpadHeld;

    tvSub.setTurret(controllers.get(ControllerName.MANIP, Axis.LX) *  -15.0 + turretOffset);
    // if(controllers.getDPAD(ControllerName.MANIP) == 180) {
    //   tvSub.elevatorSet(ElevatorStage.DOWN);
    // } else if ( controllers.getDPAD(ControllerName.MANIP) == 270){
    //   tvSub.elevatorSet(ElevatorStage.MIDDLE);
    // } else if ( controllers.getDPAD(ControllerName.MANIP) == 0){
    //   tvSub.elevatorSet(ElevatorStage.HIGH);
    // }
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
          driveSub.setBrake(true);
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
          driveSub.setBrake(false);
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
      default: break;
      }
      break;
    //Manip Controller
    case MANIP:
      switch(event.button){
        case B:
          if(event.action == Action.PRESSED){
            tvSub.setGripper(false);
            System.out.println("Gripper OPEN");
            return true;
          }
          break;
        case X:
          if(event.action == Action.PRESSED){
            tvSub.setGripper(true);
            System.out.println("Gripper CLOSE");
            return true;
          }
          break;
        case A:
          if(event.action == Action.PRESSED){
            tvSub.elevatorSet(ElevatorStage.DOWN);
            return true;
          }
          break;
        case Y:
          if(event.action == Action.PRESSED){
            tvSub.elevatorSet(ElevatorStage.MIDDLE);
            return true;
          }
          break;
        case RB:
          if(event.action == Action.PRESSED){
            tvSub.elevatorSet(ElevatorStage.HIGH);
            return true;
          }
          break;
        case LB:
          if(event.action == Action.PRESSED){
            tvSub.elevatorSet(ElevatorStage.LOAD);
            return true;
          }
            break; 
        default:
          break;
      }
      break;
    }
    return false;
  }
}
