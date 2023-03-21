package frc.robot.states.balance;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import frc.robot.Constants;
import frc.robot.PID;
import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.sensors.IMU;
import frc.robot.states.BalanceState;

public class BalanceOnPlatformState implements State {
  private String name;
  private BalanceState parent;

  private IMU imu;
  private PID pitchPID;

  private GenericEntry pidPWidget, pidIWidget, pidDWidget;

  public BalanceOnPlatformState(String name, String parent){
    this.name = name;
    this.parent = (BalanceState) RobotStateManager.getInstance().findState("balance");

    ShuffleboardLayout pidTuningTab = Shuffleboard.getTab(Constants.PID_SHUFFLEBOARD_TAB)
      .getLayout("Balance On Platform PID", BuiltInLayouts.kList)
      .withSize(2, 3);
    
    //TODO: tune me once robot is built
    pidPWidget = pidTuningTab
      .add("Balance On Platform PID - Proportional", 0.1)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    pidIWidget = pidTuningTab
      .add("Balance On Platform PID - Integral", 0.0)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    pidDWidget = pidTuningTab
      .add("Balance On Platform PID - Derivative", 0.0)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
      //.withProperties(Map.of("Min", -0.15, "Max", 0.15))

    imu = IMU.getInstance();
    pitchPID = new PID(()->imu.getPitch(), 0.0, 0.0, 0.0, 0.0);
    pitchPID.setTolerance(0.05);
    pitchPID.setSetpoint(0);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getParent() {
    return parent.getName();
  }

  @Override
  public void Enter() {
    System.out.println("entered" + name);
    pitchPID.startThread();
  }

  @Override
  public void Leave() {
    pitchPID.stopThread();
    System.out.println("left " + name);
  }

  @Override
  public void Periodic(RobotStateManager rs) {
    // NOTE: These are all divided by 10 from the values displayed in shuffleboard. This is just in order to get more precision.
    pitchPID.setP(pidPWidget.getDouble(0)/10);
    pitchPID.setI(pidIWidget.getDouble(0)/10);
    pitchPID.setD(pidDWidget.getDouble(0)/10);
    if(Math.abs(imu.getPitch()) > Constants.PLATFORM_BALANCE_PITCH_THRESHOLD_DEG){
      parent.calculatedForwardSpeed += pitchPID.getOutput();
    }

    System.out.format("(BalanceOnPlatform) Current forward speed %f\n", parent.calculatedForwardSpeed);
  }
}
