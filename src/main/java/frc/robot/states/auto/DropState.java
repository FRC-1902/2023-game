package frc.robot.states.auto;

import frc.robot.FiniteStateMachine;
import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.TurretvatorSubsystem;
import frc.robot.subsystems.TurretvatorSubsystem.ElevatorStage;

public class DropState implements State{
    private String name, parent;
    private TurretvatorSubsystem tvSub;
    private FiniteStateMachine finiteStateMachine = new FiniteStateMachine();

    public DropState(String name, String parent){
        this.name = name;
        this.parent = parent;
        tvSub = TurretvatorSubsystem.getInstance();
        
        finiteStateMachine.addState(() -> {}, () -> {
            return finiteStateMachine.getTimeSinceStartOfCurrentState() > 500;
        })
        
        .addState(() -> {
            tvSub.elevatorSet(3.6); 
        }, () -> {
            return 
                tvSub.isExtended() ||
                finiteStateMachine.getTimeSinceStartOfCurrentState() > 1000;
        })
        
        .addState(() -> {}, () -> {
            return finiteStateMachine.getTimeSinceStartOfCurrentState() > 1000;
        })

        .addState(() -> {
            tvSub.setGripper(false);
        }, () -> {
            return finiteStateMachine.getTimeSinceStartOfCurrentState() > 1000;
        })

        .addState(() -> {
            tvSub.elevatorSet(ElevatorStage.DOWN);
        }, () -> {
            return 
                tvSub.isExtended() ||
                finiteStateMachine.getTimeSinceStartOfCurrentState() > 500;
        });
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
        
        tvSub.setGripper(true);
        System.out.println("entered" + name);
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        if(!finiteStateMachine.periodic()){
            rs.setState("path");
        }
    }
}
