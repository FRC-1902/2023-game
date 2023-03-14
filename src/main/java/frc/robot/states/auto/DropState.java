package frc.robot.states.auto;

import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.TurretvatorSubsystem;

public class DropState implements State{
    private String name, parent;
    private TurretvatorSubsystem tvSub;
    private int state;
    private long dropStartTime;

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
        System.out.println("entered" + name);
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        switch(state) {
            case 0:// extend elavator
                tvSub.elevatorSet(4.0);
                if(tvSub.isExtended()){
                    state++;
                    dropStartTime = System.currentTimeMillis();
                }
                break;
            case 1:// drop game element
                tvSub.setGripper(false);
                if(System.currentTimeMillis() - dropStartTime > 1000) {
                    state++;
                }
                break;
            case 2:// retract elevator
                tvSub.elevatorSet(0.2);
                if(tvSub.isExtended()) state++;
                break;
            default:
                rs.setState("disabled");
                break;
            
        }
        /**
         * set outtake to open
         * exit state when finished opening
         */
    }
}
