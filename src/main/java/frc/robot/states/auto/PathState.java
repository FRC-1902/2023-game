package frc.robot.states.auto;

import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.path.Paths;
import frc.robot.subsystems.DriveSubsystem;

import org.json.simple.JSONObject;

import edu.wpi.first.wpilibj.Timer;


public class PathState implements State{
    private String name, parent;

    DriveSubsystem driveSubsystem;

    double beganAvgDist, beganLeftDist;
    int currentFrame;
    Timer timer = new Timer();

    int startCheckFrame = 1;

    boolean firstLoop = true;
    
    public PathState(String name, String parent){
        this.name = name;
        this.parent = parent;
        timer.start();
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

        driveSubsystem.setPIDEnable(true);

        startCheckFrame = 1;
        
        firstLoop = true;
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
        driveSubsystem.setPIDEnable(false);
        DriveSubsystem.getInstance().velocityPID(0, 0);
    }

    @Override
    public void Periodic(RobotStateManager rs) {

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
            rs.setState("autoBalance");
        }
        for(int i = startCheckFrame; i < frames.length; i++){

            double previousTime = ((Number) frames[i-1].get("time")).doubleValue();
            double nextTime = ((Number) frames[i].get("time")).doubleValue();
            
            System.out.format("%d | ", i);

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
        

        //find difference from current encoder tics to target encoder tics


        //run pid on difference to find fix velocity


        //add velocity to right and left motors

        driveSubsystem.velocityPID(velocity, angularVelocity);


        System.out.format("Velocity: %.3f | Angular: %.3f | Left Encoder Rate: %.3f\n", velocity, angularVelocity, driveSubsystem.leftEncoder.getRate());

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
    private double lerp(double initialValue, double finalValue, double t){
        return initialValue + t * (finalValue - initialValue);
    }


    

    // private 

    // private Pose2d findTargetVelocity(Translation2d robotToCurrentPointOnPath, Translation2d currentVelocityOfPointOnPath) {
    //     Translation2d targetVelocity = robotToCurrentPointOnPath.plus(currentVelocityOfPointOnPath);
        
    //     Rotation2d rotation = new Rotation2d(targetVelocity.getX(), targetVelocity.getY());
        
    //     return new Pose2d(1, 0 , rotation);
    // }
    
    
}

/*
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
*/
