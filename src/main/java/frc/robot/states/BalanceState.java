package frc.robot.states;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import frc.robot.Constants;
import frc.robot.Event;
import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.Controllers.Action;
import frc.robot.Controllers.Button;
import frc.robot.Controllers.ControllerName;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.HeaderWrapper;
import frc.robot.subsystems.IMUSubsystem;

public class BalanceState implements State {
  private String name, parent;

  private DriveSubsystem drive;

  private PIDController yawPID;
  private IMUSubsystem imu;
  private HeaderWrapper compass;
  private State enteredFromState;

  private double desiredYaw;

  // Child states are supposed to modify these, so Periodic() can set the appropriate speed
  // without things getting all messed up.
  public double calculatedForwardSpeed, calculatedYawSpeed;

  private GenericEntry pidPWidget, pidIWidget, pidDWidget;
  
  public BalanceState(String name, String parent){
    this.name = name;
    this.parent = parent;
    imu = IMUSubsystem.getInstance();
    compass = new HeaderWrapper(0);
    drive = DriveSubsystem.getInstance();

    ShuffleboardLayout pidTuningTab = Shuffleboard.getTab(Constants.MAIN_SHUFFLEBOARD_TAB)
      .getLayout("Balance Yaw PID", BuiltInLayouts.kList)
      .withSize(2, 3);
    
    pidPWidget = pidTuningTab
      .add("Balance Yaw PID - Proportional", 0.0)
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
    // Checks whether or not the robot is coming from the back of the platform.
    if (Math.abs(imu.getX() - Constants.PLATFORM_YAW_DEG) > 180)
        desiredYaw = (desiredYaw + 180) % 360;

    compass.setHeadingOffset(desiredYaw);

    enteredFromState = enteredFrom;
  }

  @Override
  public void Leave() {
    System.out.println("left " + name);
  }

  @Override
  public void Periodic(RobotStateManager rs) {
    double currentYaw = compass.getHeading();

    yawPID.setP(pidPWidget.getDouble(0)/10);
    yawPID.setI(pidIWidget.getDouble(0)/10);
    yawPID.setD(pidDWidget.getDouble(0)/10);

    System.out.format("(BalanceState) Yaw: %3.1f, Pitch: %3.1f\n", currentYaw, imu.getZ());

    // Starts to drive onto the platform if it isn't, and it detects we have good yaw.
    if (rs.getCurrentState().equals(this) && Math.abs(currentYaw) < Constants.PLATFORM_BALANCE_YAW_THRESHOLD_DEG)
      rs.setState("drivePlatform");

    calculatedYawSpeed += yawPID.calculate(currentYaw);

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
