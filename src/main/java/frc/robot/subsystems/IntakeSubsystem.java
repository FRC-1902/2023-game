// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.HashMap;
import java.util.Map;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class IntakeSubsystem extends SubsystemBase {
  private static IntakeSubsystem instance;
  private CANSparkMax deployMotor, leverMotor, rollerMotor;
  private DutyCycleEncoder deployEncoder, leverEncoder;
  private PIDController deployPID, leverPID;
  
  private static final int ENCODER_CPR = 1024;
  private boolean leverSide, leverPIDEnabled;
  private double leverEncoderPrev;

  public static enum DeployStage{
    STOW,LOAD,LOADDOWN,DOWN
  }

  //TODO: set me
  public Map<Enum<DeployStage>, Integer> deployMap = 
        new HashMap<Enum<DeployStage>, Integer>() {{
            put(DeployStage.STOW, 1);
            put(DeployStage.LOAD, 2);
            put(DeployStage.LOADDOWN,3);
            put(DeployStage.DOWN, 4);
        }};

  public IntakeSubsystem() {
    deployMotor = new CANSparkMax(Constants.DEPLOY_INTAKE_ID, MotorType.kBrushless);
    leverMotor = new CANSparkMax(Constants.LEVER_INTAKE_ID, MotorType.kBrushless);
    rollerMotor = new CANSparkMax(Constants.ROLLER_INTAKE_ID, MotorType.kBrushless);

    deployEncoder = new DutyCycleEncoder(Constants.DEPLOY_INTAKE_ENCODER);
    leverEncoder = new DutyCycleEncoder(Constants.LEVER_INTAKE_ENCODER);
    leverEncoder.setPositionOffset(0);//TODO: set me
    leverEncoderPrev = leverEncoder.getAbsolutePosition();

    //TODO: set me to where intake can't go
    deployEncoder.setPositionOffset(0);

    //TODO: tune me
    deployPID = new PIDController(0, 0, 0);
    leverPID = new PIDController(0, 0, 0);

    deployIntake(DeployStage.STOW);
    leverPID.enableContinuousInput(0, 360);
    deployPID.enableContinuousInput(0, ENCODER_CPR);
    deployPID.setTolerance(2);

    initializeShuffleBoardWidgets();
  }

  public void initializeShuffleBoardWidgets(){
    ShuffleboardLayout dashboardLayout = Shuffleboard.getTab(Constants.MAIN_SHUFFLEBOARD_TAB)
      .getLayout("Intake", BuiltInLayouts.kList)
      .withSize(4, 4);

    dashboardLayout.addDouble("Lever Pos", () -> getLeverPos())
      .withProperties(Map.of("Min", 0, "Max", 360))
      .withWidget(BuiltInWidgets.kNumberBar);
    dashboardLayout.addDouble("Deploy Pos", () -> deployEncoder.getAbsolutePosition())
      .withProperties(Map.of("Min", 0, "Max", 360))
      .withWidget(BuiltInWidgets.kNumberBar);
    dashboardLayout.addDouble("Roller Pow", () -> rollerMotor.get())
      .withWidget(BuiltInWidgets.kNumberBar);
  }

  /**
   * Sets the deploy position for the intake
   * @param deployState boolean to set if deployed or not
   */
  public void deployIntake(DeployStage deployStage){
    //remember that it is in a 2:1 ratio from encoder turns to deployed turns
    deployPID.setSetpoint(deployMap.get(deployStage));
  }

  /**
   * @return if intake is within threshold from PID setpoint
   */
  public boolean isDeployed(){
    return deployPID.atSetpoint();
  }

  /**
   * //TODO: specify power to direction correlation
   * @param pow -1 to 1 motor power
   */
  public void setLeverPow(double pow){
    leverPIDEnabled = false;
    leverMotor.set(pow);
  }

  /**
   * poll in periodic for all lever code to work
   */
  public void updateLeverSide(){
    double leverEncoderPos = leverEncoder.getAbsolutePosition();
    if(Math.abs(leverEncoderPos - leverEncoderPrev) >= ENCODER_CPR/4){leverSide = !leverSide;}
    leverEncoderPrev=leverEncoderPos;
  }

  /**
   * Re-maps lever encoder for 2:1 ratio
   * <p>needs constant polling of IntakeSubsystem.periodic() to work</p>
   * @return enocder pos 0 to 360
   */
  public double getLeverPos(){
    double pos;
    pos = (leverEncoder.getAbsolutePosition() / ENCODER_CPR) * 180;
    if(leverSide){
      pos += 180;
    }
    return pos;
  }

  /**
   * @param pos angle where 0 is down
   * @param dir LeverDir enum IN/OUT, direction that the lever spins
   */
  public void setLeverPos(double pos){
    leverPIDEnabled = true;
    leverPID.setSetpoint(pos);
  }

  /**
   * Sets if the leverPID runs or not
   * <p>handled by using setLeverPos() or setLeverPOW() automatically</p>
   * @param leverPIDEnabled boolean
   */
  public void enableLeverPID(boolean leverPIDEnabled){
    this.leverPIDEnabled = leverPIDEnabled;
  }

  /**
   * @param pow -1 to 1 motor power
   */
  public void setRollerPow(double pow){
    rollerMotor.set(pow);
  }

  /**
   * Call me periodically when robot is enabled
   * PIDs intake deploy to setpoint
   * lever side detection for bad ratio
   */
  public void enabledPeriodic(){
    double deployPow;
    deployPow = deployPID.calculate(deployEncoder.getAbsolutePosition());
    deployMotor.set(deployPow);

    if(leverPIDEnabled){
      leverPID.calculate(getLeverPos());
    }

    updateLeverSide();
  }

  public void disabledPeriodic(){
    updateLeverSide();
  }

  public static IntakeSubsystem getInstance() {
    if (instance == null) {
      instance = new IntakeSubsystem();
    }

    return instance;
  }
}
