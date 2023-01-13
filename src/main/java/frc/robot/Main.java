// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;

/**
 * Do NOT add any static variables to this class, or any initialization at all. Unless you know what
 * you are doing, do not modify this file except to change the parameter class to the startRobot
 * call.
 */
public final class Main {
  private Main() {}

  /**
   * Main initialization function. Do not perform any initialization here.
   *
   * <p>If you change your main robot class, change the parameter type.
   */
  public static void main(String... args) {
    RobotBase.startRobot(Robot::new);
  }
}
/**
 * i = 0    t = 1       t = 0
 * i = 1/8  t = 56/57   t = 8/57
 * i = 1/4  t = 12/13   t = 4/13
 * i = 1/2  t = 2/3     t = 2/3
 * i = 3/4  t = 4/13    t = 12/13
 * i = 7/8  t = 8/57    t = 56/57
 * i = 1    t = 0       t = 1
 * 
 * M    6:30
 * T    5:00
 * W    6:00
 * T    7:30
 * F    
 * S    2:00
 * S    
 * 
 * 
 * Woodworking with Grandfather
 * 
 * July 11 - August 29
 * 
 * 27
 * 
 * 
 * Florida Virtual School PE
 * April 20 - June 14
 * 10
 * 
 * Blackwatch Leadership Academy
 * June 1 - June 7
 * 168
 * 
 * Prepparing for robotics season
 * May 30-August 8
 * 4
 * 
 * Spherical Stepper Motor Prototype
 * June 10 - August 7
 * 5
 * 
 * Gym
 * June 9 - August 5
 * 7
 */