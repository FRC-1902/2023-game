package frc.robot;

public interface State {
    String getName();
    String getParent();
    void Enter();
    void Leave();
    void Periodic(RobotStateManager rs);
    default boolean handleEvent(Event event, RobotStateManager rs){
        return false;
    }
}
