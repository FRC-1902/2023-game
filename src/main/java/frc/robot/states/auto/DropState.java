package frc.robot.states.auto;

import frc.robot.statemachine.State;
import frc.robot.statemachine.RobotStateManager;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.TurretvatorSubsystem;

public class DropState implements State{
    private String name, parent;
    private TurretvatorSubsystem tvSubsystem;
    private int stage;
    private long dropStartTime;
    private long loopStartTime;
    private DriveSubsystem driveSubsystem;

    public DropState(String name, String parent){
        this.name = name;
        this.parent = parent;
        tvSubsystem = TurretvatorSubsystem.getInstance();
        driveSubsystem = DriveSubsystem.getInstance();
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
        stage = 0;
        tvSubsystem.setGripper(true);
        
        loopStartTime = System.currentTimeMillis();
        System.out.println("entered" + name);
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        switch(stage) {
            case 0:
                if(System.currentTimeMillis() - loopStartTime > 500){
                    stage++;
                    System.out.println("Entered HIGH elevator set");
                }
                break;
            case 1:// extend elavator
                
                tvSubsystem.elevatorSet(3.6); 
                if(tvSubsystem.isExtended()){
                    loopStartTime = System.currentTimeMillis();
                    stage++;
                    System.out.println("Entered wait");
                }
                break;
            case 2:
                
                if(System.currentTimeMillis() - loopStartTime > 1000) {
                    stage++;
                    dropStartTime = System.currentTimeMillis();
                    System.out.println("Entered gripper open");
                }
                break;
            case 3:// drop game element
                tvSubsystem.setGripper(false);
                if(System.currentTimeMillis() - dropStartTime > 500) {
                    stage++;
                    dropStartTime = System.currentTimeMillis();
                    System.out.println("Entered elevator half retraction");
                }
                break;
            case 4:// retract elevator
                tvSubsystem.elevatorSet(1.25); //leave out a bit for forward CG
                if(System.currentTimeMillis() - dropStartTime > 500) stage++;
                break;
            default:
                rs.setState("path");
                break;
            
        }
    }
}
