package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class DriveSubsystem extends SubsystemBase {
  private static DriveSubsystem instance;

  private CANSparkMax leftMotor1, leftMotor2, rightMotor1, rightMotor2;
  public final RelativeEncoder leftEncoder, rightEncoder;

  private MotorControllerGroup leftMotors, rightMotors;
  private Solenoid leftShifterSolenoid, rightShifterSolenoid;

  private double TICKS_PER_METERS = 10_000.0; //TODO: find this number
  private double DISTANCE_FROM_WHEEL_TO_CENTER = 0.1; //TODO: find this number


  public DriveSubsystem() {
    leftMotor1 = new CANSparkMax(Constants.LEFT_DRIVE_ID_1, MotorType.kBrushless);
    leftMotor2 = new CANSparkMax(Constants.LEFT_DRIVE_ID_2, MotorType.kBrushless);
    rightMotor1 = new CANSparkMax(Constants.RIGHT_DRIVE_ID_1, MotorType.kBrushless);
    rightMotor2 = new CANSparkMax(Constants.RIGHT_DRIVE_ID_2, MotorType.kBrushless);

    leftEncoder = leftMotor1.getAlternateEncoder(8192);
    rightEncoder = rightMotor1.getAlternateEncoder(8192);
    
    leftMotors = new MotorControllerGroup(leftMotor1, leftMotor2);
    rightMotors = new MotorControllerGroup(rightMotor1, rightMotor2);

    leftShifterSolenoid = new Solenoid(PneumaticsModuleType.REVPH, Constants.LEFT_DRIVE_SOLENOID);
    rightShifterSolenoid = new Solenoid(PneumaticsModuleType.REVPH, Constants.RIGHT_DRIVE_SOLENOID);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }

  public void arcadeDrive(double xSpeed, double zRotation) {
    tankDrive(xSpeed - zRotation, xSpeed + zRotation);
  }

  public void tankDrive(double leftSpeed, double rightSpeed) {
    leftMotors.set(leftSpeed);
    rightMotors.set(rightSpeed);
  }

  /**
   * 
   * @param velocity x = 0, y = forward velocity (meters per second), rotation = angular velocity (radians per second)
   */
  public void driveByVelocities(Transform2d velocity){
    double rightDriveVelocity = velocity.getY() * TICKS_PER_METERS, leftDriveVelocity = velocity.getY() * TICKS_PER_METERS;
    rightDriveVelocity += velocity.getRotation().getRadians()/DISTANCE_FROM_WHEEL_TO_CENTER * TICKS_PER_METERS; 
    leftDriveVelocity -= velocity.getRotation().getRadians()/DISTANCE_FROM_WHEEL_TO_CENTER * TICKS_PER_METERS;
    //TODO: apply velocities
  }

  /** shifts drive subystem gearbox
   * @param state boolean, true for high and false for low
   */
  public void shift(boolean state) {
    leftShifterSolenoid.set(state);
    rightShifterSolenoid.set(state);
  }

  public static DriveSubsystem getInstance() {
    if (instance == null) {
      instance = new DriveSubsystem();
    }

    return instance;
  }
}
