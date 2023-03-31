package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

// import edu.wpi.first.networktables.GenericEntry;
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
  public Encoder leftEncoder, rightEncoder;
  private MotorControllerGroup leftMotors, rightMotors;
  private Solenoid leftSolenoid, rightSolenoid;
  private PID highLeftVelocityController, lowLeftVelocityController, highRightVelocityController,
      lowRightVelocityController;
  private final double driveWidth;
  private double currentLeftCommand;

  // private GenericEntry pidPWidget, pidIWidget, pidDWidget, pidFWidget;

  public void initializeShuffleboardWidgets() {
    ShuffleboardLayout dashboardLayout = Shuffleboard.getTab(Constants.MAIN_SHUFFLEBOARD_TAB)
        .getLayout("Drive Train", BuiltInLayouts.kList)
        .withSize(4, 4);

    dashboardLayout.addDoubleArray("Left Drive Command", this::currentCommand).withWidget(BuiltInWidgets.kGraph);

    // dashboardLayout.addDouble("Left Drive Encoder Velocity", leftEncoder::getRate)
    //     .withWidget(BuiltInWidgets.kGraph);
    dashboardLayout.addBoolean("Left Drive Shift State", () -> leftSolenoid.get());

    // dashboardLayout.addDouble("Right Drive Encoder Velocity", rightEncoder::getRate)
    //     .withWidget(BuiltInWidgets.kGraph);
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

    leftMotor1.setIdleMode(IdleMode.kBrake);
    leftMotor2.setIdleMode(IdleMode.kBrake);
    rightMotor1.setIdleMode(IdleMode.kBrake);
    rightMotor2.setIdleMode(IdleMode.kBrake);

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

    // TODO: tune high velocity controller to PID in high gear
    highLeftVelocityController = new PID(leftEncoder::getRate, 0.0, 0.0, 0.0, 0.1);
    lowLeftVelocityController = new PID(leftEncoder::getRate, 0.01, 0.005, 0.01, 0.5);
    highRightVelocityController = new PID(rightEncoder::getRate, 0.0, 0.0, 0.0, 0.1);
    lowRightVelocityController = new PID(rightEncoder::getRate, 0.01, 0.005, 0.01, 0.5);

    driveWidth = 0.5461;

    currentLeftCommand = 0.0;
  }

  public void setPIDEnable(boolean isEnabled) {
    if (isEnabled) {
      highLeftVelocityController.startThread();
      highRightVelocityController.startThread();
      lowLeftVelocityController.startThread();
      lowRightVelocityController.startThread();
    } else {
      highLeftVelocityController.stopThread();
      highRightVelocityController.stopThread();
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
    xSpeed = Math.max(-1, Math.min(1, xSpeed));
    zRotation = Math.max(-1, Math.min(1, zRotation));

    //Curve rotation to decrease effect
    zRotation = Math.pow(zRotation, 1.8) * 0.5;

    arcadeDrive(xSpeed, zRotation);
  }

  /**
   * @param xSpeed
   * @param zRotation
   * @param scaleFactor slow mode button: 0.5 would cut down the curves by 1/2
   */
  public void curvedArcadeDrive(double xSpeed, double zRotation, double scaleFactor) {
    // Clamp input values to -1 to 1
    xSpeed = Math.max(-1, Math.min(1, xSpeed));
    zRotation = Math.max(-1, Math.min(1, zRotation));

    //Curve rotation to decrease effect
    zRotation = Math.pow(zRotation, 1.8) * 0.5 * scaleFactor;
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
    System.out.format("Drive Breaking: %b%n", isBrake);
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
   * 
   * @param velocity        m/s that you want to hit
   * @param angularVelocity m/s of angular change
   */
  public void velocityPID(double velocity, double angularVelocity) {
    currentLeftCommand = velocity;
    double leftPower, rightPower;

    velocity *= -1;
    angularVelocity *= -1;

   
    //TODO:Fix angular velocity
    
    double diffV = (driveWidth * Math.PI) * (1 / (2 * Math.PI)) * angularVelocity;

    // lowLeftVelocityController.setP(pidPWidget.getDouble(0.01));
    // lowLeftVelocityController.setI(pidIWidget.getDouble(0.005));
    // lowLeftVelocityController.setD(pidDWidget.getDouble(0.01));    
    // lowLeftVelocityController.setF(pidFWidget.getDouble(0.5));    
    // lowRightVelocityController.setP(pidPWidget.getDouble(0.01));
    // lowRightVelocityController.setI(pidIWidget.getDouble(0.005));
    // lowRightVelocityController.setD(pidDWidget.getDouble(0.01));
    // lowRightVelocityController.setF(pidFWidget.getDouble(0.5));
    // highLeftVelocityController.setP(pidPWidget.getDouble(0));
    // highLeftVelocityController.setI(pidIWidget.getDouble(0));
    // highLeftVelocityController.setD(pidDWidget.getDouble(0));    
    // highLeftVelocityController.setF(pidFWidget.getDouble(0));    
    // highRightVelocityController.setP(pidPWidget.getDouble(0));
    // highRightVelocityController.setI(pidIWidget.getDouble(0));
    // highRightVelocityController.setD(pidDWidget.getDouble(0));
    // highRightVelocityController.setF(pidFWidget.getDouble(0));
    
    // TODO:Fix angular velocity

    // System.out.println(leftSolenoid.get());

    if (getLeftShiftState()) {
      highLeftVelocityController.setSetpoint(velocity - diffV);
      highRightVelocityController.setSetpoint(velocity + diffV);
      leftPower = highLeftVelocityController.getOutput();
      rightPower = highRightVelocityController.getOutput();
    } else {
      lowLeftVelocityController.setSetpoint(velocity - diffV);
      lowRightVelocityController.setSetpoint(velocity + diffV);
      leftPower = lowLeftVelocityController.getOutput();
      rightPower = lowRightVelocityController.getOutput();
    }

    tankDrive(leftPower, rightPower);
  }

  // Low gear is false, and high gear is true
  public void shift(boolean isHigh) {
    System.out.format("Shifted %b%n", isHigh);
    leftSolenoid.set(isHigh);
    rightSolenoid.set(isHigh);
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
