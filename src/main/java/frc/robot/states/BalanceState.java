package frc.robot.states;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import frc.robot.Constants;
import frc.robot.Controllers.Action;
import frc.robot.Controllers.Button;
import frc.robot.Controllers.ControllerName;
import frc.robot.Event;
import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.HeaderWrapper;

public class BalanceState implements State {
  private String name, parent;

  private DriveSubsystem drive;

  private PIDController yawPID;
  private HeaderWrapper compass;
  private State enteredFromState;

  private double desiredYaw;

  public double calculatedForwardSpeed, calculatedYawSpeed;

  private GenericEntry pidPWidget, pidIWidget, pidDWidget;
  
  public BalanceState(String name, String parent){
    this.name = name;
    this.parent = parent;
    compass = new HeaderWrapper(0);
    drive = DriveSubsystem.getInstance();

    ShuffleboardLayout pidTuningTab = Shuffleboard.getTab(Constants.MAIN_SHUFFLEBOARD_TAB)
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

    yawPID = new PIDController(0, 0, 0);

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

    desiredYaw = Constants.PLATFORM_YAW_DEG;

    compass.setHeadingOffset(compass.getHeadingOffset() + desiredYaw);

    enteredFromState = enteredFrom;
  }

  @Override
  public void Leave() {
    System.out.println("left " + name);
  }

  @Override
  public void Periodic(RobotStateManager rs) {
    double headingTarget;
    double currentYaw = compass.getHeading();

    yawPID.setP(pidPWidget.getDouble(0)/10);
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
    calculatedYawSpeed = yawPID.calculate(currentYaw);

    System.out.format("(BalanceState) Yaw: %3.1f, YawSpeed: %3.1f, Setpoint: %3.1f\n", currentYaw, calculatedYawSpeed, yawPID.getSetpoint());

    drive.arcadeDrive(calculatedForwardSpeed, calculatedYawSpeed);
    drive.shift(DriveSubsystem.ShiftState.LOW);
    
    calculatedForwardSpeed = 0;
    calculatedYawSpeed = 0;
  }

  public boolean handleEvent(Event event, RobotStateManager rs) {
    if (event.controllerName == ControllerName.DRIVE && event.button == Button.Y && event.action == Action.RELEASED) {
      rs.setState(enteredFromState.getName());
      return true;
    }
    return false;
  }
}
