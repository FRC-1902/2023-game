package frc.robot.states.balance;

import frc.robot.Constants;
import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.states.BalanceState;
import frc.robot.subsystems.IMUSubsystem;

public class DriveOntoPlatformState implements State {
  private String name;
  private BalanceState parent;

  private IMUSubsystem imu;

  public DriveOntoPlatformState(String name, String parent){
    this.name = name;
    this.parent = (BalanceState) RobotStateManager.getInstance().findState("balance");
    imu = IMUSubsystem.getInstance();
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
    // Initiate state transition to BalanceOnPlatform, since it detects that we're now on it.
    if (imu.getZ() > Constants.PLATFORM_BALANCE_PITCH_THRESHOLD_DEG) {
      rs.setState("balancePlatform");
      return;
    }

    parent.calculatedForwardSpeed += 100;
  }
}
