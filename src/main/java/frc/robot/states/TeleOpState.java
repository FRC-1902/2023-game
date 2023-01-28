package frc.robot.states;

import frc.robot.Controllers;
import frc.robot.Controllers.*;
import frc.robot.Event;
import frc.robot.RobotStateManager;
import frc.robot.subsystems.*;
import frc.robot.subsystems.DriveSubsystem.ShiftState;

public class TeleOpState implements frc.robot.State{

  private String name, parent;
  private DriveSubsystem driveSub;
  private Controllers controllers;
  
  public TeleOpState(String name, String parent){
    this.name = name;
    this.parent = parent;
    driveSub = DriveSubsystem.getInstance();
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
    driveSub.arcadeDrive(controllers.get(ControllerName.DRIVE, Axis.LY), controllers.get(ControllerName.DRIVE, Axis.RX));
  }

  @Override
  public boolean handleEvent(Event event, RobotStateManager rs) {
    switch(event.button){
  //Shift high
    case RB:
      switch(event.action){
      case PRESSED:
        driveSub.shift(ShiftState.LOW);
        return true;
      default:
          break;
      }
  //Shift low
    case LB:
      switch(event.action){
      case PRESSED:
        driveSub.shift(ShiftState.LOW);
        return true;
      default:
          break;
      }
    default:
      return false;
    }
  }
}