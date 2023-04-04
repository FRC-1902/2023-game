// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.Map;

import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.robot.states.*;
import frc.robot.states.auto.*;
import frc.robot.states.balance.AutoBalanceState;
import frc.robot.states.balance.BalanceOnPlatformState;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.LEDSubsystem;
import frc.robot.subsystems.TurretvatorSubsystem;
import frc.robot.subsystems.TurretvatorSubsystem.ElevatorStage;
import frc.robot.path.Paths;
import frc.robot.sensors.IMU;
import frc.robot.statemachine.Controllers;
import frc.robot.statemachine.RobotStateManager;
import frc.robot.statemachine.State;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private RobotStateManager rs;
  private Controllers controllers;
  private TurretvatorSubsystem turretvatorSubsystem;
  private DriveSubsystem driveSubsystem;
  private IMU imu;
  private SendableChooser<Autos> auto;
  private LEDSubsystem ledSubsystem;
  
  public enum Autos {
    BALANCE,
    COMMUNITY,
    NOTHING
  }

  public static Autos chosenAuto = Autos.NOTHING;

  public void initializeShuffleBoardWidgets() {
    PowerDistribution pdh;
    ShuffleboardTab dashboardTab = Shuffleboard.getTab(Constants.MAIN_SHUFFLEBOARD_TAB);

    ShuffleboardLayout pdhLayout = 
      dashboardTab.getLayout("Power Distribution Panel", BuiltInLayouts.kList);
    ShuffleboardLayout stateMachineLayout = 
      dashboardTab.getLayout("State Machine", BuiltInLayouts.kList);
    
    ShuffleboardLayout autoLayout = dashboardTab.getLayout("Auto", BuiltInLayouts.kList);

    if (RobotBase.isReal()) {
      pdh = new PowerDistribution(Constants.PDH_ID, ModuleType.kRev);
      
      pdhLayout.addDouble("Battery Voltage", pdh::getVoltage)
        .withWidget(BuiltInWidgets.kGraph)
        .withProperties(Map.of("Unit", "V"));
      pdhLayout.addDouble("Total Output Current", pdh::getTotalCurrent)
        .withWidget(BuiltInWidgets.kGraph)
        .withProperties(Map.of("Unit", "A"));
      pdhLayout.addDouble("PDH Temperature", pdh::getTemperature)
        .withWidget(BuiltInWidgets.kGraph)
        .withProperties(Map.of("Unit", "deg C"));
    }
    auto = new SendableChooser<Autos>();
    
    auto.addOption("Exit Community", Autos.COMMUNITY);
    auto.addOption("Balance", Autos.BALANCE);
    auto.addOption("Nothing", Autos.NOTHING);
    
    autoLayout.add(auto);

    stateMachineLayout.addString("Current State", () -> {
      State currState = rs.getCurrentState();

      if (currState == null)
        return "Root";
      return currState.getName();
    });
  }

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    DataLogManager.start();
    DataLogManager.log("Initializing robot...");

    // Starts the recording of DS and joystick data
    DriverStation.startDataLog(DataLogManager.getLog());

    // autonomous chooser on the dashboard.
    controllers = Controllers.getInstance();
    ledSubsystem = LEDSubsystem.getInstance();

    rs = RobotStateManager.getInstance();
    rs.addStates(
      new DisabledState("disabled", null),
      new TeleOpState("teleOp", null),
      new BalanceState("balance", null),
      new AutoState("auto", null),
      new AutoBalanceState("autoBalance", null),
      new PickupState("pickup", "auto"),
      new DropState("drop", null),
      new VisionAlignState("visionAlign", "auto"),
      new PathState("path", null),
      new TurretState("turret", "path"),
      new TestState("test", null)
     );

    // We have to initialize these last, because they depend on getting their.
    rs.addStates(
      new BalanceOnPlatformState("balancePlatform", "balance")
    );

    initializeShuffleBoardWidgets();

    rs.startRobot("disabled");
    turretvatorSubsystem = TurretvatorSubsystem.getInstance();
    driveSubsystem = DriveSubsystem.getInstance();
    imu = IMU.getInstance();

    DataLogManager.log("Robot initialized");
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    rs.periodic();

    driveSubsystem.logPeriodic();
    imu.logPeriodic();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
    rs.setState("disabled");
    turretvatorSubsystem.enablePID(false);
    turretvatorSubsystem.resetWatchdogs();
    ledSubsystem.setRGB(0, 20, 0);
    DataLogManager.log("Robot disabled");
  }

  @Override
  public void disabledPeriodic() {
  }

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    chosenAuto = auto.getSelected();
    switch(chosenAuto){
      case BALANCE:
        Paths.getInstance().readPathArray(Paths.pathName.BALANCE);
        DataLogManager.log("balance");
        rs.setState("drop");
        
        break;
      case COMMUNITY:
        Paths.getInstance().readPathArray(Paths.pathName.REVERSE);
        DataLogManager.log("community");
        rs.setState("drop");
        break;
      default:
        DataLogManager.log("nothing");
        break;
    }

    ledSubsystem.setRGB(0, 255, 0);
    
    DataLogManager.log("Robot autonomous initialized");
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    turretvatorSubsystem.periodic();
  }

  @Override
  public void teleopInit() {
    turretvatorSubsystem.elevatorSet(ElevatorStage.DOWN);
    rs.setState("teleOp");
    ledSubsystem.setRGB(0, 255, 0);
    DataLogManager.log("Robot teleop initialized");
    System.out.println("Robot teleop initialized");
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    controllers.eventPeriodic();
    turretvatorSubsystem.periodic();
  }

  @Override
  public void close(){
    rs.setState("disabled");
    rs.periodic();
  }

  @Override
  public void testInit() {
    rs.setState("autoBalance");
    DataLogManager.log("Robot test initialized");
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
    turretvatorSubsystem.periodic();
    controllers.eventPeriodic();
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
    controllers.eventPeriodic();
  }
}
