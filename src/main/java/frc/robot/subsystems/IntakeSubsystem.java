// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class IntakeSubsystem extends SubsystemBase {
  private static IntakeSubsystem instance;
  private CANSparkMax deployMotor, leverMotor, rollerMotor;
  private DutyCycleEncoder deployEncoder, leverEncoder;
  private PIDController deployPID, leverPID;

  //TODO: set/check me
  private static final int DEPLOY_UP_ENC_POS = 0;
  private static final int DEPLOY_LOAD_ENC_POS = 0;
  private static final int DEPLOY_DOWN_ENC_POS = 0;
  
  private static final int ENCODER_CPR = 1024;

  public IntakeSubsystem() {
    deployMotor = new CANSparkMax(Constants.DEPLOY_INTAKE_ID, MotorType.kBrushless);
    leverMotor = new CANSparkMax(Constants.LEVER_INTAKE_ID, MotorType.kBrushless);
    rollerMotor = new CANSparkMax(Constants.ROLLER_INTAKE_ID, MotorType.kBrushless);

    deployEncoder = new DutyCycleEncoder(Constants.DEPLOY_INTAKE_ENCODER);
    leverEncoder = new DutyCycleEncoder(Constants.LEVER_INTAKE_ENCODER);

    //TODO: set me to where intake can't go
    deployEncoder.setPositionOffset(0);

    //TODO: tune me
    deployPID = new PIDController(0, 0, 0);
    leverPID = new PIDController(0, 0, 0);
  }

  public static enum DeployState{
    UP,LOAD,DOWN
  }
  /**
   * Deploys the intake down/up, CALL IN LOOP
   * @param deployState boolean to set if deployed or not
   */
  public void deployIntake(DeployState deployState){
    //remember that it is in a 2:1 ratio from encoder turns to deployed turns
    double pow;
    switch(deployState){
      case UP:
        pow = deployPID.calculate(deployEncoder.getAbsolutePosition(), DEPLOY_UP_ENC_POS);
        break;
      case LOAD:
        pow = deployPID.calculate(deployEncoder.getAbsolutePosition(), DEPLOY_LOAD_ENC_POS);
        break;
      case DOWN:
        pow = deployPID.calculate(deployEncoder.getAbsolutePosition(), DEPLOY_DOWN_ENC_POS);
        break;
      default: pow = 0;
    }
    deployMotor.set(pow);
  }

  /**
   * @param pow -1 to 1 motor power
   */
  public void setLeverPow(double pow){
    leverMotor.set(pow);
  }
  
  public static enum LeverDir{
    IN,OUT
  }

  /**
   * @param pos angle where 0 is down
   * @param dir LeverDir enum IN/OUT, direction that the lever spins
   */
  public void setLeverPos(double pos, LeverDir dir){
    //TODO: write me
    //remember that it is a 2:1 ratio from encoder turns to lever movement
    //set from offset ig
  }

  /**
   * @param pow -1 to 1 motor power
   */
  public void setRollerPow(double pow){
    rollerMotor.set(pow);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }

  public static IntakeSubsystem getInstance() {
    if (instance == null) {
      instance = new IntakeSubsystem();
    }

    return instance;
  }
}
