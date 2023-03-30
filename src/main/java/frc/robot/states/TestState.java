package frc.robot.states;

import frc.robot.statemachine.Event;
import edu.wpi.first.wpilibj.RobotState;
import frc.robot.statemachine.State;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.TurretvatorSubsystem;
import frc.robot.sensors.IMU;
import frc.robot.statemachine.Controllers;
import frc.robot.statemachine.RobotStateManager;
import frc.robot.statemachine.Controllers.*;

public class TestState implements State{
    private String name, parent;
    private TurretvatorSubsystem tvSubsystem;
    private DriveSubsystem driveSubsystem;
    private IMU imu;
    private Controllers controllers;
    
    public TestState(String name, String parent){
        this.name = name;
        this.parent = parent;
        tvSubsystem = TurretvatorSubsystem.getInstance();
        driveSubsystem = DriveSubsystem.getInstance();
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
    public void enter() {
    }

    @Override
    public void leave() {
    }

    @Override
    public void periodic(RobotStateManager rs) {
    }

    @Override
    public boolean handleEvent(Event event, RobotStateManager rs){
        return false;
    }
}
