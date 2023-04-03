package frc.robot;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;

public class PID implements Runnable {
    private double kP;
    private double p;
    private double kI;
    private double i;
    private double kD;
    private double d;
    private double kF;
    private double f;

    private Double setPoint;
    private DoubleSupplier getSensor;
    private double lastSensor;
    private long lastFrameTime;
    private double currentOutput;
    private boolean isRunning;
    private boolean isContinuous;
    private double continuousLowRange;
    private double continuousHighRange;
    private double tolerance;
    private int setpointCounter;

    private boolean isVelocity;
    private boolean isSetpointExplicitlyDeclared;

    private DoubleLogEntry pLogger, iLogger, dLogger, fLogger, currentSensorLogger, setPointLogger;
    private boolean isLogging;

    public void initializeLogger(String loggerName) {
        loggerName = "/" + loggerName;

        pLogger = new DoubleLogEntry(DataLogManager.getLog(), loggerName + "/p");
        iLogger = new DoubleLogEntry(DataLogManager.getLog(), loggerName + "/i");
        dLogger = new DoubleLogEntry(DataLogManager.getLog(), loggerName + "/d");
        fLogger = new DoubleLogEntry(DataLogManager.getLog(), loggerName + "/f");
        currentSensorLogger = new DoubleLogEntry(DataLogManager.getLog(), loggerName + "/input");
        setPointLogger = new DoubleLogEntry(DataLogManager.getLog(), loggerName + "/setPoint");

        pLogger.append(kP);
        iLogger.append(kI);
        dLogger.append(kD);
        fLogger.append(kF);
        setPointLogger.append(setPoint);

        isLogging = true;
    }

    public PID(DoubleSupplier doubleSupplier, double kP, double kI, double kD, double kF) {
        getSensor = doubleSupplier;
        setPoint = 0.0;
        lastSensor = 0.0;
        tolerance = 0.0;
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
        isRunning = false;
        isContinuous = false;
        setpointCounter = 0;
        isVelocity = false;
        isSetpointExplicitlyDeclared = false;
    }

    public PID(DoubleSupplier doubleSupplier, double kP, double kI, double kD, double kF, String loggerName) {
        this(doubleSupplier, kP, kI, kD, kF);
        initializeLogger(loggerName);
    }

    public PID(DoubleSupplier doubleSupplier, double kP, double kI, double kD, double kF, boolean isVelocity) {
        this(doubleSupplier, kP, kI, kD, kF);
        this.isVelocity = isVelocity;
    }

    public PID(DoubleSupplier doubleSupplier, double kP, double kI, double kD, double kF, boolean isVelocity, String loggerName) {
        this(doubleSupplier, kP, kI, kD, kF, isVelocity);
        initializeLogger(loggerName);
    }

    public void setP(double kP) {
        this.kP = kP;

        if (isLogging)
            pLogger.append(kP);
    }
    public void setI(double kI) {
        this.kI = kI;

        if (isLogging)
            iLogger.append(kI);
    }
    public void setD(double kD) {
        this.kD = kD;

        if (isLogging)
            dLogger.append(kD);
    }    
    public void setF(double kF) {
        this.kF = kF;

        if (isLogging)
            fLogger.append(kF);
    }

    public double getP(){
        return kP;
    }
    public double getI(){
        return kI;
    }
    public double getD(){
        return kD;
    }
    public double getF(){
        return kF;
    }

    public void enableContinuousInput(double lowRange, double highRange){
        isContinuous = true;
        continuousHighRange = lowRange;
        continuousLowRange = highRange;
    }
    public void disableContinuousInput(){
        isContinuous = false;
    }

    public void setTolerance(double tolerance){
        this.tolerance = tolerance;
    }

    public boolean atSetpoint(){
        return setpointCounter >= 5;
    }

    public void startThread() {
        Thread thread;
        if (!isRunning) {
            isRunning = true;
            i = 0;
            lastFrameTime = System.currentTimeMillis();
            if(!isSetpointExplicitlyDeclared){
                if(isVelocity){
                    setPoint = 0.0;
                }else{
                    setPoint = getSensor.getAsDouble();
                }
            }
            thread = new Thread(this);
            
            thread.start();
            DataLogManager.log("Starting PID Thread " + toString());
        }
    }

    public void stopThread() {
        isRunning = false;
        currentOutput = 0.0;
    }

    public void setSetpoint(double setPoint) {
        this.setPoint = setPoint;
        isSetpointExplicitlyDeclared = true;

        if (isLogging)
            setPointLogger.append(setPoint);
    }

    public double getSetpoint(){
        return setPoint;
    }

    public double getOutput() {
        return currentOutput;
    }

    public double getSensorInput(){
        return getSensor.getAsDouble();
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                double currentSensor = getSensor.getAsDouble();
                double error;

                if (isLogging)
                    currentSensorLogger.append(currentSensor);

                if(isContinuous){
                    double errorBound = (continuousHighRange - continuousLowRange) / 2;
                    error = MathUtil.inputModulus(setPoint - currentSensor, -errorBound, errorBound);
                }else{
                    error = setPoint - currentSensor;
                }

                if(Math.abs(error) < tolerance){
                    if(setpointCounter < 5){
                        setpointCounter++;
                    }
                    currentOutput = 0.0;
                }else{long currentTime = System.currentTimeMillis();
                    p = error;
                    i += p;
                    d = (currentSensor - lastSensor) / (currentTime - lastFrameTime);
                    f = setPoint;

                    setpointCounter = 0;
                    //clamp integral 
                    //XXX: hardcoded clamp, may need to change in the future if reused
                    i = Math.min(Math.max(i, -0.5), 0.5);
    
                    currentOutput = p * kP + i * kI - d * kD + f * kF;
                    lastFrameTime = System.currentTimeMillis();
                }
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                // Resture interrupted state
                Thread.currentThread().interrupt();
            }
        }

        currentOutput = 0.0;
        DataLogManager.log("PID Thread ending "  + toString());
    }
}
