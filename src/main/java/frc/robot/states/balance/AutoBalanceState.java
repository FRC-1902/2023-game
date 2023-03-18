package frc.robot.states.balance;

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
import frc.robot.PID;
import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.HeaderWrapper;
import frc.robot.subsystems.IMUSubsystem;

public class AutoBalanceState implements State {
  private String name, parent;

  private DriveSubsystem drive;

  private PID pitchPID;
  private IMUSubsystem imu;
  private State enteredFromState;

  private double desiredPitch;
  
  public AutoBalanceState(String name, String parent){
    this.name = name;
    this.parent = parent;
    imu = IMUSubsystem.getInstance();
    drive = DriveSubsystem.getInstance();
    
    pitchPID = new PID(imu::getPitch, 0.1, 0.0, 0.0, 0.0);
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


    drive.shift(false);;

    pitchPID.startThread();

    pitchPID.setSetpoint(0.0);

    pitchPID.setTolerance(Constants.PLATFORM_BALANCE_PITCH_THRESHOLD_DEG);
  }

  @Override
  public void Leave() {
    pitchPID.stopThread();
    drive.arcadeDrive(0, 0);
    System.out.println("left " + name);
  }

  @Override
  public void Periodic(RobotStateManager rs) {

    System.out.format("Setpoint: %.3f\n", pitchPID.getSetpoint());

    drive.arcadeDrive(pitchPID.getOutput(), 0.0);
    
  }

  public boolean handleEvent(Event event, RobotStateManager rs) {
    return false;
  }
}
