package frc.robot.states.balance;

import frc.robot.Constants;
import frc.robot.Event;
import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.TurretvatorSubsystem;
import frc.robot.subsystems.TurretvatorSubsystem.ElevatorStage;
import frc.robot.sensors.IMU;

public class AutoBalanceState implements State {
  private String name, parent;

  private DriveSubsystem driveSub;
  private TurretvatorSubsystem tvSub;

  private IMU imu;
  private State enteredFromState;
  
  public AutoBalanceState(String name, String parent){
    this.name = name;
    this.parent = parent;
    imu = IMU.getInstance();
    driveSub = DriveSubsystem.getInstance();
    tvSub = TurretvatorSubsystem.getInstance();
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
    tvSub.elevatorSet(ElevatorStage.DOWN);

    driveSub.shift(false);
 }

  @Override
  public void Leave() {
    driveSub.arcadeDrive(0.0, 0.0);
    System.out.println("left " + name);
  }

  @Override
  public void Periodic(RobotStateManager rs) {
    double output = 0.0;
    if(imu.getPitch() > Constants.PLATFORM_BALANCE_PITCH_THRESHOLD_DEG && imu.getPitch() > 0){
      output =  -.06;
    } else if(imu.getPitch() < Constants.PLATFORM_BALANCE_PITCH_THRESHOLD_DEG && imu.getPitch() < 0) {
      output = .06;
    }

    if((int)(System.currentTimeMillis() / 100) % 10 == 0){
      System.out.format("Angle: %.3f | Output: %.3f\n", imu.getPitch(), output);
    }
    
    driveSub.arcadeDrive(output, 0.0);
  }


  public boolean handleEvent(Event event, RobotStateManager rs) {
    return false;
  }
}
