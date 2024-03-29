package frc.robot.states;

import edu.wpi.first.wpilibj.DataLogManager;
import frc.robot.statemachine.Controllers;
import frc.robot.statemachine.RobotStateManager;
import frc.robot.statemachine.Controllers.*;
import frc.robot.statemachine.Event;
import frc.robot.statemachine.State;
import frc.robot.subsystems.*;
import frc.robot.subsystems.TurretvatorSubsystem.ElevatorStage;

public class TeleOpState implements State{

  private String name;
  private String parent;
  private DriveSubsystem driveSubsystem;
  private TurretvatorSubsystem tvSubsystem;
  private LEDSubsystem ledSubsystem;
  private Controllers controllers;
  private double turretOffset;
  private boolean wasDpadHeld;
  
  public TeleOpState(String name, String parent){
    this.name = name;
    this.parent = parent;
    driveSubsystem = DriveSubsystem.getInstance();
    tvSubsystem = TurretvatorSubsystem.getInstance();
    controllers = Controllers.getInstance();
    ledSubsystem = LEDSubsystem.getInstance();
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
  public void enter() {
    turretOffset = 0;
    driveSubsystem.setBrake(false);
  }

  @Override
  public void leave() {
  }

/**DPAD 90 degree offset code*/
  private void handleTurretOffsets(){
    boolean isDpadHeld;
    if(controllers.getDPAD(ControllerName.MANIP) == -1){
      isDpadHeld = false;
    }else{
      isDpadHeld = true;
    }

    if(isDpadHeld != wasDpadHeld && isDpadHeld){
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
  public void periodic(RobotStateManager rs) {
    //arcade drive code w/ slow controller
    double xSpeed = controllers.get(ControllerName.DRIVE, Axis.LY);
    double zRotation = controllers.get(ControllerName.DRIVE, Axis.RX);
    driveSubsystem.curvedArcadeDrive(xSpeed,zRotation,1-controllers.get(ControllerName.DRIVE, Axis.RT)/3.0);

    //Manual elevator control
    tvSubsystem.addElevator(-controllers.get(ControllerName.MANIP, Axis.RY)/50.0);

    handleTurretOffsets();
    tvSubsystem.setTurret(controllers.get(ControllerName.MANIP, Axis.LX) *  -15.0 + turretOffset);
  }

  @Override
  public boolean handleEvent(Event event, RobotStateManager rs) {
    //Drive Controller
    if(event.controllerName == ControllerName.DRIVE){
      switch(event.button){
        //Shift low
        case RB:
          if(event.action == Action.PRESSED){
            driveSubsystem.shift(false);
            return true;
          }
          break;
        //Shift high
        case LB:
          if(event.action == Action.PRESSED){
            driveSubsystem.shift(true);
            return true;
          }
          break;
        //break mode
        case B:
          if(event.action == Action.PRESSED){
            driveSubsystem.setBrake(true);
            return true;
          }
          break;
        //coast mode
        case A:
          if(event.action == Action.PRESSED){
            driveSubsystem.setBrake(false);
            return true;
          }
          break;
        //cone hp signal
        case Y:
          if(event.action == Action.PRESSED){
            ledSubsystem.setTemporaryRGB(2000, 230, 80, 0); // cone color
            return true;
          }
          break;
        //cube hp signal
        case X:
          if(event.action == Action.PRESSED){
            ledSubsystem.setTemporaryRGB(2000, 20, 4, 28); // cube color
            return true;
          }
          break;
        default: break;
      }
    //Manip Controller
    }else if(event.controllerName == ControllerName.MANIP){
      switch(event.button){
        //Gripper open
        case B:
          if(event.action == Action.PRESSED){
            tvSubsystem.setGripper(false);
            DataLogManager.log("Gripper OPEN");
            return true;
          }
          break;
        //Gripper close
        case X:
          if(event.action == Action.PRESSED){
            tvSubsystem.setGripper(true);
            DataLogManager.log("Gripper CLOSE");
            return true;
          }
          break;
        //Elevator Down
        case A:
          if(event.action == Action.PRESSED){
            tvSubsystem.elevatorSet(ElevatorStage.DOWN);
            return true;
          }
          break;
        //Elevator Middle
        case Y:
          if(event.action == Action.PRESSED){
            tvSubsystem.elevatorSet(ElevatorStage.MIDDLE);
            return true;
          }
          break;
        //Elevator High
        case RB:
          if(event.action == Action.PRESSED){
            tvSubsystem.elevatorSet(ElevatorStage.HIGH);
            return true;
          }
          break;
        //Elevator Load (human player station)
        case LB:
          if(event.action == Action.PRESSED){
            tvSubsystem.elevatorSet(ElevatorStage.LOAD);
            return true;
          }
            break; 
        default: break;
      }
    }
    return false;
  }
}
