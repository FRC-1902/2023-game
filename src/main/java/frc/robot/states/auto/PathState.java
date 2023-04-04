package frc.robot.states.auto;

import frc.robot.Robot;
import frc.robot.statemachine.State;
import frc.robot.path.Paths;
import frc.robot.statemachine.RobotStateManager;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.LEDSubsystem;

import javax.xml.crypto.Data;

import org.json.simple.JSONObject;

import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.util.datalog.IntegerLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.Timer;


public class PathState implements State{
    private String name;
    private String parent;

    DriveSubsystem driveSubsystem;

    private IntegerLogEntry frameLogger;

    double beganAvgDist;
    double beganLeftDist;
    int currentFrame;
    Timer timer;
    LEDSubsystem ledSubsystem;

    int startCheckFrame = 1;

    boolean firstLoop = true;
    
    public PathState(String name, String parent){
        this.name = name;
        this.parent = parent;
        ledSubsystem = LEDSubsystem.getInstance();
        timer = new Timer();
        timer.start();
        frameLogger = new IntegerLogEntry(DataLogManager.getLog(), "DriveSubsystem/PathingFrame");
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
    public void enter() {
        driveSubsystem = DriveSubsystem.getInstance();

        beganLeftDist = driveSubsystem.leftEncoder.getDistance();
        beganAvgDist = (driveSubsystem.leftEncoder.getDistance() + driveSubsystem.rightEncoder.getDistance())/2;

        driveSubsystem.setPIDEnable(true);

        startCheckFrame = 1;
        
        firstLoop = true;
    }

    @Override
    public void leave() {
        ledSubsystem.setRGB(0, 255, 0);
        driveSubsystem.setPIDEnable(false);
        DriveSubsystem.getInstance().velocityPID(0, 0);
    }

    private void handleExit(RobotStateManager rs){
        switch(Robot.chosenAuto){
            case BALANCE:
                rs.setState("autoBalance");
                break;
            default:
                rs.setState("disabled");
        }
    }
    @Override
    public void periodic(RobotStateManager rs) {

        if(firstLoop){
            timer.reset();
            firstLoop = false;
        }

        JSONObject[] frames = Paths.getInstance().getJSONObjectArray();

        if(frames.length <= 1) return;

        double velocity = 0.0;
        double angularVelocity = 0.0;
        
        double currentTime = timer.get();

        //find current position in path
        if(startCheckFrame == frames.length - 1) {
            handleExit(rs);
        }
        for(int i = startCheckFrame; i < frames.length; i++){

            double previousTime = ((Number) frames[i-1].get("time")).doubleValue();
            double nextTime = ((Number) frames[i].get("time")).doubleValue();
            
            frameLogger.append(i);

            if(nextTime > currentTime){
                //find forward velocity
                velocity = //((Number)frames[i].get("velocity")).doubleValue();
                    lerp(
                        ((Number) frames[i-1].get("velocity")).doubleValue(),
                        ((Number) frames[i].get("velocity")).doubleValue(),
                        (currentTime - previousTime)/(nextTime - previousTime)
                    );

                //find angular velocity
                angularVelocity = 
                    lerp(
                        ((Number) frames[i-1].get("angularVelocity")).doubleValue(),
                        ((Number) frames[i].get("angularVelocity")).doubleValue(),
                        (currentTime - previousTime)/(nextTime - previousTime)
                    );
                startCheckFrame = i;
                break;
            }
        }
        
        //as path progresses, increases in brightness
        ledSubsystem.setRGB(0, (int)(((double)startCheckFrame / frames.length)*255), 0);

        driveSubsystem.velocityPID(velocity, angularVelocity);
    }

    //linear interpolation
    private double lerp(double initialValue, double finalValue, double t){
        return initialValue + t * (finalValue - initialValue);
    }
}