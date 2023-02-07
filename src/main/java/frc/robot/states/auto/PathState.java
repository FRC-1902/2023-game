package frc.robot.states.auto;

import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.path.Paths;
import frc.robot.subsystems.DriveSubsystem;

import org.json.simple.JSONObject;


public class PathState implements State{
    private String name, parent;

    DriveSubsystem driveSubsystem;

    double beganAvgDist, beganLeftDist;
    int currentFrame;
    
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
        System.out.println("entered " + name);

        driveSubsystem = DriveSubsystem.getInstance();

        beganLeftDist = driveSubsystem.leftEncoder.getDistance();
        beganAvgDist = (driveSubsystem.leftEncoder.getDistance() + driveSubsystem.rightEncoder.getDistance())/2;
        currentFrame = 0;
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
        DriveSubsystem.getInstance().velocityPID(0, 0);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        Object[] objects = Paths.getInstance().getPathArray();

        if(currentFrame >= objects.length){
            driveSubsystem.velocityPID(0, 0);
            return;
        }

        double velocity = 0.0;
        double angularVelocity = 0.0;

        JSONObject greaterJO = (JSONObject) objects[currentFrame + 1];
        JSONObject lesserJO = (JSONObject) objects[currentFrame];
        
        double greaterLeftEncoderDist = (double) greaterJO.get("lMeter");

        double curLeftEncoderDist = driveSubsystem.leftEncoder.getDistance();
        
    //TODO: fixme, what happends when greaterLeftEncoderDist or curLeftEncoderDist are negative
        if((double) ((JSONObject)objects[objects.length - 1]).get("lMeter") <= curLeftEncoderDist - beganLeftDist){
            driveSubsystem.velocityPID(0, 0);
            currentFrame = objects.length;
            System.out.println("SURPASSED FINAL LENGTH PREMATURELY");
        }

        if(greaterLeftEncoderDist > curLeftEncoderDist - beganLeftDist){
            double lesserPathLeftDist = ((Number)lesserJO.get("lMeter")).doubleValue();

            double percentComplete = (curLeftEncoderDist - lesserPathLeftDist) / (greaterLeftEncoderDist - lesserPathLeftDist);
            
            double finalVelocity = ((Number) greaterJO.get("velocity")).doubleValue() /3; //TODO: fixme, testing
            double finalAngularVelocity = ((Number) greaterJO.get("angularVelocity")).doubleValue();

            double initialVelocity = ((Number) lesserJO.get("velocity")).doubleValue() /3;
            double initialAngularVelocity = ((Number) lesserJO.get("angularVelocity")).doubleValue();
            
            velocity = lerp(initialVelocity, finalVelocity, percentComplete);
            angularVelocity = lerp(initialAngularVelocity, finalAngularVelocity, percentComplete);

            driveSubsystem.velocityPID(finalVelocity, finalAngularVelocity);
        }

        //Frame change
        while(greaterLeftEncoderDist <= driveSubsystem.leftEncoder.getDistance() - beganLeftDist){
            if(greaterLeftEncoderDist > driveSubsystem.leftEncoder.getDistance() - beganLeftDist){break;}
            if(currentFrame >= objects.length){return;}
            currentFrame ++;
            greaterJO = (JSONObject) objects[currentFrame + 1];
            greaterLeftEncoderDist = (double) greaterJO.get("lMeter");
        }

        //debug prints
        System.out.format("Distance (l): %f\tDistance Traveled (l): %f\tFinal Velocity: %f\tCurrent Frame: %d\tCurrent Rate: %f\n", 
            Math.round(greaterLeftEncoderDist * 1000.0) / 1000.0,
            Math.round(curLeftEncoderDist * 1000.0) / 1000.0,
            Math.round(((Number) greaterJO.get("velocity")).doubleValue() * 1000.0) / 1000.0, 
            currentFrame,
            Math.round(driveSubsystem.leftEncoder.getRate() * 1000.0) / 1000.0
        );
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
