// package frc.robot.states.teleOp.intake;

// import frc.robot.Event;
// import frc.robot.RobotStateManager;
// import frc.robot.subsystems.IntakeSubsystem;
// import frc.robot.subsystems.TurretvatorSubsystem;
// import frc.robot.subsystems.IntakeSubsystem.DeployStage;
// import frc.robot.subsystems.TurretvatorSubsystem.ElevatorStage;

// public class LoadPieceState implements frc.robot.State{
//   private String name, parent;
//   private int stage;
//   private IntakeSubsystem intakeSub;
//   private TurretvatorSubsystem tvSub;
//   //TODO: write me
//   public LoadPieceState(String name, String parent){
//     this.name = name;
//     this.parent = parent;
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
//     stage = 0;
//     //TODO: remove me when ready for testing
//     System.out.println("test and set intake and turretvator code first");
//     RobotStateManager.getInstance().setState(parent);
//     return;
//     //restore me
//     //tvSub.setGripper(false);
//     //intakeSub.deployIntake(DeployStage.LOAD);
    
//   }

//   @Override
//   public void Leave() {
//     System.out.println("left " + name);
//   }

//   @Override
//   public void Periodic(RobotStateManager rs) {
//     //restore me
//     /*
//     switch(stage){
//       case 0:
//         if(intakeSub.isDeployed()){
//           tvSub.elevatorSet(ElevatorStage.LOAD);
//           stage ++;
//         }else{break;}
//       case 1:
//         if(tvSub.isExtended()){
//           tvSub.setGripper(true);
//           //intakeSub.setLeverPos();//TODO: set me
//           intakeSub.deployIntake(DeployStage.LOADDOWN);
//           stage ++;
//         }else{
//           break;
//         }
//       case 2:
//         if(intakeSub.isDeployed()){
//           tvSub.elevatorSet(ElevatorStage.DOWN);
//         }else{
//           break;
//         }
//       case 3:
//         if(tvSub.isExtended()){
//           intakeSub.deployIntake(DeployStage.STOW);
//           stage ++;
//         }else{
//           break;
//         }
//       case 4:
//         if(intakeSub.isDeployed()){
//           rs.setState("centerTurret");
//         }
//     }
//     */
//   }
  
//   @Override
//   public boolean handleEvent(Event event, RobotStateManager rs) {
//     switch(event.controllerName){
//     //Manip Controller
//     case MANIP:
//       switch(event.button){
//       default:
//       }
//     default:
//     }
//     return false;
//   }
// }
