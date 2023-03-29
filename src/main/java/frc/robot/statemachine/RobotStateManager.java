package frc.robot.statemachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import frc.robot.statemachine.Event;
import frc.robot.statemachine.State;

public class RobotStateManager{
    private State currentState;
    private State targetState;
    private Map<String, State> stateMap= new HashMap<String, State>();
    private static RobotStateManager instance = null;
  
    public RobotStateManager(){
      currentState = null;
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
     
    public void periodic(){
      updateState();
      State loopingState = currentState;
      while(loopingState!=null){
        loopingState.Periodic(this);
        loopingState = stateMap.get(loopingState.getParent());
      }
    }

    public void handleEvent(Event event){
      updateState();
      State loopingState = currentState;
      while(loopingState != null && !loopingState.handleEvent(event, this)){
        loopingState = stateMap.get(loopingState.getParent());
      }
    }
    
    private State findCommonAncestor(State A, State B){
      State candidateA = A;
      while(true){
        if(candidateA==null)break;
        State candidateB = B;
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
      while(true){
        if(child == null || child == ancestor) break;
        child.Leave();
        if(stateMap.get(child.getParent())==ancestor)break;
        child = stateMap.get(child.getParent());
      }
    }
  
    private void enterTo(State ancestor, State child, State enteringFrom){
      ArrayList<State> lineage = new ArrayList<State>();
      while(true){
        if(child == ancestor) break;
        lineage.add(0, child);
        child = stateMap.get(child.getParent());
      }
      for(State s:lineage){
        s.Enter(enteringFrom);
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