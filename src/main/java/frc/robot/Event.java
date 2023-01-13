
package frc.robot;

import frc.robot.Controller.*;

public class Event {
    public final Action action;
    public final Button button;
    public Event(Button button, Action action){
        this.button = button;
        this.action = action;
    }
}
