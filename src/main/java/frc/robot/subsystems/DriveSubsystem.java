package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class DriveSubsystem extends SubsystemBase {
  private static DriveSubsystem instance;

  private CANSparkMax leftMotor1, leftMotor2, rightMotor1, rightMotor2;
  private DifferentialDrive tankDrive;
  private Solenoid transmissionSolenoid;

  public DriveSubsystem() {
    leftMotor1 = new CANSparkMax(Constants.LEFT_MOTOR_ID_1, MotorType.kBrushed);
    leftMotor2 = new CANSparkMax(Constants.LEFT_MOTOR_ID_2, MotorType.kBrushed);
    rightMotor1 = new CANSparkMax(Constants.RIGHT_MOTOR_ID_1, MotorType.kBrushed);
    rightMotor2 = new CANSparkMax(Constants.RIGHT_MOTOR_ID_1, MotorType.kBrushed);

    tankDrive = new DifferentialDrive(
      new MotorControllerGroup(leftMotor1, leftMotor2), 
      new MotorControllerGroup(rightMotor1, rightMotor2)
    );

    transmissionSolenoid = new Solenoid(PneumaticsModuleType.REVPH, Constants.SOLENOID_CHANNEL);
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
    tankDrive.arcadeDrive(xSpeed, zRotation, true);
  }

  public void tankDrive(double leftSpeed, double rightSpeed) {
    tankDrive.tankDrive(leftSpeed, rightSpeed);
  }

  public void setVoltage(double volts) {
    leftMotor1.setVoltage(volts);
    leftMotor2.setVoltage(volts);
    rightMotor1.setVoltage(volts);
    rightMotor2.setVoltage(volts);
  }

  public DriveEncoders getRelativeEncoders() {
    return new DriveEncoders(
      leftMotor1.getEncoder(), 
      leftMotor2.getEncoder(), 
      rightMotor1.getEncoder(), 
      rightMotor2.getEncoder()
    );
  }

  public void shift(TransmissionState state) {
    transmissionSolenoid.set(state == TransmissionState.HIGH_RATIO);
  }

  public static DriveSubsystem getInstance() {
    if (instance == null) {
      instance = new DriveSubsystem();
    }

    return instance;
  }
}
