package frc.robot.subsystems;


import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import frc.robot.Constants;
import frc.robot.PID;

public class DriveSubsystem {
  private static DriveSubsystem instance;

  private CANSparkMax leftMotor1, leftMotor2, rightMotor1, rightMotor2;
  private Encoder leftEncoder, rightEncoder;
  private MotorControllerGroup leftMotors, rightMotors;
  private Solenoid leftSolenoid, rightSolenoid;
  private PID lowLeftVelocityController, lowRightVelocityController;
  private final double driveWidth;
  private double currentLeftCommand;

  private DoubleLogEntry leftEncoderLogger, rightEncoderLogger;

  // private GenericEntry pidPWidget, pidIWidget, pidDWidget, pidFWidget;

  private void initializeShuffleboardWidgets() {
    ShuffleboardLayout dashboardLayout = Shuffleboard.getTab(Constants.MAIN_SHUFFLEBOARD_TAB)
        .getLayout("Drive Train", BuiltInLayouts.kList)
        .withSize(4, 4);

    dashboardLayout.addDoubleArray("Left Drive Command", this::currentCommand).withWidget(BuiltInWidgets.kGraph);

    dashboardLayout.addBoolean("Left Drive Shift State", () -> leftSolenoid.get());
    dashboardLayout.addBoolean("Right Drive Shift State", () -> rightSolenoid.get());

    // ShuffleboardLayout pidTuningTab = Shuffleboard.getTab(Constants.PID_SHUFFLEBOARD_TAB)
    //   .getLayout("Low Gear Auto PID", BuiltInLayouts.kList)
    //   .withSize(2, 3);

    // pidPWidget = pidTuningTab
    //   .add("Auto Drive PID - Proportional", 0.0)
    //   .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    // pidIWidget = pidTuningTab
    //   .add("Auto Drive PID - Integral", 0.0)
    //   .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    // pidDWidget = pidTuningTab
    //   .add("Auto Drive PID - Derivative", 0.0)
    //   .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    // pidFWidget = pidTuningTab
    //   .add("Auto Drive PID - FeedForward", 0.0)
    //   .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
  }

  private void initializeLogger() {
    leftEncoderLogger = new DoubleLogEntry(DataLogManager.getLog(), "/DriveSubsystem/leftEncoder");
    rightEncoderLogger = new DoubleLogEntry(DataLogManager.getLog(), "/DriveSubsystem/rightEncoder");
  }

  public void logPeriodic() {
    leftEncoderLogger.append(leftEncoder.getRate());
    rightEncoderLogger.append(rightEncoder.getRate());
  }

  private double[] currentCommand() {
    return new double[] { currentLeftCommand, leftEncoder.getRate() };
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

    setBrake(true);

    leftEncoder = new Encoder(Constants.LEFT_DRIVE_ENCODER_1, Constants.LEFT_DRIVE_ENCODER_2);
    rightEncoder = new Encoder(Constants.RIGHT_DRIVE_ENCODER_1, Constants.RIGHT_DRIVE_ENCODER_2);

    leftEncoder.setDistancePerPulse(0.0002337788);
    rightEncoder.setDistancePerPulse(0.0002337788);
    leftEncoder.setReverseDirection(true);
    rightEncoder.setReverseDirection(false);

    leftMotors = new MotorControllerGroup(leftMotor1, leftMotor2);
    rightMotors = new MotorControllerGroup(rightMotor1, rightMotor2);

    leftSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.LEFT_DRIVE_SOLENOID);
    rightSolenoid = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.RIGHT_DRIVE_SOLENOID);

    initializeShuffleboardWidgets();
    initializeLogger();

    lowLeftVelocityController = new PID(leftEncoder::getRate, 0.01, 0.005, 0.01, 0.5, "DriveSubsystem/lowLeftVelocityControllerPID");
    lowRightVelocityController = new PID(rightEncoder::getRate, 0.01, 0.005, 0.01, 0.5, "DriveSubsystem/lowRightVelocityControllerPID");

    driveWidth = 0.5461;

    currentLeftCommand = 0.0;
  }


  public void setPIDEnable(boolean isEnabled) {
    if (isEnabled) {
      lowLeftVelocityController.startThread();
      lowRightVelocityController.startThread();
    } else {
      lowLeftVelocityController.stopThread();
      lowRightVelocityController.stopThread();
    }
  }

  public void arcadeDrive(double xSpeed, double zRotation){
    double lPow = xSpeed - zRotation;
    double rPow = xSpeed + zRotation;

    // Desaturate wheel speeds for powers over 1
    double maxMagnitude = Math.max(Math.abs(lPow), Math.abs(rPow));
    if (maxMagnitude > 1.0) {
      lPow /= maxMagnitude;
      rPow /= maxMagnitude;
    }
      
    tankDrive(lPow, rPow);
  }

  /**
   * @param xSpeed
   * @param zRotation
   */
  public void curvedArcadeDrive(double xSpeed, double zRotation) {
    // Clamp input values to -1 to 1
    xSpeed = Math.max(-1.0, Math.min(1.0, xSpeed));
    zRotation = Math.max(-1.0, Math.min(1.0, zRotation));

    //Curve rotation to decrease effect
    //Desmos lied to me and said that this curve would work without the pow(abs) * sign
    zRotation = Math.signum(zRotation) * (Math.pow(Math.abs(zRotation), 1.8) * 0.5);

    arcadeDrive(xSpeed, zRotation);
  }

  /**
   * @param xSpeed
   * @param zRotation
   * @param scaleFactor slow mode button: 0.5 would cut down the curves by 1/2
   */
  public void curvedArcadeDrive(double xSpeed, double zRotation, double scaleFactor) {
    // Clamp input values to -1 to 1
    xSpeed = Math.max(-1.0, Math.min(1.0, xSpeed));
    zRotation = Math.max(-1.0, Math.min(1.0, zRotation));

    //Curve rotation to decrease effect
    //Desmos lied to me and said that this curve would work without the pow(abs) * sign
    zRotation = Math.signum(zRotation) * (Math.pow(Math.abs(zRotation), 1.8) * 0.5 * scaleFactor);
    xSpeed *= scaleFactor;

    arcadeDrive(xSpeed, zRotation);
  }
  

  /**
   * Bog standard tank drive
   * @param leftSpeed
   * @param rightSpeed
   */
  public void tankDrive(double leftSpeed, double rightSpeed) {
    leftMotors.set(leftSpeed);
    rightMotors.set(rightSpeed);
  }

  public void setBrake(boolean isBrake){
    DataLogManager.log("Drive Breaking: " + isBrake);
    if(isBrake){
      leftMotor1.setIdleMode(IdleMode.kBrake);
      leftMotor2.setIdleMode(IdleMode.kBrake);
      rightMotor1.setIdleMode(IdleMode.kBrake);
      rightMotor2.setIdleMode(IdleMode.kBrake);
    } else {
      leftMotor1.setIdleMode(IdleMode.kCoast);
      leftMotor2.setIdleMode(IdleMode.kCoast);
      rightMotor1.setIdleMode(IdleMode.kCoast);
      rightMotor2.setIdleMode(IdleMode.kCoast);
    }
  }

  /**
   * PID to hit a specific velocity for your drivetrain
   * <p>XXX: angular velocity is BROKEN, we never used it</p>
   * @param velocity        m/s that you want to hit
   * @param angularVelocity m/s of angular change
   */
  public void velocityPID(double velocity, double angularVelocity) {
    assert !getLeftShiftState();

    currentLeftCommand = velocity;
    double leftPower;
    double rightPower;

    velocity *= -1;
    angularVelocity *= -1;
    
    double diffV = (driveWidth * Math.PI) * (1 / (2 * Math.PI)) * angularVelocity;

    lowLeftVelocityController.setSetpoint(velocity - diffV);
    lowRightVelocityController.setSetpoint(velocity + diffV);
    leftPower = lowLeftVelocityController.getOutput();
    rightPower = lowRightVelocityController.getOutput();

    tankDrive(leftPower, rightPower);
  }

  // Low gear is false, and high gear is true
  public void shift(boolean isHigh) {
    if(leftSolenoid.get() != isHigh || leftSolenoid != rightSolenoid){
      DataLogManager.log("Shifted: " + isHigh);
      leftSolenoid.set(isHigh);
      rightSolenoid.set(isHigh);
      LEDSubsystem.getInstance().setTemporaryRGB(200, 0, 64, 255);
    }
  }

  public boolean getLeftShiftState() {
    return leftSolenoid.get();
  }

  public boolean getRightShiftState() {
    return rightSolenoid.get();
  }

  public double getLeftEncoderDistance(){
    return leftEncoder.getDistance();
  }

  public double getRightEncoderDistance(){
    return rightEncoder.getDistance();
  }

  public static DriveSubsystem getInstance() {
    if (instance == null) {
      instance = new DriveSubsystem();
    }

    return instance;
  }
}
