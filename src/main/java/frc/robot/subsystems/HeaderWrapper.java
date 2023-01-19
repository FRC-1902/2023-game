package frc.robot.subsystems;

public class HeaderWrapper {
  private IMUSubsystem wrappedSensor;
  private double offset;

  public HeaderWrapper(double offset) {
    wrappedSensor = IMUSubsystem.getInstance();
    this.offset = offset;
  }

  public double getHeading() {
    return wrappedSensor.getHeading() + offset;
  }

  public void setHeadingOffset(double offset) {
    this.offset = offset;
  }

  public void clearHeadingOffset() {
    offset = 0;
  }
}