package frc.robot.states.balance;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.DoubleEntry;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.states.BalanceState;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IMUSubsystem;

public class BalanceOnPlatformState implements State {
  private String name;
  private BalanceState parent;

  private IMUSubsystem imu;
  private PIDController pitchPID;

  private GenericEntry pidPWidget, pidIWidget, pidDWidget;

  public BalanceOnPlatformState(String name, String parent){
    this.name = name;
    this.parent = (BalanceState) RobotStateManager.getInstance().findState("balance");

    ShuffleboardTab pidTuningTab = Shuffleboard.getTab("PID Tuning");
    
    pidPWidget = pidTuningTab.add("Balance On Platform PID - Proportional", 0.0).withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    pidIWidget = pidTuningTab.add("Balance On Platform PID - Integral", 0.0).withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    pidDWidget = pidTuningTab.add("Balance On Platform PID - Derivative", 0.0).withWidget(BuiltInWidgets.kNumberSlider).getEntry();

    imu = IMUSubsystem.getInstance();
    pitchPID = new PIDController(0, 0, 0);
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
  }

  @Override
  public void Leave() {
    System.out.println("left " + name);
  }

  @Override
  public void Periodic(RobotStateManager rs) {
    pitchPID.setP(pidPWidget.getDouble(0));
    pitchPID.setI(pidIWidget.getDouble(0));
    pitchPID.setD(pidDWidget.getDouble(0));

    parent.calculatedForwardSpeed += pitchPID.calculate(imu.getZ(), 0);
  }
}
