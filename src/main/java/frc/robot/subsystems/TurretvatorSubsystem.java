// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.HashMap;
import java.util.Map;

import com.revrobotics.CANSparkMax;
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

public class TurretvatorSubsystem extends SubsystemBase {
  private static TurretvatorSubsystem instance;

  public static final int throughboreCPR = 1024; //TODO: check me
  public static final int turretCenter = throughboreCPR/2; //TODO: set me
  public static final int turretMaxAngle = 90;
  public static final int elevatorStop = 0; //TODO: set me

  private double desiredElevatorDistance = 0;
  private double elevatorEncoderOffset = 0;

  private boolean initialPeriodic = true;

  private GenericEntry turretPWidget, turretIWidget, turretDWidget, elevatorPWidget, elevatorIWidget, elevatorDWidget;

  private CANSparkMax elevatorLeft, elevatorRight, turretMotor;
  private MotorControllerGroup elevatorMotors;
  private DutyCycleEncoder elevatorLeftEncoder, elevatorRightEncoder, turretEncoder;
  private PIDController elevatorPID, turretPID;
  private Solenoid gripperSolenoidA, gripperSolenoidB;

  public void initializeShuffleBoardWidgets() {
    ShuffleboardLayout dashboardLayout = Shuffleboard.getTab(Constants.MAIN_SHUFFLEBOARD_TAB)
      .getLayout("Turret PID", BuiltInLayouts.kList)
      .withSize(4, 4);

    turretPWidget = dashboardLayout
      .add("Turret PID - Proportional", 0)
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
    elevatorMotors = new MotorControllerGroup(elevatorLeft, elevatorRight);
    elevatorLeftEncoder = new DutyCycleEncoder(Constants.LEFT_ELEVATOR_ENCODER);
    elevatorRightEncoder = new DutyCycleEncoder(Constants.RIGHT_ELEVATOR_ENCODER);
    //TODO: Tune me
    elevatorPID = new PIDController(0, 0, 0);
    elevatorPID.setTolerance(2); //TODO: set me

    turretMotor = new CANSparkMax(Constants.TURRET_ID, MotorType.kBrushless);
    turretEncoder = new DutyCycleEncoder(Constants.TURRET_ENCODER);
    //TODO: Tune me
    turretPID = new PIDController(0, 0, 0);
    turretPID.enableContinuousInput(0, throughboreCPR);
    turretPID.setTolerance(2); //TODO: set me

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
    turretPID.setSetpoint(turretCenter);
  }

  /**
   * @return whether or not the turret is centered
   */
  public boolean isCentered() {
    return turretPID.atSetpoint() && turretPID.getSetpoint() == turretCenter;
  }

  /**
   * Sets turret to specified degree
   * turns with PID
   * @param degrees degree set (+/- max turret angle)
   */
  public void turretSet(double degrees){
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
  public Map<Enum<ElevatorStage>, Integer> elevatorMap = 
    new HashMap<Enum<ElevatorStage>, Integer>() {{
      put(ElevatorStage.HIGH, 4);
      put(ElevatorStage.MIDDLE,3);
      put(ElevatorStage.LOAD, 2);
      put(ElevatorStage.DOWN, 1);
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
    double desiredElevatorRotations = 
      desiredElevatorDistance / (Math.cos(Math.toRadians(90 - Math.abs(turretEncoder.getAbsolutePosition()))) *
        Math.cos(Math.toRadians(Constants.ELEVATOR_PITCH_DEG)) *
        Constants.ELEVATOR_CM_PER_ROTATION);

    if (desiredElevatorRotations > elevatorStop || desiredElevatorRotations < 0)
      System.out.println("Elevator is extended to extreme!");

    desiredElevatorRotations = Math.max(Math.min(desiredElevatorRotations, elevatorStop), 0);

    elevatorPID.setP(elevatorPWidget.getDouble(0));
    elevatorPID.setI(elevatorIWidget.getDouble(0));
    elevatorPID.setD(elevatorDWidget.getDouble(0));
    elevatorPID.setSetpoint(desiredElevatorRotations * throughboreCPR - elevatorEncoderOffset);
    elevatorPower = Math.min(
      Math.max(
        elevatorPID.calculate(elevatorLeftEncoder.get()),
        Constants.MAX_ELEVATOR_MOTOR_POWER
      ),
      -1 * Constants.MAX_ELEVATOR_MOTOR_POWER
    );
    
    elevatorMotors.set(elevatorPower);
  }

  private void turretPeriodic() {
    turretPID.setP(turretPWidget.getDouble(0));
    turretPID.setI(turretIWidget.getDouble(0));
    turretPID.setD(turretDWidget.getDouble(0));

    turretMotor.set(turretPID.calculate(turretEncoder.getAbsolutePosition()));
  }

  // Called from Robot
  @Override
  public void periodic() {
    elevatorPeriodic();
    turretPeriodic();

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
