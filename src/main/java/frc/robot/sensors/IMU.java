package frc.robot.sensors;

import java.util.Map;

import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import frc.robot.Constants;

public class IMU {

  private static IMU instance = new IMU();

  private final BNO055 bno055 = BNO055.getInstance(BNO055.opmode_t.OPERATION_MODE_IMUPLUS,
      BNO055.vector_type_t.VECTOR_EULER);

  private DoubleLogEntry headingLogger, rollLogger, pitchLogger, yawLogger, turnLogger, offsetLogger;

  private void initializeLogger() {
    headingLogger = new DoubleLogEntry(DataLogManager.getLog(), "/IMU/heading");
    rollLogger = new DoubleLogEntry(DataLogManager.getLog(), "/IMU/roll");
    pitchLogger = new DoubleLogEntry(DataLogManager.getLog(), "/IMU/pitch");
    turnLogger = new DoubleLogEntry(DataLogManager.getLog(), "/IMU/turn");
    offsetLogger = new DoubleLogEntry(DataLogManager.getLog(), "/IMU/offset");

    offsetLogger.append(bno055.headingOffset);
  }

  public IMU() {
    ShuffleboardLayout dashboardTab = Shuffleboard.getTab(Constants.MAIN_SHUFFLEBOARD_TAB)
        .getLayout("BNO055 Telemetry", BuiltInLayouts.kList)
        .withSize(3, 3);

    dashboardTab.addDouble("BNO055 Yaw", this::getHeading)
        .withProperties(Map.of("Min", -180, "Max", 180))
        .withWidget(BuiltInWidgets.kNumberBar);

    dashboardTab.addDouble("BNO055 Roll", this::getRoll)
        .withProperties(Map.of("Min", -90, "Max", 90))
        .withWidget(BuiltInWidgets.kNumberBar);

    dashboardTab.addDouble("BNO055 Pitch", this::getPitch)
        .withProperties(Map.of("Min", -180, "Max", 180))
        .withWidget(BuiltInWidgets.kNumberBar);

      initializeLogger();
  }

  public void logPeriodic() {
    headingLogger.append(getHeading());
    rollLogger.append(getRoll());
    pitchLogger.append(getPitch());
    turnLogger.append(getTurns());
  }

  /**
   * @return returns the imu's x scalar (heading/yaw) representing an angle from 0
   *         to 360 degrees
   */
  public double getHeading() {
    double[] xyz = bno055.getVector();
    return xyz[0];
  }

  /**
   * @return returns the imu's y scalar (roll) representing an angle from -90 to
   *         90 degrees
   */
  public double getRoll() {
    double[] xyz = bno055.getVector();
    return xyz[1];
  }

  /**
   * @return returns the imu's z scalar (pitch) representing an angle from -180 to
   *         180 degrees
   */
  public double getPitch() {
    double[] xyz = bno055.getVector();
    return xyz[2];
  }

  /**
   * @return the signed sum of the amount of full rotations the BNO has taken
   */
  public long getTurns() {
    return bno055.getTurns();
  }

  /**
   * @param offset sets imu x heading offset
   */
  public void setOffset(double offset) {
    bno055.headingOffset = offset;
    offsetLogger.append(offset);
  }

  /**
   * resets imu x heading to default offset
   */
  public void resetHeading() {
    bno055.resetHeading();
  }

  /**
   * @return true if the sensor is found on the I2C bus
   */
  public boolean isSensorPresent() {
    return bno055.isSensorPresent();
  }

  /**
   * @return true when the sensor is initialized.
   */
  public boolean isInitialized() {
    return bno055.isInitialized();
  }

  /**
   * @return true if calibration is complete for all sensors required for the
   *         mode the sensor is currently operating in.
   */
  public boolean isCalibrated() {
    return bno055.isCalibrated();
  }

  /**
   * @reutrn imu instance
   */
  public static IMU getInstance() {
    if (instance == null) {
      instance = new IMU();
    }
    return instance;
  }
}