package frc.robot;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;



public class FiniteStateMachine {
    ArrayList<FiniteStatePeriodic> states = new ArrayList<>();
    ArrayList<BooleanSupplier> exitConditions = new ArrayList<>();
    int currentState = 0;
    long startTimeOfCurrentState = System.currentTimeMillis();
    boolean isFirstLoop = true;
    

    public FiniteStateMachine addState(FiniteStatePeriodic state, BooleanSupplier exitCondition){
        states.add(state);
        exitConditions.add(exitCondition);
        return this;
    }

    public long getTimeSinceStartOfCurrentState () {
        return System.currentTimeMillis() - startTimeOfCurrentState;
    }

    public boolean periodic() {
        if(isFirstLoop) {
            startTimeOfCurrentState = System.currentTimeMillis();
            isFirstLoop = false;
        }

        if(states.isEmpty()) return false;

        if(exitConditions.get(currentState).getAsBoolean()) {
            currentState++; 
            startTimeOfCurrentState = System.currentTimeMillis();
        }
        
        if(currentState >= states.size()) return false; 
        states.get(currentState).run();
        return true;
    }
}