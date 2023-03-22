// package frc.robot.path;

// public class PathFollower {
//     boolean firstLoop = false;
//     startCheckFrame = 0;

//     public double getVelocity(){
//         if(firstLoop){
//             // timer.reset();
//             firstLoop = false;
//         }

//         JSONObject[] frames = Paths.getInstance().getJSONObjectArray();

//         if(frames.length <= 1) return 0.0;

//         double velocity = 0.0;
//         double angularVelocity = 0.0;
//         
//         // double currentTime = timer.get();

//         //find current position in path
//         if(startCheckFrame == frames.length - 1) {
//             rs.setState("autoBalance");
//         }
//         for(int i = startCheckFrame; i < frames.length; i++){

//             double previousTime = ((Number) frames[i-1].get("time")).doubleValue();
//             double nextTime = ((Number) frames[i].get("time")).doubleValue();
//             
//             System.out.format("%d | ", i);

//             if(nextTime > currentTime){
//                 //find forward velocity
//                 velocity = //((Number)frames[i].get("velocity")).doubleValue();
//                     lerp(
//                         ((Number) frames[i-1].get("velocity")).doubleValue(),
//                         ((Number) frames[i].get("velocity")).doubleValue(),
//                         (currentTime - previousTime)/(nextTime - previousTime)
//                     );

//                 //find angular velocity
//                 angularVelocity = 
//                     lerp(
//                         ((Number) frames[i-1].get("angularVelocity")).doubleValue(),
//                         ((Number) frames[i].get("angularVelocity")).doubleValue(),
//                         (currentTime - previousTime)/(nextTime - previousTime)
//                     );
//                 startCheckFrame = i;
//                 break;
//             }
//         }
//     }    


//     private double lerp(double initialValue, double finalValue, double t){
//         return initialValue + t * (finalValue - initialValue);
//     }
// }
