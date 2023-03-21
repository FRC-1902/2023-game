package frc.robot.states;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import frc.robot.Constants;
import frc.robot.Controllers.Action;
import frc.robot.Controllers.Button;
import frc.robot.Controllers.ControllerName;
import frc.robot.sensors.IMU;
import frc.robot.Event;
import frc.robot.PID;
import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.DriveSubsystem;

public class BalanceState implements State {
  private String name, parent;

  private DriveSubsystem drive;

  private PID yawPID;
  private IMU imu;
  private State enteredFromState;

  public double calculatedForwardSpeed, calculatedYawSpeed;

  private GenericEntry pidPWidget, pidIWidget, pidDWidget;
  
  public BalanceState(String name, String parent){
    this.name = name;
    this.parent = parent;
    imu = new IMU();
    drive = DriveSubsystem.getInstance();

    ShuffleboardLayout pidTuningTab = Shuffleboard.getTab(Constants.PID_SHUFFLEBOARD_TAB)
      .getLayout("Balance Yaw PID", BuiltInLayouts.kList)
      .withSize(2, 3);
    
    //TODO: tune me once robot is built
    pidPWidget = pidTuningTab
      .add("Balance Yaw PID - Proportional", 0.1)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    pidIWidget = pidTuningTab
      .add("Balance Yaw PID - Integral", 0.0)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    pidDWidget = pidTuningTab
      .add("Balance Yaw PID - Derivative", 0.0)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    
    yawPID = new PID(()->imu.getHeading(), 0.01, 0.0, 0.0, 0.0);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getParent() {
    return parent;
  }

  // Dont ask
  @Override
  public void Enter() {}

  @Override
  public void Enter(State enteredFrom) {
    System.out.println("entered" + name);

    drive.shift(false);

    yawPID.startThread();

    enteredFromState = enteredFrom;
  }

  @Override
  public void Leave() {
    yawPID.stopThread();
    drive.arcadeDrive(0, 0);
    System.out.println("left " + name);
  }

  @Override
  public void Periodic(RobotStateManager rs) {
    double headingTarget;
    double currentYaw = imu.getHeading();

    yawPID.setP(pidPWidget.getDouble(0.1)/10);
    yawPID.setI(pidIWidget.getDouble(0)/10);
    yawPID.setD(pidDWidget.getDouble(0)/10);

    
    if(currentYaw > 270){
      currentYaw -= 360;
    }

    if(currentYaw < 270 && currentYaw > 90){
      headingTarget = 180;
    }else{
      headingTarget = 0;
    }

    yawPID.setSetpoint(headingTarget);
    calculatedYawSpeed = yawPID.getOutput();

    // System.out.format("(BalanceState) Yaw: %3.1f, YawSpeed: %3.1f, Setpoint: %3.1f\n", currentYaw, calculatedYawSpeed, yawPID.getSetpoint());

    drive.arcadeDrive(calculatedForwardSpeed, calculatedYawSpeed);
    
    calculatedForwardSpeed = 0;
    calculatedYawSpeed = 0;
  }

  public boolean handleEvent(Event event, RobotStateManager rs) {
    if (event.controllerName == ControllerName.DRIVE && event.button == Button.B && event.action == Action.RELEASED) {
      rs.setState(enteredFromState.getName());
      return true;
    }

    return false;
  }

}
