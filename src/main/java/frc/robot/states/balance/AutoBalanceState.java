package frc.robot.states.balance;

import frc.robot.Constants;
import frc.robot.Event;
import frc.robot.PID;
import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.sensors.IMU;

public class AutoBalanceState implements State {
  private String name, parent;

  private DriveSubsystem drive;

  private PID pitchPID;
  private IMU imu;
  private State enteredFromState;
  
  public AutoBalanceState(String name, String parent){
    this.name = name;
    this.parent = parent;
    imu = IMU.getInstance();
    drive = DriveSubsystem.getInstance();
    
    pitchPID = new PID(imu::getPitch, 0.007, 0.0, 0.0, 0.0);
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

    //pitchPID.setTolerance(Constants.PLATFORM_BALANCE_PITCH_THRESHOLD_DEG);

    pitchPID.setSetpoint(0.0);
    
    pitchPID.startThread();


 }

  @Override
  public void Leave() {
    pitchPID.stopThread();
    drive.arcadeDrive(0.0, 0.0);
    System.out.println("left " + name);
  }

  @Override
  public void Periodic(RobotStateManager rs) {
    //TODO: fix me
    double output = pitchPID.getOutput();
    if((int)(System.currentTimeMillis() / 100) % 10 == 0)
      System.out.format("Angle: %.3f | Output: %.3f\n", pitchPID.getSensorInput(), output);

    drive.arcadeDrive(output, 0.0);
    
  }


  public boolean handleEvent(Event event, RobotStateManager rs) {
    return false;
  }
}
