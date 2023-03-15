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
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class TurretvatorSubsystem extends SubsystemBase {
  private static TurretvatorSubsystem instance;

  public static final double throughboreCPR = 1;
  public static final int turretMaxAngle = 120;
  public static final double elevatorStop = 4.5;

  private double desiredElevatorDistance = 0;
  private double elevatorEncoderOffset = 0;
  private double constantElevatorPower = 0.08;

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

  private int elevatorKillSwitchHits = 0;

  private boolean turretKillSwitchInterlock = false;
  private boolean elevatorKillSwitchInterlock = false;

  private boolean initialPeriodic = true;
  private boolean isPIDEnabled = false;

  // 5 Second timeout
  private long killSwitchTimeout = 5000000;
  private long killSwitchActivationTime;

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
      .add("Elevator PID - Proportional", 0)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    elevatorIWidget = elevatorLayout
      .add("Elevator PID - Integral", 0)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    elevatorDWidget = elevatorLayout
      .add("Elevator PID - Derivative", 0)
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
    elevatorLeft.setIdleMode(IdleMode.kCoast);
    elevatorRight.setIdleMode(IdleMode.kCoast);
    
    elevatorMotors = new MotorControllerGroup(elevatorLeft, elevatorRight);
    elevatorLeftEncoder = new DutyCycleEncoder(Constants.LEFT_ELEVATOR_ENCODER);
    elevatorRightEncoder = new DutyCycleEncoder(Constants.RIGHT_ELEVATOR_ENCODER);
    
    elevatorPID = new PID(() -> {
      // System.out.format("Encoder %.03f | Offset %.03f \n", elevatorLeftEncoder.get(), elevatorEncoderOffset);
      return elevatorLeftEncoder.get() - elevatorEncoderOffset; 
    },0.22, 0.0, 0.0, 0.0);
    elevatorPID.setTolerance(0.05); //TODO: set me

    turretMotor = new CANSparkMax(Constants.TURRET_ID, MotorType.kBrushless);
    turretMotor.setInverted(true);
    turretEncoder = new DutyCycleEncoder(Constants.TURRET_ENCODER);
    
    //(turretEncoder.getAbsolutePosition() - 0.393 + 1) % 1
    turretPID = new PID(() -> ((turretEncoder.getAbsolutePosition() - 0.643 + 1) % 1), 9, 0.0, 0.0, 0.0);
 
    turretPID.enableContinuousInput(0, throughboreCPR);
    turretPID.setTolerance(0.001);

    gripperSolenoidA = new Solenoid(PneumaticsModuleType.REVPH, Constants.GRIPPER_SOLENOID_A);
    gripperSolenoidB = new Solenoid(PneumaticsModuleType.REVPH, Constants.GRIPPER_SOLENOID_B);
    

    initializeShuffleBoardWidgets();

    lastElevatorEncoderValue = elevatorLeftEncoder.get();
    lastTurretEncoderValue = 0; //XXX: isn't setting right from sensor on first loop, check me
    //I think that it takes a short while to initialize the sensor properly
  }

  /**
   * Sets the gripper's solenoid to opened/closed
   * @param isClosed gripper closed/opened state
   */
  public void setGripper(boolean isClosed){
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
    if(Math.abs(degrees) > turretMaxAngle){
      System.out.println("Degree put into TurretvatorSubsystem.turretSet too large!");
      return;
    }

    turretPID.setSetpoint(degrees * throughboreCPR / 360);
  }

  public static enum ElevatorStage{
    HIGH, MIDDLE, LOAD, DOWN;
  }

  public Map<Enum<ElevatorStage>, Double> elevatorMap = 
    new HashMap<Enum<ElevatorStage>, Double>() {{
      put(ElevatorStage.HIGH, 4.418);
      put(ElevatorStage.MIDDLE, 3.034);
      put(ElevatorStage.LOAD, 2.8);
      put(ElevatorStage.DOWN, 0.0);
    }};
  
  /**
   * Makes the elevator move and maintain a certain distance away from an imaginary 
   * line normal to the line extending from the front of the robot to the back of it.
   * @param stage ElevatorStage - preset distances to go to
   */
  public void elevatorSet(ElevatorStage stage){
    elevatorSet(elevatorMap.get(stage));
  }

  /**
   * Makes the elevator move and maintain a certain distance away from an imaginary 
   * line normal to the line extending from the front of the robot to the back of it.
   * @param distance The distance to maintain the elevator from the imaginary line.
   */
  public void elevatorSet(double distance) {
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

    if (desiredElevatorRotations > elevatorStop)
      System.out.println("Elevator is extending to extreme!");
    if (desiredElevatorRotations < 0)
      System.out.println("Elevator shouldn't try to be negative!");

    desiredElevatorRotations = Math.max(Math.min(desiredElevatorRotations, elevatorStop), 0.0);

    elevatorPID.setP(0.22);
    elevatorPID.setI(elevatorIWidget.getDouble(0));
    elevatorPID.setD(elevatorDWidget.getDouble(0));
    elevatorPID.setSetpoint(desiredElevatorRotations);
    elevatorPower = Math.max(
      Math.min(
        elevatorPID.getOutput(),
        Constants.MAX_ELEVATOR_MOTOR_POWER
      ),
      -1 * Constants.MAX_ELEVATOR_MOTOR_POWER
    );

    elevatorPower = elevatorPower < -0.1 ? -0.1 : elevatorPower;
    
    elevatorMotors.set(elevatorPower + .08);
  }

  private void turretPeriodic() {
    double turretPow;

    turretPID.setP(turretPWidget.getDouble(0.9) * 10);
    turretPID.setI(turretIWidget.getDouble(0) * 10);
    turretPID.setD(turretDWidget.getDouble(0) * 10);
 
    //XXX: add wraparound protection, just setting to -45 and 45 for wraparound protecting atm
    
    turretPow = turretPID.getOutput();
    
    //ramp soak for smooth startup
    if (turretPID.atSetpoint())
      turretRampTime = System.currentTimeMillis() + 2000;
    
    if (turretRampTime - System.currentTimeMillis() >= 0 && !turretPID.atSetpoint())
      turretPow *= 1/((turretRampTime - System.currentTimeMillis()) / 2000 + 1);

    turretMotor.set(turretPow);
  }

  // Called from Robot
  @Override
  public void periodic() {
    if(!isPIDEnabled){
      enablePID(true);
    }

    System.out.println(elevatorPID.getSensorInput());
    if (elevatorKillSwitchInterlock) {
      elevatorMotors.set(0);
    }
    else {
      elevatorPeriodic();
      
      if (Math.abs(lastElevatorEncoderValue - elevatorLeftEncoder.get()) < 0.005 && Math.abs(elevatorMotors.get()) > 0.4)
        elevatorKillSwitchHits++;
      else
        elevatorKillSwitchHits = 0;
      
      if (elevatorKillSwitchHits >= 10) {
        System.out.println("==== ELEVATOR INTERLOCK ENGAGED ====");
        killSwitchActivationTime = RobotController.getFPGATime();
        elevatorKillSwitchInterlock = true;
      }

      if(elevatorLeftEncoder.get() < -1.0){
        elevatorKillSwitchInterlock = true;
      }
    }

    if (turretKillSwitchInterlock) {
      turretMotor.set(0);
    }
    else {
      turretPeriodic();

      // Detects wrap around
      if (Math.abs(lastTurretEncoderValue - turretPID.getSensorInput()) > 0.35 && Math.abs(lastTurretEncoderValue - turretPID.getSensorInput()) < 0.65) {
        System.out.println("==== TURRET INTERLOCK ENGAGED ====");
        System.out.format("Last: %.3f, Current: %.3f\n", lastTurretEncoderValue, turretPID.getSensorInput());
        killSwitchActivationTime = RobotController.getFPGATime();
        turretKillSwitchInterlock = true;
      }
    }

    initialPeriodic = false;

    // if (RobotController.getFPGATime() - killSwitchActivationTime > killSwitchTimeout && (turretKillSwitchInterlock || elevatorKillSwitchInterlock)) {
    //   System.out.println("==== ALL INTERLOCKS RELEASED ====");
    //   turretKillSwitchInterlock = false;
    //   elevatorKillSwitchInterlock = false;

    //   initialPeriodic = true;
    // }

    // //Medium l 3.302 r -2.610

    lastElevatorEncoderValue = elevatorLeftEncoder.get();
    lastTurretEncoderValue = turretPID.getSensorInput();
  }

  public static TurretvatorSubsystem getInstance() {
    if (instance == null) {
      instance = new TurretvatorSubsystem();
    }
    return instance;
  }
}
