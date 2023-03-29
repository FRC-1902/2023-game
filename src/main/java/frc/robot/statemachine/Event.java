
package frc.robot.statemachine;

import frc.robot.statemachine.Controllers.*;

public class Event {
    public final Action action;
    public final Button button;
    public final ControllerName controllerName;
    public Event(Button button, Action action, ControllerName controllerName){
        this.button = button;
        this.action = action;
        this.controllerName = controllerName;
    }
}
