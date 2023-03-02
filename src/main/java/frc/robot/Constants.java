// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  //CAN IDs (leave 0 blank to avoid confliction with new devices)
  public static final int LEFT_DRIVE_ID_1 = 2;
  public static final int LEFT_DRIVE_ID_2 = 3;
  public static final int RIGHT_DRIVE_ID_1 = 4;
  public static final int RIGHT_DRIVE_ID_2 = 5;
  public static final int LEFT_ELEVATOR_ID = 6;
  public static final int RIGHT_ELEVATOR_ID = 7;
  public static final int TURRET_ID = 8;
  public static final int DEPLOY_INTAKE_ID = 9;
  public static final int LEVER_INTAKE_ID = 10;
  public static final int ROLLER_INTAKE_ID = 11;

  // Balancing constants
  public static final double PLATFORM_BALANCE_YAW_THRESHOLD_DEG = 5;
  public static final double PLATFORM_BALANCE_PITCH_THRESHOLD_DEG = 3;
  public static final double PLATFORM_YAW_DEG = 0;
  public static final double PLATFORM_DRIVE_PLATFORM_YAW_DELTA = 5;

  //REVPH Solenoid Chanels
  public static final int LEFT_DRIVE_SOLENOID = 0;
  public static final int RIGHT_DRIVE_SOLENOID = 1;
  public static final int GRIPPER_SOLENOID_A = 3;
  public static final int GRIPPER_SOLENOID_B = 4;

  //DIO ports
  public static final int LEFT_DRIVE_ENCODER_1 = 0;
  public static final int LEFT_DRIVE_ENCODER_2 = 1;
  public static final int RIGHT_DRIVE_ENCODER_1 = 2;
  public static final int RIGHT_DRIVE_ENCODER_2 = 3;
  public static final int TURRET_ENCODER = 4;
  public static final int LEFT_ELEVATOR_ENCODER = 5;
  public static final int RIGHT_ELEVATOR_ENCODER = 6;
  public static final int DEPLOY_INTAKE_ENCODER = 7;
  public static final int LEVER_INTAKE_ENCODER = 8;

  // Shuffleboard junk
  public static final String MAIN_SHUFFLEBOARD_TAB = "Shuffleboard";

  // Elevator junk
  public static final double MAX_ELEVATOR_MOTOR_POWER = 0.25;
  public static final double ELEVATOR_CM_PER_ROTATION = 1; // TODO: Set me!
  public static final double ELEVATOR_PITCH_DEG = 15;
}
