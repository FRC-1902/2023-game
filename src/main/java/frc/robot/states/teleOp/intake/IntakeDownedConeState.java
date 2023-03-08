// package frc.robot.states.teleOp.intake;

// import frc.robot.Event;
// import frc.robot.RobotStateManager;
// import frc.robot.Controllers.Action;
// import frc.robot.subsystems.IntakeSubsystem;

// public class IntakeDownedConeState implements frc.robot.State{
//   private String name, parent;
//   private IntakeSubsystem intakeSub;
//   //TODO: write me
//   public IntakeDownedConeState(String name, String parent){
//     this.name = name;
//     this.parent = parent;
//     intakeSub = IntakeSubsystem.getInstance();
//   }

//   @Override
//   public String getName() {
//     return name;
//   }

//   @Override
//   public String getParent() {
//     return parent;
//   }

//   @Override
//   public void Enter() {
//     System.out.println("entered " + name);
//   }

//   @Override
//   public void Leave() {
//     System.out.println("left " + name);
//   }

//   @Override
//   public void Periodic(RobotStateManager rs) {

//   }
  
//   @Override
//   public boolean handleEvent(Event event, RobotStateManager rs) {
//     switch(event.controllerName){
//     //Manip Controller
//     case MANIP:
//       switch(event.button){
//     //back
//       case B:
//         if(event.action == Action.RELEASED){
//           intakeSub.setRollerPow(0);
//           rs.setState(parent);
//           return true;
//         }
//         break;
//     //load piece
//       case RB:
//         if(event.action == Action.PRESSED){
//           rs.setState("loadPiece");
//           return true;
//         }
//         break;
//       default:
//       }
//     default:
//     }
//     return false;
//   }
// }
