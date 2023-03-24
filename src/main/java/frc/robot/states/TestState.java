package frc.robot.states;

import frc.robot.Controllers;
import frc.robot.Event;
import edu.wpi.first.wpilibj.RobotState;
import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.DriveSubsystem;
// import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.TurretvatorSubsystem;
// import frc.robot.subsystems.IntakeSubsystem.DeployStage;
import frc.robot.Controllers.*;
import frc.robot.sensors.IMU;

public class TestState implements State{
    private String name, parent;
    private TurretvatorSubsystem tvSub;
    private DriveSubsystem driveSub;
    private IMU imu;
    private int stage;
    private Controllers controllers;
    
    public TestState(String name, String parent){
        this.name = name;
        this.parent = parent;
        tvSub = TurretvatorSubsystem.getInstance();
        driveSub = DriveSubsystem.getInstance();
        imu = IMU.getInstance();
        controllers = Controllers.getInstance();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public void Enter() {
        System.out.println("entered " + name);
        // driveSub.setPIDEnable(true);
    }

    @Override
    public void Leave() {
        // driveSub.setPIDEnable(false);
        System.out.println("left " + name);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        // driveSub.velocityPID(-controllers.get(ControllerName.DRIVE, Axis.LY)/ 4.0, controllers.get(ControllerName.DRIVE, Axis.RX));
        // tvSub.setTurret(controllers.get(ControllerName.MANIP, Axis.RX) *  -90);
    }

    @Override
    public boolean handleEvent(Event event, RobotStateManager rs){
        return false;
    }
}
