package frc.robot.states.auto;

import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.path.Paths;
import frc.robot.subsystems.DriveSubsystem;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.Data;

// import org.json.simple.JSONArray;


public class PathState implements State{
    private String name, parent;

    double startTimeSeconds;
    
    public PathState(String name, String parent){
        this.name = name;
        this.parent = parent;
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
        System.out.println("entered" + name);
        startTimeSeconds = System.currentTimeMillis()/1000.0;
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        
        Object[] objects = Paths.getInstance(Paths.pathName.BLUE).getPathArray().toArray();

        double currentSecondsSinceStart = System.currentTimeMillis()/1000.0 - startTimeSeconds;

        Transform2d velocity = new Transform2d();
        
        for(int i = 1; i < objects.length; i++){
            JSONObject greaterJO = (JSONObject) objects[i];
            double greaterPathTime = (double) greaterJO.get("time");
            
            
            if(greaterPathTime > currentSecondsSinceStart){
                // JSONObject lesserJO = (JSONObject) objects[i-1];
                // double lesserPathTime = (double) lesserJO.get("time");
                velocity = new Transform2d(new Translation2d(0.0, ((Number) greaterJO.get("velocity")).doubleValue()), new Rotation2d(((Number) greaterJO.get("angularVelocity")).doubleValue()));
                System.out.println(velocity.toString());
                break;
            }
            
        }
        
        DriveSubsystem.getInstance().driveByVelocities(velocity);

        
        /** 
         * calculate current point on path
         * calculate target velocity (including getting back to spline)
         * use velocities to control movment
         * increment position on path
         */

        // getPointOnPath(); 
        
    }


    

    // private 

    // private Pose2d findTargetVelocity(Translation2d robotToCurrentPointOnPath, Translation2d currentVelocityOfPointOnPath) {
    //     Translation2d targetVelocity = robotToCurrentPointOnPath.plus(currentVelocityOfPointOnPath);
        
    //     Rotation2d rotation = new Rotation2d(targetVelocity.getX(), targetVelocity.getY());
        
    //     return new Pose2d(1, 0 , rotation);
    // }
    
    
}
