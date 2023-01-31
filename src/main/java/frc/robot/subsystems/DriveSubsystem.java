package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class DriveSubsystem extends SubsystemBase {
  private static DriveSubsystem instance;

  private CANSparkMax leftMotor1, leftMotor2, rightMotor1, rightMotor2;
  public Encoder leftEncoder, rightEncoder;
  private MotorControllerGroup leftMotors, rightMotors;
  private DoubleSolenoid leftSolenoid, rightSolenoid;
  private PIDController velocityController;

  public DriveSubsystem() {
    leftMotor1 = new CANSparkMax(Constants.LEFT_DRIVE_ID_1, MotorType.kBrushless);
    leftMotor2 = new CANSparkMax(Constants.LEFT_DRIVE_ID_2, MotorType.kBrushless);
    rightMotor1 = new CANSparkMax(Constants.RIGHT_DRIVE_ID_1, MotorType.kBrushless);
    rightMotor2 = new CANSparkMax(Constants.RIGHT_DRIVE_ID_2, MotorType.kBrushless);

    leftMotor1.setInverted(false);
    leftMotor2.setInverted(false);
    rightMotor1.setInverted(true);
    rightMotor2.setInverted(true);

    leftEncoder = new Encoder(Constants.LEFT_DRIVE_ENCODER_1, Constants.LEFT_DRIVE_ENCODER_2);
    rightEncoder = new Encoder(Constants.RIGHT_DRIVE_ENCODER_1, Constants.RIGHT_DRIVE_ENCODER_2);
    //TODO: check me, meters per count
    leftEncoder.setDistancePerPulse(0.0000584447);
    rightEncoder.setDistancePerPulse(0.0000584447);
    leftEncoder.setReverseDirection(true);
    rightEncoder.setReverseDirection(false);

    leftMotors = new MotorControllerGroup(leftMotor1, leftMotor2);
    rightMotors = new MotorControllerGroup(rightMotor1, rightMotor2);
    
    leftSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, Constants.LEFT_LOW_DRIVE_SOLENOID, Constants.LEFT_HIGH_DRIVE_SOLENOID);
    rightSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, Constants.RIGHT_LOW_DRIVE_SOLENOID, Constants.RIGHT_HIGH_DRIVE_SOLENOID);

    //TODO:tune me
    velocityController = new PIDController(0.2,0,0);
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
   * PID to hit a specific velocity for your drivetrain
   * @param velocity m/s that you want to hit
   * @param angularVelocity m/s of angular change
   */
  public void velocityPID(double velocity, double angularVelocity){
    System.out.println(leftEncoder.getRate() + " " + rightEncoder.getRate());
    //TODO:test me
    double leftPower = velocityController.calculate(leftEncoder.getRate(), velocity - angularVelocity);
    double rightPower = velocityController.calculate(rightEncoder.getRate(), velocity + angularVelocity);
    tankDrive(leftPower,rightPower);
  }

  public static enum ShiftState{
    HIGH,LOW,DEPRESSURIZED
  }

  /** shifts drive subystem gearbox
   * @param state DoubleSolenoid.Value, kForward or kReverse
   */
  public void shift(ShiftState state) {
    switch(state){
    case HIGH:
      leftSolenoid.set(DoubleSolenoid.Value.kForward);
      rightSolenoid.set(DoubleSolenoid.Value.kForward);
      break;
    case LOW:
      leftSolenoid.set(DoubleSolenoid.Value.kReverse);
      rightSolenoid.set(DoubleSolenoid.Value.kReverse);
      break;
    case DEPRESSURIZED:
      leftSolenoid.set(DoubleSolenoid.Value.kOff);
      rightSolenoid.set(DoubleSolenoid.Value.kOff);
      break;
    }
  }

  public static DriveSubsystem getInstance() {
    if (instance == null) {
      instance = new DriveSubsystem();
    }

    return instance;
  }
}
