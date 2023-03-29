package frc.robot.states;

import frc.robot.statemachine.Controllers;
import frc.robot.statemachine.RobotStateManager;
import frc.robot.statemachine.Controllers.*;
import frc.robot.statemachine.Event;
import frc.robot.statemachine.State;
import frc.robot.subsystems.*;
import frc.robot.subsystems.TurretvatorSubsystem.ElevatorStage;

public class TeleOpState implements State{

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
    driveSub.setBrake(false);
  }

  @Override
  public void Leave() {
    System.out.println("left " + name);
  }

/**DPAD 90 degree offset code*/
  private void handleTurretOffsets(){
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
  }

  @Override
  public void Periodic(RobotStateManager rs) {
    //arcade drive code w/ slow controller
    double xSpeed = controllers.get(ControllerName.DRIVE, Axis.LY) * (1-controllers.get(ControllerName.DRIVE, Axis.RT)/3.0);
    double zRotation = controllers.get(ControllerName.DRIVE, Axis.RX) / 2.0 * (1-controllers.get(ControllerName.DRIVE, Axis.RT)/3.0);
    driveSub.arcadeDrive(xSpeed,zRotation);

    //Manual elevator control
    tvSub.addElevator(-controllers.get(ControllerName.MANIP, Axis.RY)/50.0);

    handleTurretOffsets();
    tvSub.setTurret(controllers.get(ControllerName.MANIP, Axis.LX) *  -15.0 + turretOffset);
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
          driveSub.shift(false);
          // driveSub.setBrake(true);
          return true;
        default:
        }
        break;
      //Shift high
      case LB:
        switch(event.action){
        case PRESSED:
          driveSub.shift(true);
          // driveSub.setBrake(false);
          return true;
        default:
        }
        break;
      //Goes to the balance state
      // case B:
      //   switch (event.action) {
      //   case PRESSED:
      //     rs.setState("balancePlatform");
      //     return true;
      //   default:
      //   }
      //   break;
      //break mode
      case B:
        switch (event.action){
          case PRESSED:
            driveSub.setBrake(true);
            return true;
          default:
        }
        break;
      //coast mode
      case A:
        switch (event.action){
          case PRESSED:
            driveSub.setBrake(false);
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
        //Gripper open
        case B:
          if(event.action == Action.PRESSED){
            tvSub.setGripper(false);
            System.out.println("Gripper OPEN");
            return true;
          }
          break;
        //Gripper close
        case X:
          if(event.action == Action.PRESSED){
            tvSub.setGripper(true);
            System.out.println("Gripper CLOSE");
            return true;
          }
          break;
        //Elevator Down
        case A:
          if(event.action == Action.PRESSED){
            tvSub.elevatorSet(ElevatorStage.DOWN);
            return true;
          }
          break;
        //Elevator Middle
        case Y:
          if(event.action == Action.PRESSED){
            tvSub.elevatorSet(ElevatorStage.MIDDLE);
            return true;
          }
          break;
        //Elevator High
        case RB:
          if(event.action == Action.PRESSED){
            tvSub.elevatorSet(ElevatorStage.HIGH);
            return true;
          }
          break;
        //Elevator Load (human player station)
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
