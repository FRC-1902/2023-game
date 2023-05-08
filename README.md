# FRC Team 1902 - Exploding Bacon - 2023 Charged UP

Welcome to the Github repository for the 2023 code of FRC Team 1902 - Exploding Bacon! This repository contains the codebase that we used for our robot during the 2023 FIRST Robotics Competition season.
### About the Team

FRC Team 1902, Exploding Bacon, is a high school robotics team based in Orlando, Florida. We are a community-driven team that focuses on STEM education and outreach. Our team is dedicated to creating a positive impact in our community through the power of robotics. We run a variety of programs, outreaches, and more, so feel free to visit us on our website: [explodingbacon.com](https://explodingbacon.com/)
### About the Code

The code for our robot is written in Java, using the following external libraries: [WPILib](https://docs.wpilib.org/en/stable/), [REVLib](https://codedocs.revrobotics.com/java/com/revrobotics/package-summary.html), [PathPlanner](https://github.com/mjansen4857/pathplanner). The code is organized into several different packages, each with a specific purpose. Here's a brief overview of the packages in our codebase:

- [src.main.java.frc.robot](https://github.com/FRC-1902/2023-game/tree/main/src/main/java/frc/robot): This package contains the main robot classes, which is responsible for initializing and controlling the robot. It is the head directory for most of the code in this project. Our custom PID controller and constants are also present here.

- [src.main.java.frc.robot.subsystems](https://github.com/FRC-1902/2023-game/tree/main/src/main/java/frc/robot/subsystems): This package contains the different subsystems of our robot, such as the drive train, turretvator (turret + elevator), and LEDs

- [src.main.java.frc.robot.statemachine](https://github.com/FRC-1902/2023-game/tree/main/src/main/java/frc/robot/statemachine): We have left behind the traditional command based architecture for a custon hiearchial state machine. While this works entirely like a hiearchial state machine, we mainly used it as a finite state machine. This also includes our custom event passing and controller framework as well as the state interface.

- [src.main.java.frc.robot.path](https://github.com/FRC-1902/2023-game/tree/main/src/main/java/frc/robot/path): This is the code to parse the JSON files given from path planner and act upon them
    
- [src.main.deploy.pathplanner](https://github.com/FRC-1902/2023-game/tree/main/src/main/deploy/pathplanner): This is where the path planner JSON files are stored after being pregenerated

- [src.main.java.frc.robot.sensors](https://github.com/FRC-1902/2023-game/tree/main/src/main/java/frc/robot/sensors): This is where we have our bno055 IMU initialized

### Getting Started

If you'd like to get started with our codebase, there are a few things you'll need to do:

    - Clone this repository to your local machine.
    - Open the codebase in your preferred Java IDE.
    - Install the WPILib and REVlib.
    - Deploy the code to your robot and start testing!

### Contributing

We welcome contributions from the community! If you find a bug in our code, or if you have an idea for a new feature, please open an issue on this repository. If you'd like to contribute code, please fork this repository and submit a pull request.

### Acknowledgments

This code was written by:
- Tyler Waddell
- Aidan Van Duyne
- Erick Leaf

We'd like to give a special thanks to the sponsors, fellow students, and mentors who have helped us throughout the season. Without their support, this codebase would not have been possible. Thank you!
