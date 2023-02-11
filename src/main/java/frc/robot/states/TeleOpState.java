package frc.robot.states;

import edu.wpi.first.wpilibj.RobotController;
import frc.robot.Constants;
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
  private long balancedPressedTimestamp;
  
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
            System.out.println("Shifted LOW");
            driveSub.shift(ShiftState.LOW);
            return true;
          default: break;
          }
          break;
      //Shift high
        case LB:
          switch(event.action){
          case PRESSED:
            System.out.println("Shifted HIGH");
            driveSub.shift(ShiftState.HIGH);
            return true;
          default: break;
          }
          break;
      // Goes to the balance state
        case Y:
          switch(event.action) {
          case PRESSED:
            System.out.println("Balance button depressed, waiting for release...");
            balancedPressedTimestamp = RobotController.getFPGATime();
            return true;
          case RELEASED:
            if (RobotController.getFPGATime() - balancedPressedTimestamp > Constants.ENTER_AUTO_DRIVE_BALANCE_THRESHOLD_US) {
              System.out.format(
                "Balance button depressed for more than %dus, going into `drivePlatform` state\n", 
                Constants.ENTER_AUTO_DRIVE_BALANCE_THRESHOLD_US
              );
              rs.setState("drivePlatform");
            }
            else {
              System.out.format(
                "Balance button depressed for less than %dus, going into `balance`\n", 
                Constants.ENTER_AUTO_DRIVE_BALANCE_THRESHOLD_US
              );
              rs.setState("balance");
            }
            return true;
          }
        default: break;
      }
  //Manip Controller
      case MANIP:
      default: break;
    }
    return false;
  }
}