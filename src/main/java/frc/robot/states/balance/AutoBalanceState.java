package frc.robot.states.balance;

import frc.robot.Constants;
import frc.robot.statemachine.Event;
import frc.robot.statemachine.State;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.TurretvatorSubsystem;
import frc.robot.subsystems.TurretvatorSubsystem.ElevatorStage;
import frc.robot.sensors.IMU;
import frc.robot.statemachine.RobotStateManager;

public class AutoBalanceState implements State {
  private String name;
  private String parent;

  private DriveSubsystem driveSubsystem;
  private TurretvatorSubsystem tvSubsystem;

  private IMU imu;
  private State enteredFromState;
  
  public AutoBalanceState(String name, String parent){
    this.name = name;
    this.parent = parent;
    imu = IMU.getInstance();
    driveSubsystem = DriveSubsystem.getInstance();
    tvSubsystem = TurretvatorSubsystem.getInstance();
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
  public void enter() {}

  @Override
  public void enter(State enteredFrom) {
    System.out.println("entered" + name);
    tvSubsystem.elevatorSet(ElevatorStage.DOWN);

    driveSubsystem.shift(false);
 }

  @Override
  public void leave() {
    driveSubsystem.arcadeDrive(0.0, 0.0);
    System.out.println("left " + name);
  }

  @Override
  public void periodic(RobotStateManager rs) {
    double output = 0.0;
    if(imu.getPitch() > Constants.PLATFORM_BALANCE_PITCH_THRESHOLD_DEG && imu.getPitch() > 0){
      output =  -.06;
    } else if(imu.getPitch() < Constants.PLATFORM_BALANCE_PITCH_THRESHOLD_DEG && imu.getPitch() < 0) {
      output = .06;
    }

    if((int)(System.currentTimeMillis() / 100) % 10 == 0){
      System.out.format("Angle: %.3f | Output: %.3f%n", imu.getPitch(), output);
    }
    
    driveSubsystem.arcadeDrive(output, 0.0);
  }


  @Override
  public boolean handleEvent(Event event, RobotStateManager rs) {
    return false;
  }
}
