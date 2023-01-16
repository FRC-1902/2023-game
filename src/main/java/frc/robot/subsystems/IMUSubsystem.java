package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.sensors.BNO055;


public class IMUSubsystem extends SubsystemBase {

    private static IMUSubsystem instance = new IMUSubsystem();

    private final BNO055 bno055 = BNO055.getInstance(BNO055.opmode_t.OPERATION_MODE_IMUPLUS, BNO055.vector_type_t.VECTOR_EULER);

    /**
     * @return imu x heading
     */
    public double getX() {
      double[] xyz = bno055.getVector();
      return xyz[0];
    }

    /**
     * @return imu y heading
     */
    public double getY(){
      double[] xyz = bno055.getVector();
      return xyz[1];
    }

    /**
     * @return imu z heading
     */
    public double getZ(){
      double[] xyz = bno055.getVector();
      return xyz[2];
    }

    /**
     * @return returns the imu's sensed heading
     */
    public double getHeading() {
      return bno055.getHeading();
    }
    
    /**
     * @param offset sets imu x heading offset
     */
    public void setOffset(double offset){
      bno055.headingOffset = offset;
    }

    /**
     * resets imu x heading to default offset
     */
    public void resetHeading(){
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
     *   mode the sensor is currently operating in. 
     */
    public boolean isCalibrated() {
        return bno055.isCalibrated();
    }

    /**
     * @reutrn imu instance
     */
    public static IMUSubsystem getInstance() {
      if (instance == null) {
        instance = new IMUSubsystem();
      }
      return instance;
    }
}