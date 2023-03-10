// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.HashMap;
import java.util.Map;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Controllers;

public class TurretvatorSubsystem extends SubsystemBase {
  private static TurretvatorSubsystem instance;

  public static final double throughboreCPR = 1; //TODO: check me
  public static final int turretMaxAngle = 90;
  public static final double elevatorStop = 4.5; //TODO: set me

  private double desiredElevatorDistance = 0;
  private double elevatorEncoderOffset = 0;
  private double constantElevatorPower = 0.08;
  private boolean initialPeriodic = true;

  private long turretRampTime;

  private GenericEntry turretPWidget, turretIWidget, turretDWidget, elevatorPWidget, elevatorIWidget, elevatorDWidget;
  private CANSparkMax elevatorLeft, elevatorRight, turretMotor;
  private MotorControllerGroup elevatorMotors;
  private DutyCycleEncoder elevatorLeftEncoder, elevatorRightEncoder;
  private DutyCycleEncoder turretEncoder;
  private PIDController elevatorPID, turretPID;
  private Solenoid gripperSolenoidA, gripperSolenoidB;

  public void initializeShuffleBoardWidgets() {
    ShuffleboardLayout dashboardLayout = Shuffleboard.getTab(Constants.PID_SHUFFLEBOARD_TAB)
      .getLayout("Turret PID", BuiltInLayouts.kList)
      .withSize(4, 4);

    turretPWidget = dashboardLayout
      .add("Turret PID - Proportional", 0.9)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    turretIWidget = dashboardLayout
      .add("Turret PID - Integral", 0)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    turretDWidget = dashboardLayout
      .add("Turret PID - Derivative", 0)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();

    elevatorPWidget = dashboardLayout
      .add("Elevator PID - Proportional", 0)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    elevatorIWidget = dashboardLayout
      .add("Elevator PID - Integral", 0)
      .withWidget(BuiltInWidgets.kNumberSlider).getEntry();
    elevatorDWidget = dashboardLayout
      .add("Elevator PID - Derivative", 0)
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
    //TODO: Tune me
    elevatorPID = new PIDController(0, 0, 0);
    elevatorPID.setTolerance(0.01); //TODO: set me

    turretMotor = new CANSparkMax(Constants.TURRET_ID, MotorType.kBrushless);
    turretMotor.setInverted(true);
    turretEncoder = new DutyCycleEncoder(Constants.TURRET_ENCODER);
    //TODO: Tune me
    turretPID = new PIDController(0, 0, 0);
 
    turretPID.enableContinuousInput(0, throughboreCPR);
    turretPID.setTolerance(0.001); //TODO: set me

    gripperSolenoidA = new Solenoid(PneumaticsModuleType.REVPH, Constants.GRIPPER_SOLENOID_A);
    gripperSolenoidB = new Solenoid(PneumaticsModuleType.REVPH, Constants.GRIPPER_SOLENOID_B);


    initializeShuffleBoardWidgets();
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
    //TODO: test me
    if(Math.abs(degrees) > turretMaxAngle){
      System.out.println("Degree put into TurretvatorSubsystem.turretSet too large!");
      return;
    }

    turretPID.setSetpoint(degrees * throughboreCPR / 360);
  }

  public static enum ElevatorStage{
    HIGH, MIDDLE, LOAD, DOWN;
  }

  //TODO: set me
  public Map<Enum<ElevatorStage>, Double> elevatorMap = 
    new HashMap<Enum<ElevatorStage>, Double>() {{
      put(ElevatorStage.HIGH, 4.418);
      put(ElevatorStage.MIDDLE, 3.034);
      put(ElevatorStage.LOAD, 0.0);
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

  /**
   * @return if elevator has reached its setpoint
   */
  public boolean isExtended(){
    return elevatorPID.atSetpoint();
  }

  private void elevatorPeriodic() {
    if (initialPeriodic)
      elevatorEncoderOffset = elevatorLeftEncoder.get();

    double elevatorPower;
    // Calculates how much the motors should rotate in order to maintain a constant distance
    // double desiredElevatorRotations = 
    //   desiredElevatorDistance / (Math.cos((turretEncoder.getAbsolutePosition() - 0.393 + 1)%1 * throughboreCPR * Math.PI * 2) *
    //   Math.cos(Math.toRadians(Constants.ELEVATOR_PITCH_DEG)) *
    //   Constants.ELEVATOR_CM_PER_ROTATION);
    double desiredElevatorRotations = desiredElevatorDistance; 

    if (desiredElevatorRotations > elevatorStop || desiredElevatorRotations < 0)
      System.out.println("Elevator is extended to extreme!");

    desiredElevatorRotations = Math.max(Math.min(desiredElevatorRotations, elevatorStop), 0);

    elevatorPID.setP(0.2);
    elevatorPID.setI(elevatorIWidget.getDouble(0));
    elevatorPID.setD(elevatorDWidget.getDouble(0));
    elevatorPID.setSetpoint(desiredElevatorRotations);
    elevatorPower = Math.max(
      Math.min(
        elevatorPID.calculate(elevatorLeftEncoder.get() - elevatorEncoderOffset),
        Constants.MAX_ELEVATOR_MOTOR_POWER
      ),
      -1 * Constants.MAX_ELEVATOR_MOTOR_POWER
    );

    elevatorPower = elevatorPower < -0.1 ? -0.1 : elevatorPower; 
    
    System.out.format("desired: %.3f current: %.3f elevator power: %.3f\n", 
      desiredElevatorRotations, 
      elevatorLeftEncoder.get() - elevatorEncoderOffset, 
      elevatorPower);
      elevatorMotors.set(elevatorPower + .08);
  }

  private void turretPeriodic() {
    double turretPow;

    turretPID.setP(turretPWidget.getDouble(0.9) * 10);
    turretPID.setI(turretIWidget.getDouble(0) * 10);
    turretPID.setD(turretDWidget.getDouble(0) * 10);

    
    //TODO: add wraparound protection!
    //TODO: finish ramp soak on turret
    
    turretPow = turretPID.calculate((turretEncoder.getAbsolutePosition() - 0.393 + 1)%1);
    
    //ramp soak for smooth startup
    if(turretPID.atSetpoint()){
      turretRampTime = System.currentTimeMillis();
    }
    //if(System.currentTimeMillis() - turretRampTime <= 2000.0 && !turretPID.atSetpoint()){
    //  turretPow *= 1/((Math.abs(System.currentTimeMillis() - turretRampTime)+1.0)/2000.0);
    //}
    turretMotor.set(turretPow);
  }

  // Called from Robot
  @Override
  public void periodic() {
    elevatorPeriodic();
    // System.out.format("Left Encoder: %.3f, Right Encoder: %.3f\n", elevatorLeftEncoder.get(), elevatorRightEncoder.get());
    // double ly = -Controllers.getInstance().get(Controllers.ControllerName.MANIP, Controllers.Axis.LY);
    // double addPower = .08;//elevatorDWidget.getDouble(0);

    
    // elevatorMotors.set(ly/2.0 + addPower);
    turretPeriodic();
    //Medium l 3.302 r -2.610

    initialPeriodic = false;
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }

  public static TurretvatorSubsystem getInstance() {
    if (instance == null) {
      instance = new TurretvatorSubsystem();
    }
    return instance;
  }
}
