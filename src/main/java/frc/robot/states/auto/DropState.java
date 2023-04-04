package frc.robot.states.auto;

import frc.robot.statemachine.State;
import edu.wpi.first.wpilibj.DataLogManager;
import frc.robot.statemachine.RobotStateManager;
import frc.robot.subsystems.TurretvatorSubsystem;

public class DropState implements State{
    private String name;
    private String parent;
    private TurretvatorSubsystem tvSubsystem;
    private int stage;
    private long dropStartTime;
    private long loopStartTime;

    public DropState(String name, String parent){
        this.name = name;
        this.parent = parent;
        tvSubsystem = TurretvatorSubsystem.getInstance();
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
        stage = 0;
        tvSubsystem.setGripper(true);
        
        loopStartTime = System.currentTimeMillis();
    }

    @Override
    public void leave() {
    }

    @Override
    public void periodic(RobotStateManager rs) {
        switch(stage) {
            case 0:
                if(System.currentTimeMillis() - loopStartTime > 500){
                    stage++;
                    DataLogManager.log("Entered HIGH elevator set");
                }
                break;
            case 1:// extend elavator
                
                tvSubsystem.elevatorSet(3.6); 
                if(tvSubsystem.isExtended()){
                    loopStartTime = System.currentTimeMillis();
                    stage++;
                    DataLogManager.log("Entered wait");
                }
                break;
            case 2:
                
                if(System.currentTimeMillis() - loopStartTime > 1000) {
                    stage++;
                    dropStartTime = System.currentTimeMillis();
                    DataLogManager.log("Entered gripper open");
                }
                break;
            case 3:// drop game element
                tvSubsystem.setGripper(false);
                if(System.currentTimeMillis() - dropStartTime > 500) {
                    stage++;
                    dropStartTime = System.currentTimeMillis();
                    DataLogManager.log("Entered elevator half retraction");
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
