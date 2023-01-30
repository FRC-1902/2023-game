// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.Map;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.states.*;
import frc.robot.states.auto.*;
import frc.robot.states.balance.DriveOntoPlatformState;
import frc.robot.states.balance.BalanceOnPlatformState;
import frc.robot.states.teleOp.*;
import frc.robot.Controllers.*;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  // private Command m_autonomousCommand;
  //private RobotContainer m_robotContainer;
  private RobotStateManager rs;
  private Controllers ControllerInstance;
  private XboxController driveController;
  private XboxController manipController;
  private Compressor compressor;

  public void initializeShuffleBoardWidgets() {
    ShuffleboardTab dashboardTab = Shuffleboard.getTab(Constants.MAIN_SHUFFLEBOARD_TAB);

    ShuffleboardLayout pdpLayout = 
      dashboardTab.getLayout("Power Distribution Panel", BuiltInLayouts.kList);
    ShuffleboardLayout stateMachineLayout = 
      dashboardTab.getLayout("State Machine", BuiltInLayouts.kList);

    // TODO: Close this lol
    PowerDistribution pdp = new PowerDistribution(1, ModuleType.kRev);
    
    pdpLayout.addDouble("Battery Voltage", pdp::getVoltage)
      .withWidget(BuiltInWidgets.kGraph)
      .withProperties(Map.of("Unit", "V"));
    pdpLayout.addDouble("Total Output Current", pdp::getTotalCurrent)
      .withWidget(BuiltInWidgets.kGraph)
      .withProperties(Map.of("Unit", "A"));
    pdpLayout.addDouble("Total Output Power", pdp::getTotalPower)
      .withWidget(BuiltInWidgets.kGraph)
      .withProperties(Map.of("Unit", "W"));
    pdpLayout.addDouble("PDP Temperature", pdp::getTemperature)
      .withWidget(BuiltInWidgets.kGraph)
      .withProperties(Map.of("Unit", "deg C"));

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
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    compressor = new Compressor(1, PneumaticsModuleType.REVPH);
    compressor.enableDigital();
    ControllerInstance = Controllers.getInstance();
    driveController = ControllerInstance.driveController;
    manipController = ControllerInstance.manipController;

    rs = RobotStateManager.getInstance();
    rs.addStates(
      new DisabledState("disabled", null),
      new TeleOpState("teleOp", null),
      new CenterTurretState("driveTeleOp", "teleOp"),
      new BalanceState("balance", null),
      new AutoState("auto", null),
      new PickupState("pickup", "auto"),
      new DropState("drop", "visionAlign"),
      new VisionAlignState("visionAlign", "auto"),
      new TurretState("turret", "path"),
      new TestState("test", null)
     );

    // We have to initialize these last, because they depend on getting their.
    rs.addStates(
      new DriveOntoPlatformState("drivePlatform", "balance"),
      new BalanceOnPlatformState("balancePlatform", "balance")
    );

    initializeShuffleBoardWidgets();

    rs.startRobot("disabled");
    //m_robotContainer = new RobotContainer();
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
    for(Map.Entry<Enum<Controllers.Button>, Integer> entry : ControllerInstance.buttonMap.entrySet()) {
      
      if(driveController.getRawButtonPressed(entry.getValue())){
        rs.handleEvent(new Event((Button) entry.getKey(), Action.PRESSED, ControllerName.DRIVE));
      }
      if(driveController.getRawButtonReleased(entry.getValue())){
        rs.handleEvent(new Event((Button) entry.getKey(), Action.RELEASED, ControllerName.DRIVE));
      }
      
      if(manipController.getRawButtonPressed(entry.getValue())){
        rs.handleEvent(new Event((Button) entry.getKey(), Action.PRESSED, ControllerName.MANIP));
      }
      if(manipController.getRawButtonReleased(entry.getValue())){
        rs.handleEvent(new Event((Button) entry.getKey(), Action.RELEASED, ControllerName.MANIP));
      }
    }
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    
    // CommandScheduler.getInstance().run();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
    rs.setState("disabled");
  }

  @Override
  public void disabledPeriodic() {
  }

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    rs.setState("auto");
    // m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // // schedule the autonomous command (example)
    // if (m_autonomousCommand != null) {
    //   m_autonomousCommand.schedule();
    // }

    rs.setState("balancePlatform");
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    // if (m_autonomousCommand != null) {
    //   m_autonomousCommand.cancel();
    // }
    rs.setState("teleOp");
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

  }

  @Override
  public void close(){
    rs.setState("disabled");
    rs.periodic();
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    // CommandScheduler.getInstance().cancelAll();
    rs.setState("test");
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}

}
