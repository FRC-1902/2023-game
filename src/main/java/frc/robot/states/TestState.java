package frc.robot.states;

import edu.wpi.first.util.concurrent.Event;
import edu.wpi.first.wpilibj.RobotState;
import frc.robot.RobotStateManager;
import frc.robot.State;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IMUSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.TurretvatorSubsystem;
import frc.robot.subsystems.IntakeSubsystem.DeployStage;

public class TestState implements State{
    private String name, parent;
    private TurretvatorSubsystem tvSub;
    private DriveSubsystem driveSub;
    private IntakeSubsystem intakeSub;
    private IMUSubsystem imu;
    private int stage;
    
    public TestState(String name, String parent){
        this.name = name;
        this.parent = parent;
        tvSub = TurretvatorSubsystem.getInstance();
        driveSub = DriveSubsystem.getInstance();
        intakeSub = IntakeSubsystem.getInstance();
        imu = IMUSubsystem.getInstance();
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
        intakeSub.deployIntake(DeployStage.STOW);
        stage = 0;
    }

    @Override
    public void Leave() {
        System.out.println("left " + name);
    }

    @Override
    public void Periodic(RobotStateManager rs) {
        switch(stage){
        case 0:
            if(intakeSub.isDeployed()){
                intakeSub.setLeverPos(50);
                stage ++;
            }else{break;}
        case 1:
            if(intakeSub.isLeverAtSetpoint()){
                intakeSub.deployIntake(DeployStage.DOWN);
                stage ++;
            }else{break;}
        case 2:
            if(intakeSub.isDeployed()){
                System.out.println("FINISHED");
            }
        }
        
        
        //intakeSub.deployIntake(DeployStage.STOW);
    }

    public void handleEvent(Event event, RobotStateManager rs){
        
    }
}
