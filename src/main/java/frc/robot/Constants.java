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
  private Constants(){
    throw new IllegalStateException("Utility class");
  }
  
  //CAN IDs (leave 0 blank to avoid confliction with new devices)
  public static final int LEFT_DRIVE_ID_1 = 9;
  public static final int LEFT_DRIVE_ID_2 = 8;
  public static final int RIGHT_DRIVE_ID_1 = 12;
  public static final int RIGHT_DRIVE_ID_2 = 11;
  public static final int LEFT_ELEVATOR_ID = 4;
  public static final int RIGHT_ELEVATOR_ID = 5;
  public static final int TURRET_ID = 13;
  public static final int PDH_ID = 15;

  //CTREPCM Solenoid Chanels
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

  //PWM ports
  public static final int LED_PORT = 9;

  //Controllers
  public static final int DRIVE_CONTROLLER_PORT = 0;
  public static final int MANIP_CONTROLLER_PORT = 1;

  // Shuffleboard
  public static final String MAIN_SHUFFLEBOARD_TAB = "Shuffleboard";
  public static final String PID_SHUFFLEBOARD_TAB = "PID";

  // Elevator
  public static final double MAX_ELEVATOR_MOTOR_POWER = 0.5;

  // Balancing
  public static final double PLATFORM_BALANCE_PITCH_THRESHOLD_DEG = 4;

  /* IMPORTANT, SET ME EVERY TIME TURRET SHAFT CHANGED */
  public static final double TURRET_OFFSET = 0.602;
}
