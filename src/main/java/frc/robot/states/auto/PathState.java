package frc.robot.states.auto;

import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.path.Paths;
import frc.robot.subsystems.DriveSubsystem;

import org.json.simple.JSONObject;

import edu.wpi.first.wpilibj.Encoder;




// import org.json.simple.JSONArray;


public class PathState implements State{
    private String name, parent;

    double beganAvgDist, beganLeftDist;
    
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
        DriveSubsystem driveSubsystem = DriveSubsystem.getInstance();

        System.out.println("entered " + name);

        beganLeftDist = driveSubsystem.leftEncoder.getDistance();
        beganAvgDist = (driveSubsystem.leftEncoder.getDistance() + driveSubsystem.rightEncoder.getDistance())/2;
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
        DriveSubsystem.getInstance().velocityPID(0, 0);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        Object[] objects = Paths.getInstance().getPathArray();

        DriveSubsystem driveSubsystem = DriveSubsystem.getInstance();
        Encoder leftEncoder = driveSubsystem.leftEncoder, rightEncoder = driveSubsystem.rightEncoder;

        double velocity = 0.0;
        double angularVelocity = 0.0;
        boolean foundStuff = false;

        System.out.format("objects length: %d\n", objects.length);

        for(int i = 1; i < objects.length; i++){ //this can be optimized by starting at the i value of the last frame
            
            JSONObject greaterJO = (JSONObject) objects[i];
            
            double greaterLeftEncoderDist = (double) greaterJO.get("lMeter");
            double greaterRightEncoderDist = (double) greaterJO.get("rMeter");

            double greaterAvgEncoderDist = (greaterLeftEncoderDist + greaterRightEncoderDist) / 2;

            double curLeftEncoderDist = leftEncoder.getDistance();
            double curRightEncoderDist = rightEncoder.getDistance();
            double curAvgEncoderDist = (curLeftEncoderDist + curRightEncoderDist) / 2;
            
            //if(greaterAvgEncoderDist > curAvgEncoderDist - beganAvgDist){
            if(greaterLeftEncoderDist > curLeftEncoderDist - beganLeftDist){
                foundStuff = true;

                System.out.format("found thing!\n");

                JSONObject lesserJO = (JSONObject) objects[i-1];

                double lesserPathLeftDist = (double) lesserJO.get("lMeter");

                double percentComplete = (curLeftEncoderDist - lesserPathLeftDist) / (greaterLeftEncoderDist - lesserPathLeftDist);
                
                double finalVelocity = ((Number) greaterJO.get("velocity")).doubleValue();
                double finalAngularVelocity = ((Number) greaterJO.get("angularVelocity")).doubleValue();

                double initialVelocity = ((Number) lesserJO.get("velocity")).doubleValue();
                double initialAngularVelocity = ((Number) lesserJO.get("angularVelocity")).doubleValue();
                
                velocity = lerp(initialVelocity, finalVelocity, percentComplete);
                angularVelocity = lerp(initialAngularVelocity, finalAngularVelocity, percentComplete);

                driveSubsystem.velocityPID(velocity, angularVelocity);

                // Vector2D currentVelocity = new Vector2D(velocity, 0);
                //TODO: fixme, error
                //currentVelocity.rotateBy(BNO055.getInstance(opmode_t.OPERATION_MODE_GYRONLY, ));

                // velocity += getCorrectingPointAdd(new Vector2D(), new Vector2D(), currentVelocity, 0.3);

                System.out.format("Distance (l): %f\tDistance (r): %f\tVelocity: %f\tAngular velocity: %f\n", 
                    Math.round(greaterLeftEncoderDist * 1000.0) / 1000.0,
                    Math.round(greaterRightEncoderDist * 1000.0) / 1000.0,
                    Math.round(velocity * 1000.0) / 1000.0, 
                    Math.round(angularVelocity* 1000.0) / 1000.0
                );

                break;
            }
            
        }
        if(!foundStuff) rs.setState("disabled");
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
