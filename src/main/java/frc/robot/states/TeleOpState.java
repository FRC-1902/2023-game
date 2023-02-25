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
    
  }

  @Override
  public void Leave() {
    
  }

  @Override
  public void Periodic(RobotStateManager rs) {
    double xSpeed = controllers.get(ControllerName.DRIVE, Axis.LY) * (1-controllers.get(ControllerName.DRIVE, Axis.RT)/2.0);
    double zRotation = controllers.get(ControllerName.DRIVE, Axis.RX) * (1-controllers.get(ControllerName.DRIVE, Axis.RT)/2.0);
    driveSub.arcadeDrive(xSpeed,zRotation);
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
          driveSub.shift(ShiftState.LOW);
          return true;
        default: break;
        }
        break;
      //Shift high
      case LB:
        switch(event.action){
        case PRESSED:
          driveSub.shift(ShiftState.HIGH);
          return true;
        default: break;
        }
        break;
      // Goes to the balance state
      case Y:
        switch (event.action) {
        case PRESSED:
          rs.setState("balancePlatform");
          return true;
        default:
          break;
        }
        break;
      default: break;
      }
    //Manip Controller
    case MANIP:
    default: break;
    }
    return false;
  }
}