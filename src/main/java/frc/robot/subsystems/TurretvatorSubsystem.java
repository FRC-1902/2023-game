// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.HashMap;
import java.util.Map;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.PID;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.IntegerEntry;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.util.datalog.IntegerLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import frc.robot.Constants;

public class TurretvatorSubsystem{
  private static TurretvatorSubsystem instance;

  private static final double THROUGHBORE_CPR = 1;
  private static final int TURRET_MAX_ANGLE = 120;
  private static final double ELEVATOR_MAX_ROTATIONS = 4.5;

  private double desiredElevatorDistance = 0;
  private double elevatorEncoderOffset = 0;
  private static final double CONSTANT_ELEVATOR_POWER = 0.08;

  private long turretRampTime;

  private GenericEntry turretPWidget, turretIWidget, turretDWidget, turretFWidget, elevatorPWidget, elevatorIWidget, elevatorDWidget, elevatorFWidget;
  private CANSparkMax elevatorLeft, elevatorRight, turretMotor;
  private MotorControllerGroup elevatorMotors;
  private DutyCycleEncoder elevatorLeftEncoder, elevatorRightEncoder;
  private DutyCycleEncoder turretEncoder;
  private PID elevatorPID, turretPID;
  private Solenoid gripperSolenoidA, gripperSolenoidB;

  private double lastElevatorEncoderValue;
  private double lastTurretEncoderValue;

  private int elevatorWatchdogHits = 0;

  private boolean isTurretWatchdogEnabled = false;
  private boolean isElevatorWatchdogEnabled = false;

  private boolean initialPeriodic = true;
  private boolean isPIDEnabled = false;

  // 5 Second timeout
  // private long watchdogTimeout = 5000000;
  // private long watchdogActivationTime;

  public static enum ElevatorStage{
    HIGH, MIDDLE, LOAD, DOWN;
  }

  private Map<Enum<ElevatorStage>, Double> elevatorMap;

  public void initializeShuffleBoardWidgets() {
    ShuffleboardLayout turretLayout = Shuffleboard.getTab(Constants.PID_SHUFFLEBOARD_TAB)
      .getLayout("Turret PID", BuiltInLayouts.kList)
      .withSize(2, 3);
    
    turretPWidget = turretLayout
      .add("Turret PID - Proportional", 0.9)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    turretIWidget = turretLayout
      .add("Turret PID - Integral", 0)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    turretDWidget = turretLayout
      .add("Turret PID - Derivative", 0)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    turretFWidget = turretLayout
      .add("Turret PID - FeedForward", 0)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();

    ShuffleboardLayout elevatorLayout = Shuffleboard.getTab(Constants.PID_SHUFFLEBOARD_TAB)
      .getLayout("Elevator PID", BuiltInLayouts.kList)
      .withSize(2, 3);

    elevatorPWidget = elevatorLayout
      .add("Elevator PID - Proportional", 0.18)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    elevatorIWidget = elevatorLayout
      .add("Elevator PID - Integral", 0)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    elevatorDWidget = elevatorLayout
      .add("Elevator PID - Derivative", 0.04)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    elevatorFWidget = elevatorLayout
      .add("Elevator PID - FeedForward", 0)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
  }

  public TurretvatorSubsystem() {
    elevatorLeft = new CANSparkMax(Constants.LEFT_ELEVATOR_ID, MotorType.kBrushless);
    elevatorRight = new CANSparkMax(Constants.RIGHT_ELEVATOR_ID, MotorType.kBrushless);
    elevatorLeft.setInverted(false);
    elevatorRight.setInverted(true);
    elevatorLeft.setIdleMode(IdleMode.kBrake);
    elevatorRight.setIdleMode(IdleMode.kBrake);

    elevatorLeft.burnFlash();
    elevatorRight.burnFlash();
    
    elevatorMotors = new MotorControllerGroup(elevatorLeft, elevatorRight);
    elevatorLeftEncoder = new DutyCycleEncoder(Constants.LEFT_ELEVATOR_ENCODER);
    elevatorRightEncoder = new DutyCycleEncoder(Constants.RIGHT_ELEVATOR_ENCODER);
    
    elevatorPID = new PID(() -> elevatorLeftEncoder.get() - elevatorEncoderOffset, 0.18, 0.0, 0.04, 0.0, "elevatorPID");
    elevatorPID.setTolerance(0.05);

    elevatorMap = new HashMap<>();

    elevatorMap.put(ElevatorStage.HIGH, 4.418);
    elevatorMap.put(ElevatorStage.MIDDLE, 2.550);
    elevatorMap.put(ElevatorStage.LOAD, 2.8);
    elevatorMap.put(ElevatorStage.DOWN, 0.0);

    //turret initialization
    turretMotor = new CANSparkMax(Constants.TURRET_ID, MotorType.kBrushless);
    turretMotor.setInverted(true);
    turretEncoder = new DutyCycleEncoder(Constants.TURRET_ENCODER);
    
    turretPID = new PID(() -> ((turretEncoder.getAbsolutePosition() - Constants.TURRET_OFFSET + 1) % 1), 9, 0.0, 0.0, 0.0, "turretPID");
 
    turretPID.enableContinuousInput(0, THROUGHBORE_CPR);
    turretPID.setTolerance(0.001); //0.36 of a degree

    //gripper initialization
    gripperSolenoidA = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.GRIPPER_SOLENOID_A);
    gripperSolenoidB = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.GRIPPER_SOLENOID_B);
    

    // initializeShuffleBoardWidgets();

    lastElevatorEncoderValue = elevatorLeftEncoder.get();
    //I think that it takes a short while to initialize the sensor properly
    //Sets when periodic is called on first run
    lastTurretEncoderValue = 0;
  }

  /**
   * Sets the gripper's solenoid to opened/closed
   * @param isClosed gripper closed/opened state
   */
  public void setGripper(boolean isClosed){
    DataLogManager.log("Setting gripper to: " + isClosed);

    gripperSolenoidA.set(isClosed);
    gripperSolenoidB.set(!isClosed);
  }

  /**
   * Starts to center the turret.
   */
  public void centerTurret(){
    turretPID.setSetpoint(0);
  }

  /**
   * @return whether or not the turret is centered
   */
  public boolean isCentered() {
    return turretPID.atSetpoint() && turretPID.getSetpoint() == 0;
  }

  /**
   * Sets turret to specified degree
   * turns with PID
   * @param degrees degree set (+/- max turret angle)
   */
  public void setTurret(double degrees){
    if(Math.abs(degrees) > TURRET_MAX_ANGLE){
      DataLogManager.log("Degree put into TurretvatorSubsystem.setTurret too large!");
      return;
    }

    turretPID.setSetpoint(degrees * THROUGHBORE_CPR / 360);
  }
  
  /**
   * Makes the elevator move and maintain a certain distance away from an imaginary 
   * line normal to the line extending from the front of the robot to the back of it.
   * @param stage ElevatorStage - preset distances to go to
   */
  public void elevatorSet(ElevatorStage stage){
    setElevator(elevatorMap.get(stage));
  }

  /**
   * Makes the elevator move and maintain a certain distance away from an imaginary 
   * line normal to the line extending from the front of the robot to the back of it.
   * @param distance The distance to maintain the elevator from the imaginary line.
   */
  public void setElevator(double distance) {
    desiredElevatorDistance = distance;
  }

  public void addElevator(double distance) {
    desiredElevatorDistance += distance;
  }
  /**
   * @return if elevator has reached its setpoint
   */
  public boolean isExtended(){
    return elevatorPID.atSetpoint();
  }

  public void enablePID(boolean isEnabled){
    if(isEnabled){
      elevatorPID.startThread();
      turretPID.startThread();
    }else{
      elevatorPID.stopThread();
      turretPID.stopThread();
    }
  }

  private void elevatorPeriodic() {
    double elevatorPower;
    // Calculates how much the motors should rotate in order to maintain a constant distance
    // double desiredElevatorRotations = 
    //   desiredElevatorDistance / (Math.cos(turretPID.getSetpoint() * throughboreCPR * Math.PI * 2) *
    //   Math.cos(Math.toRadians(Constants.ELEVATOR_PITCH_DEG)) *
    //   Constants.ELEVATOR_CM_PER_ROTATION);
    double desiredElevatorRotations = desiredElevatorDistance; 

    if (initialPeriodic)
      elevatorEncoderOffset = elevatorLeftEncoder.get();

    if (desiredElevatorRotations > ELEVATOR_MAX_ROTATIONS)
      DataLogManager.log("Elevator is extending to extreme!");
    if (desiredElevatorRotations < 0)
      DataLogManager.log("Elevator shouldn't try to be negative!");

    desiredElevatorRotations = Math.max(Math.min(desiredElevatorRotations, ELEVATOR_MAX_ROTATIONS), 0.0);

    // elevatorPID.setP(elevatorPWidget.getDouble(.18));
    // elevatorPID.setI(elevatorIWidget.getDouble(0));
    // elevatorPID.setD(elevatorDWidget.getDouble(0.04));
    elevatorPID.setSetpoint(desiredElevatorRotations);
    //clamp elevator power to max
    elevatorPower = Math.max(
      Math.min(
        elevatorPID.getOutput(),
        Constants.MAX_ELEVATOR_MOTOR_POWER
      ),
      -1 * Constants.MAX_ELEVATOR_MOTOR_POWER
    );
    
    //max down power set to -0.1
    elevatorPower = elevatorPower < -0.1 ? -0.1 : elevatorPower;
    
    if(desiredElevatorDistance == 0.0 && elevatorPID.getSensorInput() < 0.15){
      elevatorMotors.set(0); //makes brake mode go into effect when down v/s maintain power
    }else{
      elevatorMotors.set(elevatorPower + CONSTANT_ELEVATOR_POWER);
    }
  }

  private void turretPeriodic() {
    // System.out.format("Turret Position: %.3f, PID: %.3f%n", turretEncoder.getAbsolutePosition(), turretPID.getSensorInput());

    double turretPow;
    long curTime = System.currentTimeMillis();

    // turretPID.setP(turretPWidget.getDouble(0.9) * 10);
    // turretPID.setI(turretIWidget.getDouble(0) * 10);
    // turretPID.setD(turretDWidget.getDouble(0) * 10);
 
    //XXX: add wraparound protection, just setting to -120 and 120 for wraparound protecting atm
    
    turretPow = turretPID.getOutput();
    
    //ramp soak for smooth startup
    if (turretPID.atSetpoint())
      turretRampTime = curTime + 2000;
    
    if (turretRampTime - curTime >= 0 && !turretPID.atSetpoint())
      turretPow *= 1.0/((double)(turretRampTime - curTime) / 2000.0 + 1.0);
    
    turretMotor.set(turretPow);
  }

  /**
   * Interlock watchdog on the elevator
   * @return boolean corresponding on whether or not the elevator should be locked
   */
  private boolean elevatorWatchdog(){
    

    if(isElevatorWatchdogEnabled){return true;}

    if (Math.abs(lastElevatorEncoderValue - elevatorLeftEncoder.get()) < 0.005 && Math.abs(elevatorMotors.get()) > 0.4){
      elevatorWatchdogHits++;
      DataLogManager.log("Watchdog hit counter: " + elevatorWatchdogHits);
    }else{
      elevatorWatchdogHits = 0;
    }
    //detects negative encoder or too many kill switch hits (~200 ms worth of hits)
    if (elevatorWatchdogHits >= 10 || elevatorLeftEncoder.get() < -1.0) {
      DataLogManager.log("==== ELEVATOR KILL SWITCH WATCHDOG ENGAGED ====");
      // watchdogActivationTime = RobotController.getFPGATime();
      isElevatorWatchdogEnabled = true;
    }

    return isElevatorWatchdogEnabled;
  }

  /**
   * Interlock watchdog on the turret
   * @return boolean corresponding on whether or not the turret should be locked
   */
  private boolean turretWatchdog(){
    lastTurretEncoderValue = turretPID.getSensorInput();

    if(isTurretWatchdogEnabled){return true;}

    //Detects wrap around to not catch that
    if (Math.abs(lastTurretEncoderValue - turretPID.getSensorInput()) > 0.35 && Math.abs(lastTurretEncoderValue - turretPID.getSensorInput()) < 0.65) {
      DataLogManager.log("==== TURRET KILL SWITCH WATCHDOG ENGAGED ====");
      // watchdogActivationTime = RobotController.getFPGATime();
      isTurretWatchdogEnabled = true;
    }
    return isTurretWatchdogEnabled;
  }

  public void resetWatchdogs(){
    if(isTurretWatchdogEnabled || isElevatorWatchdogEnabled){
      DataLogManager.log("==== ALL KILL SWITCH WATCHDOGS RELEASED ====");
      isTurretWatchdogEnabled = false;
      isElevatorWatchdogEnabled = false;

      initialPeriodic = true;
    }
  }

  // Called from Robot
  public void periodic() {
    if(!isPIDEnabled){
      enablePID(true);
    }

    // System.out.format("E Sens: %.3f | E Set: %.3f | T Sens: %.3f | T Set %.3f%n", elevatorPID.getSensorInput(), elevatorPID.getSetpoint(), turretPID.getSensorInput(), turretPID.getSetpoint());
    if (elevatorWatchdog()) {
      elevatorMotors.set(0);
    }
    else {
      elevatorPeriodic();
    }

    if (turretWatchdog()) {
      turretMotor.set(0);
    }
    else {
      turretPeriodic();
    }

    initialPeriodic = false;
    lastElevatorEncoderValue = elevatorLeftEncoder.get();

    //Disable watchdogs after timeout
    // if (RobotController.getFPGATime() - watchdogActivationTime > watchdogTimeout && (isTurretWatchdogEnabled || isElevatorWatchdogEnabled)) {
    //   DataLogManager.log("==== ALL KILL SWITCH WATCHDOGS RELEASED ====");
    //   isTurretWatchdogEnabled = false;
    //   isElevatorWatchdogEnabled = false;

    //   initialPeriodic = true;
    // }

  }

  public static TurretvatorSubsystem getInstance() {
    if (instance == null) {
      instance = new TurretvatorSubsystem();
    }
    return instance;
  }
}
