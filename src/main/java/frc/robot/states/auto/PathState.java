package frc.robot.states.auto;

import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.path.Paths;
import frc.robot.path.Vector2D;
import frc.robot.subsystems.DriveSubsystem;

import org.json.simple.JSONObject;




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
        startTimeSeconds = -1.0;
        System.out.println("entered " + name);
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
        DriveSubsystem.getInstance().velocityPID(0, 0);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        
        if(startTimeSeconds<0.0){
            startTimeSeconds = System.currentTimeMillis()/1000.0;
        }
        
        Object[] objects = Paths.getInstance().getPathArray();

        double currentSecondsSinceStart = System.currentTimeMillis()/1000.0 - startTimeSeconds;

        double velocity = 0.0;
        double angularVelocity = 0.0;
        boolean foundTime = false;
        for(int i = 1; i < objects.length; i++){ //this can be optimized by starting at the i value of the last frame
            
            JSONObject greaterJO = (JSONObject) objects[i];
            
            double greaterPathTime = (double) greaterJO.get("time");
            
            if(greaterPathTime > currentSecondsSinceStart){
                foundTime = true;
                JSONObject lesserJO = (JSONObject) objects[i-1];

                double lesserPathTime = (double) lesserJO.get("time");

                double percentComplete = (currentSecondsSinceStart - lesserPathTime) / (greaterPathTime - lesserPathTime);

                
                double finalVelocity = ((Number) greaterJO.get("velocity")).doubleValue();
                double finalAngularVelocity = ((Number) greaterJO.get("angularVelocity")).doubleValue();

                double initialVelocity = ((Number) lesserJO.get("velocity")).doubleValue();
                double initialAngularVelocity = ((Number) lesserJO.get("angularVelocity")).doubleValue();
                
                velocity = lerp(initialVelocity, finalVelocity, percentComplete);
                angularVelocity = lerp(initialAngularVelocity, finalAngularVelocity, percentComplete);

                DriveSubsystem.getInstance().velocityPID(velocity, angularVelocity);

                // Vector2D currentVelocity = new Vector2D(velocity, 0);
            //TODO:fixme, error
                //currentVelocity.rotateBy(BNO055.getInstance(opmode_t.OPERATION_MODE_GYRONLY, ));

                // velocity += getCorrectingPointAdd(new Vector2D(), new Vector2D(), currentVelocity, 0.3);
                
                System.out.println("Time:\t"+ Math.round(greaterPathTime * 1000.0) / 1000.0+ ",\tVelocity:\t" + Math.round(velocity * 1000.0) / 1000.0 + ",\tAngular Velocity:\t" + Math.round(angularVelocity* 1000.0) / 1000.0);
                break;
            }
            
        }
        if(!foundTime) rs.setState("disabled");
        // DriveSubsystem.getInstance().driveByVelocities(velocity, angularVelocity);
    }

    

    // private double getCorrectingPointAdd(Vector2D target, Vector2D current, Vector2D currentVelocity, double proportional){
    //     //TODO: test
    //     //TODO: fix zero velocity state
    //     Vector2D fromRobotToPointOnPath = target.getSubtracted(current).clone();
    //     double scalarProjection = fromRobotToPointOnPath.dot(currentVelocity)/currentVelocity.getLengthSq();
    //     return currentVelocity.getMultiplied(proportional*scalarProjection).getLength();
    // }

    // private double getCorrectingAngleAdd(Vector2D target, Vector2D current, double proportional){
    //     //distance to current point effects heading
    //     //distance from current angle to target angle
    //     return 0.0;
    // }


    //linear interpolation
    private double lerp(double a, double b, double t){
        return a + t * (b - a);
    }


    

    // private 

    // private Pose2d findTargetVelocity(Translation2d robotToCurrentPointOnPath, Translation2d currentVelocityOfPointOnPath) {
    //     Translation2d targetVelocity = robotToCurrentPointOnPath.plus(currentVelocityOfPointOnPath);
        
    //     Rotation2d rotation = new Rotation2d(targetVelocity.getX(), targetVelocity.getY());
        
    //     return new Pose2d(1, 0 , rotation);
    // }
    
    
}
