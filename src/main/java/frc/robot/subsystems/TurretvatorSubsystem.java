// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class TurretvatorSubsystem extends SubsystemBase {
  private static TurretvatorSubsystem instance;

  public static final int throughboreCPR = 8192; //TODO: check me
  public static final int turretEncoderCenter = throughboreCPR/2; //TODO: set me
  public static final int turretMaxAngle = 90;
  public static final int elevatorStop = 0; //TODO: set me

  private double desiredTurretTicks = 0;

  private double desiredElevatorTicks = 0;
  private double elevatorEncoderOffset = 0;

  private boolean initialPeriodic = true;

  private GenericEntry turretPWidget, turretIWidget, turretDWidget, elevatorPWidget, elevatorIWidget, elevatorDWidget;

  CANSparkMax elevatorLeft, elevatorRight, turretMotor;
  MotorControllerGroup elevatorMotors;
  DutyCycleEncoder elevatorLeftEncoder, elevatorRightEncoder, turretEncoder;
  PIDController elevatorPID, turretPID, absoluteTurretPID;

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

    turretMotor = new CANSparkMax(Constants.TURRET_ID, MotorType.kBrushless);
    turretEncoder = new DutyCycleEncoder(Constants.TURRET_ENCODER);
    //TODO: Tune me
    turretPID = new PIDController(0, 0, 0);
    absoluteTurretPID = new PIDController(0, 0, 0);

    initializeShuffleBoardWidgets();
  }

  /**Call in loop to center turret
   * <p>Centers turret with PID from curret encoder position to turretEncoderCenter</p>
   */
  public void turretCenter(){
    desiredTurretTicks = 0;
  }

  /**
   * Sets turret to specified degree
   * turns with PID
   * @param degrees degree set (+/- 90)
   */
  public void turretSet(double degrees){
    //TODO: test me
    if(Math.abs(degrees) > turretMaxAngle){
      System.out.println("Degree put into TurretvatorSubsystem.turretSet too large!");
      return;
    }

    desiredTurretTicks = degrees * throughboreCPR / 360;
  }

  public void elevatorSet(double rotations) {
    if (rotations < 0 || rotations > elevatorStop) {
      System.out.println("Rotation count put into TurretvatorSubsystem.elevatorSet too large!");
      return;
    }

    desiredElevatorTicks = rotations * throughboreCPR;
  }

  private void elevatorPeriodic() {
    double elevatorPower;

    elevatorPID.setP(elevatorPWidget.getDouble(0));
    elevatorPID.setI(elevatorIWidget.getDouble(0));
    elevatorPID.setD(elevatorDWidget.getDouble(0));

    if (initialPeriodic)
      elevatorEncoderOffset = elevatorLeftEncoder.get();
    
    elevatorPower = Math.min(
      Math.max(
        elevatorPID.calculate(
          elevatorLeftEncoder.get(), 
          desiredElevatorTicks - elevatorEncoderOffset
        ),
        Constants.MAX_ELEVATOR_MOTOR_POWER
      ),
      -1 * Constants.MAX_ELEVATOR_MOTOR_POWER
    );
    
    elevatorLeft.set(elevatorPower);
    elevatorRight.set(elevatorPower);
  }

  private void turretPeriodic() {
    turretPID.setP(turretPWidget.getDouble(0));
    turretPID.setI(turretIWidget.getDouble(0));
    turretPID.setD(turretDWidget.getDouble(0));

    turretMotor.set(turretPID.calculate(turretEncoder.getAbsolutePosition(), desiredTurretTicks));
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
