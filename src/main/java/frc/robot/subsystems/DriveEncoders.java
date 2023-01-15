package frc.robot.subsystems;

import com.revrobotics.RelativeEncoder;

public class DriveEncoders {
  public final RelativeEncoder leftEncoder1, leftEncoder2, rightEncoder1, rightEncoder2;

  public DriveEncoders(RelativeEncoder leftEncoder1, 
    RelativeEncoder leftEncoder2, 
    RelativeEncoder rightEncoder1, 
    RelativeEncoder rightEncoder2) 
  {
    this.leftEncoder1 = leftEncoder1;
    this.leftEncoder2 = leftEncoder2;
    this.rightEncoder1 = rightEncoder1;
    this.rightEncoder2 = rightEncoder2;
  }
}
