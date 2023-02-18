// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class TurretvatorSubsystem extends SubsystemBase {
  private static TurretvatorSubsystem instance;

  public static final int throughboreCPR = 8192; //TODO: check me
  public static final int turretEncoderCenter = throughboreCPR/2; //TODO: set me
  public static final int turretMaxAngle = 90;
  public static final int elevatorStop = 0; //TODO: set me

  private double desiredTurretTicks = 0;
  private double turretEncoderOffset = 0;

  private double desiredElevatorTicks = 0;
  private double elevatorLeftEncoderOffset = 0;
  private double elevatorRightEncoderOffset = 0;
  
  private boolean turretHasCentered = false;

  private boolean initialPeriodic = true;

  // The turret motors have 5.1:1 ratio.
  // The turret encoders are also attached directly to the motors, while the encoders are attached to the output of the gearbox.
  CANSparkMax elevatorLeft, elevatorRight, turretMotor;
  MotorControllerGroup elevatorMotors;
  DutyCycleEncoder elevatorLeftEncoder, elevatorRightEncoder, turretEncoder;
  PIDController elevatorLeftPID, elevatorRightPID, turretPID, absoluteTurretPID;

  public TurretvatorSubsystem() {
    elevatorLeft = new CANSparkMax(Constants.LEFT_ELEVATOR_ID, MotorType.kBrushless);
    elevatorRight = new CANSparkMax(Constants.RIGHT_ELEVATOR_ID, MotorType.kBrushless);
    elevatorMotors = new MotorControllerGroup(elevatorLeft, elevatorRight);
    elevatorLeftEncoder = new DutyCycleEncoder(Constants.LEFT_ELEVATOR_ENCODER);
    elevatorRightEncoder = new DutyCycleEncoder(Constants.RIGHT_ELEVATOR_ENCODER);
    //TODO: Tune me
    elevatorLeftPID = new PIDController(0, 0, 0);
    elevatorRightPID = new PIDController(0, 0, 0);

    turretMotor = new CANSparkMax(Constants.TURRET_ID, MotorType.kBrushless);
    turretEncoder = new DutyCycleEncoder(Constants.TURRET_ENCODER);
    //TODO: Tune me
    turretPID = new PIDController(0, 0, 0);
    absoluteTurretPID = new PIDController(0, 0, 0);
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

    desiredTurretTicks = degrees * throughboreCPR * 5;
  }

  public void elevatorSet(double rotations) {
    if (rotations < 0 || rotations > elevatorStop) {
      System.out.println("Rotation count put into TurretvatorSubsystem.elevatorSet too large!");
      return;
    }

    desiredElevatorTicks = rotations * throughboreCPR;
  }

  private void elevatorPeriodic() {
    if (initialPeriodic) {
      elevatorLeftEncoderOffset = elevatorLeftEncoder.get();
      elevatorRightEncoderOffset = elevatorRightEncoder.get();
    }
    
    elevatorLeft.set(elevatorLeftPID.calculate(elevatorLeftEncoder.get(), desiredElevatorTicks - elevatorLeftEncoderOffset));
    elevatorRight.set(elevatorRightPID.calculate(elevatorRightEncoder.get(), desiredElevatorTicks - elevatorRightEncoderOffset));
  }

  private void turretPeriodic() {
    // Centers the turret souly off of the absolute position.
    if (!turretHasCentered) {
      double currentAbsolutePos = turretEncoder.getAbsolutePosition();
      double absoluteOffset;

      // Adjusts for rollovers
      if (currentAbsolutePos / throughboreCPR < 180)
        absoluteOffset = throughboreCPR;
      else
        absoluteOffset = 0;
    
      turretMotor.set(turretPID.calculate(currentAbsolutePos + absoluteOffset, 360));

      if (!absoluteTurretPID.atSetpoint())
        return;

      turretEncoderOffset = turretEncoder.get();
      turretHasCentered = true;
      turretPID.reset();
    }

    turretMotor.set(turretPID.calculate(turretEncoder.get(), desiredTurretTicks));
  }

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
