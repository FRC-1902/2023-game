package frc.robot.states.auto;

import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.TurretvatorSubsystem;
import frc.robot.subsystems.TurretvatorSubsystem.ElevatorStage;

public class DropState implements State{
    private String name, parent;
    private TurretvatorSubsystem tvSub;
    private int state;
    private long dropStartTime;
    private long loopStartTime;

    public DropState(String name, String parent){
        this.name = name;
        this.parent = parent;
        tvSub = TurretvatorSubsystem.getInstance();
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
        state = 0;
        tvSub.setGripper(true);
        loopStartTime = System.currentTimeMillis();
        System.out.println("entered" + name);
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        switch(state) {
            case 0:
                if(System.currentTimeMillis() - loopStartTime > 500) state++;
                break;
            case 1:// extend elavator
                System.out.println("Entered HIGH");
                tvSub.elevatorSet(ElevatorStage.HIGH); 
                if(tvSub.isExtended()){
                    loopStartTime = System.currentTimeMillis();
                    state++;
                }
                break;
            case 2:
                System.out.println("entered wait");
                if(System.currentTimeMillis() - loopStartTime > 1000) {
                    state++;
                    dropStartTime = System.currentTimeMillis();
                }
                break;
            case 3:// drop game element
                System.out.println("Entered Gripper open");
                tvSub.setGripper(false);
                if(System.currentTimeMillis() - dropStartTime > 500) {
                    state++;
                }
                break;
            case 4:// retract elevator
                tvSub.elevatorSet(ElevatorStage.DOWN);
                if(tvSub.isExtended()) state++;
                break;
            default:
                rs.setState("path");
                break;
            
        }
        /**
         * set outtake to open
         * exit state when finished opening
         */
    }
}
