package frc.robot.states;

import frc.robot.Controllers;
import frc.robot.Controllers.*;
import frc.robot.Event;
import frc.robot.RobotStateManager;
import frc.robot.subsystems.*;

public class TeleOpState implements frc.robot.State{

    private String name, parent;
    private DriveSubsystem driveSub;
    private Controllers controllers;
    
    public TeleOpState(String name, String parent){
        this.name = name;
        this.parent = parent;
        driveSub = DriveSubsystem.getInstance();
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
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        driveSub.arcadeDrive(controllers.get(ControllerName.DRIVE, Axis.RY), controllers.get(ControllerName.DRIVE, Axis.LX));
    }

    @Override
    public boolean handleEvent(Event event, RobotStateManager rs) {
        switch(event.button){
            case A:
                switch(event.action){
                    case PRESSED:
                        rs.setState("disabled");
                        return true;
                    default:
                        break;
                }
            case RB:
                switch(event.action){
                    case PRESSED:
                        driveSub.shift(TransmissionState.LOW_RATIO);
                        return true;
                    default:
                        break;
                }
            case LB:
                switch(event.action){
                    case PRESSED:
                        driveSub.shift(TransmissionState.HIGH_RATIO);
                        return true;
                    default:
                        break;
                }
            default:
                return false;
        }
    }
    
}