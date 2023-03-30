package frc.robot.statemachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RobotStateManager{
    private State currentState;
    private State targetState;
    private Map<String, State> stateMap;
    private static RobotStateManager instance = null;
  
    public RobotStateManager(){
      currentState = null;
      stateMap = new HashMap<>();
    }
    
    /**
     * Switch states to targetState
     * updates targetState to null
     */
    private void updateState(){
      if(targetState!=null){
        State ancestor = findCommonAncestor(targetState, currentState);
        State enteringFrom = currentState;

        leaveTo(currentState, ancestor);
        enterTo(ancestor, targetState, enteringFrom);

        currentState = targetState;
        targetState = null;
      }
    }
  
    /**
     * @param targetName 
     * String name of target state.
     * updates state on the next call of periodic   
     */
    public void setState (String targetName){
      targetState = stateMap.get(targetName);
    }
  
    /**
     * @param states
     * Array of State objects. 
     * Adds <Name, Object> to stateMap map
     */
    public void addStates(State... states){
      for(State state:states){
        stateMap.put(state.getName(), state);
      }
    }
  
    /**
     * @param startState
     * String name of state that the robot starts at
     */
    public void startRobot(String startState){
      currentState = stateMap.get(startState);
      enterTo(null, currentState, null);
    }
     
    /**
     * Call me periodically from Robot.java for the state machine to work
     */
    public void periodic(){
      updateState();
      State loopingState = currentState;
      while(loopingState!=null){
        loopingState.periodic(this);
        loopingState = stateMap.get(loopingState.getParent());
      }
    }

    /**
     * Call me to send controller events to the active state hiearchy
     * <p>Will be handled by the state that returns true first in the hiearchy</p>
     * @param event Event objects to send
     */
    public void handleEvent(Event event){
      updateState();
      State loopingState = currentState;
      while(loopingState != null && !loopingState.handleEvent(event, this)){
        loopingState = stateMap.get(loopingState.getParent());
      }
    }
    
    private State findCommonAncestor(State a, State b){
      State candidateA = a;
      while(true){
        if(candidateA==null)break;
        State candidateB = b;
        while(true){
          if(candidateB==null)break;
          if(candidateA==candidateB)return candidateA;
          candidateB = stateMap.get(candidateB.getParent());
        }
        candidateA = stateMap.get(candidateA.getParent());
      }
      return null;
    }

    public State getCurrentState() {
      return currentState;
    }
  
    private void leaveTo(State child, State ancestor){
      while(child != null && child != ancestor){
        child.leave();
        if(stateMap.get(child.getParent())==ancestor) break;
        child = stateMap.get(child.getParent());
      }
    }
  
    private void enterTo(State ancestor, State child, State enteringFrom){
      ArrayList<State> lineage = new ArrayList<>();
      while(true){
        if(child == ancestor) break;
        lineage.add(0, child);
        child = stateMap.get(child.getParent());
      }
      for(State s:lineage){
        s.enter(enteringFrom);
      }
    }

    public State findState(String name) {
      return stateMap.get(name);
    }

    /**
     * @return RobotStateManager instance
    */
    public static RobotStateManager getInstance(){
      if(instance == null){
        instance = new RobotStateManager();
      }
      return instance;
    }
}