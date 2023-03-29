package frc.robot.statemachine;

import frc.robot.statemachine.RobotStateManager;

public interface State {
    String getName();
    String getParent();
    void Enter();
    void Leave();
    void Periodic(RobotStateManager rs);

    default void Enter(State enteredFrom) {
        Enter();
    }

    default boolean handleEvent(Event event, RobotStateManager rs){
        return false;
    }
}
