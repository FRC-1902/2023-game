// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class TurretvatorSubsystem extends SubsystemBase {
  private static TurretvatorSubsystem instance;

  static final int throughboreCPR = 2048; //TODO: check me
  int turretEncoderCenter = throughboreCPR/2; //TODO: set me
  int turretMaxAngle = 90;
  int elevatorLRMaxDifference = 10; //TODO: set me//max encoder ticks differential between both sides of elevator
  int elevatorLowStop; //TODO: set me
  int elevatorHighStop; //TODO: set me

  CANSparkMax elevatorLeft, elevatorRight, turretMotor;
  MotorControllerGroup elevatorMotors;
  AbsoluteEncoder elevatorLeftEncoder, elevatorRightEncoder, turretEncoder;
  PIDController elevatorPID, turretPID;

  public TurretvatorSubsystem() {
    elevatorLeft = new CANSparkMax(Constants.LEFT_ELEVATOR_ID, MotorType.kBrushless);
    elevatorRight = new CANSparkMax(Constants.RIGHT_ELEVATOR_ID, MotorType.kBrushless);
    elevatorMotors = new MotorControllerGroup(elevatorLeft, elevatorRight);
    elevatorLeftEncoder = elevatorLeft.getAbsoluteEncoder(Type.kDutyCycle);
    elevatorRightEncoder = elevatorRight.getAbsoluteEncoder(Type.kDutyCycle);
    //TODO: Tune me
    elevatorPID = new PIDController(0, 0, 0);

    turretMotor = new CANSparkMax(Constants.TURRET_ID, MotorType.kBrushless);
    turretEncoder = turretMotor.getAbsoluteEncoder(Type.kDutyCycle);
    //TODO: set me to back of turret
    turretEncoder.setZeroOffset(0);
    //TODO: Tune me
    turretPID = new PIDController(0, 0, 0);
  }

  /**Call in loop to center turret
   * <p>Centers turret with PID from curret encoder position to turretEncoderCenter</p>
   */
  public void turretCenter(){
    turretMotor.set(turretPID.calculate(turretEncoder.getPosition(), turretEncoderCenter));
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
    int setAngle = (int)(degrees / 360 * throughboreCPR) + turretEncoderCenter;
    turretMotor.set(turretPID.calculate(turretEncoder.getPosition(), setAngle));
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
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
