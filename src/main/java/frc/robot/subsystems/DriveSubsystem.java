package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class DriveSubsystem extends SubsystemBase {
  private static DriveSubsystem instance;

  private CANSparkMax leftMotor1, leftMotor2, rightMotor1, rightMotor2;
  public Encoder leftEncoder, rightEncoder;
  private MotorControllerGroup leftMotors, rightMotors;
  private Solenoid leftSolenoid, rightSolenoid;
  private PIDController highVelocityController, lowVelocityController;
  private final double driveWidth;

  public void initializeShuffleboardWidgets() {
    ShuffleboardLayout dashboardLayout = Shuffleboard.getTab(Constants.MAIN_SHUFFLEBOARD_TAB)
      .getLayout("Drive Train", BuiltInLayouts.kList)
      .withSize(4, 4);

    dashboardLayout.addDouble("Left Drive Encoder Velocity", leftEncoder::getRate)
      .withWidget(BuiltInWidgets.kGraph);
    dashboardLayout.addBoolean("Left Drive Shift State", () -> leftSolenoid.get());

    dashboardLayout.addDouble("Right Drive Encoder Velocity", rightEncoder::getRate)
      .withWidget(BuiltInWidgets.kGraph);
    dashboardLayout.addBoolean("Right Drive Shift State", () -> rightSolenoid.get());
  }

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
    leftEncoder.setDistancePerPulse(0.0002337788);
    rightEncoder.setDistancePerPulse(0.0002337788);
    leftEncoder.setReverseDirection(false);
    rightEncoder.setReverseDirection(true);

    leftMotors = new MotorControllerGroup(leftMotor1, leftMotor2);
    rightMotors = new MotorControllerGroup(rightMotor1, rightMotor2);
    
    leftSolenoid = new Solenoid(PneumaticsModuleType.REVPH, Constants.LEFT_DRIVE_SOLENOID);
    rightSolenoid = new Solenoid(PneumaticsModuleType.REVPH, Constants.RIGHT_DRIVE_SOLENOID);

    initializeShuffleboardWidgets();

    //TODO:tune me
    highVelocityController = new PIDController(.25,0,0);
    lowVelocityController = new PIDController(.05,0,0);

    driveWidth = 0.5461;
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
    double leftPower, rightPower;

    velocity *= -1;
    angularVelocity *= -1;

    double diffV = (driveWidth * Math.PI)*(1/(2*Math.PI))*angularVelocity;
    //TODO:Fix angular velocity
    
    
    System.out.println(leftSolenoid.get());

    
    if(getLeftShiftState()){
      leftPower = highVelocityController.calculate(leftEncoder.getRate(), velocity - diffV);
      rightPower = highVelocityController.calculate(rightEncoder.getRate(), velocity + diffV);
    }else{
      leftPower = lowVelocityController.calculate(leftEncoder.getRate(), velocity - diffV);
      rightPower = lowVelocityController.calculate(rightEncoder.getRate(), velocity + diffV);
    }

    tankDrive(leftPower,rightPower);
  }

  // Low gear *should* be false, and high gear *should* be true
  public void shift(boolean state) {
    leftSolenoid.set(state);
    rightSolenoid.set(state);
  }
  
  public boolean getLeftShiftState() {
    return leftSolenoid.get();
  }

  public boolean getRightShiftState() {
    return rightSolenoid.get();
  }

  public static DriveSubsystem getInstance() {
    if (instance == null) {
      instance = new DriveSubsystem();
    }

    return instance;
  }
}
