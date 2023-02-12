package frc.robot.subsystems;

public class HeaderWrapper {
  private IMUSubsystem wrappedSensor;
  private double offset, turnOffset;

  public HeaderWrapper() {
    this(0);
  }

  public HeaderWrapper(double offset) {
    wrappedSensor = IMUSubsystem.getInstance();
    turnOffset = wrappedSensor.getTurns() * 360;
    this.offset = offset;
  }

  public double getHeading() {
    return wrappedSensor.getHeading() - offset - turnOffset;
  }

  public void setHeadingOffset(double offset) {
    this.offset = offset;
  }

  public void clearHeadingOffset() {
    offset = 0;
  }
}
