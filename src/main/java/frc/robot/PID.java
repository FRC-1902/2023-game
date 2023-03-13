package frc.robot;

import java.util.function.DoubleSupplier;

import javax.swing.InternalFrameFocusTraversalPolicy;

import edu.wpi.first.math.MathUtil;

public class PID implements Runnable {
    private Thread thread;
    private double kP;
    private double P;
    private double kI;
    private double I;
    private double kD;
    private double D;
    private double kF;
    private double F;

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
    private boolean isAtSetpoint;

    private boolean isVelocity;

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
        isAtSetpoint = false;
        isVelocity = false;
    }

    public PID(DoubleSupplier doubleSupplier, double kP, double kI, double kD, double kF, boolean isVelocity) {
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
        isAtSetpoint = false;
        this.isVelocity = isVelocity;
    }

    public void setP(double kP) {
        this.kP = kP;
    }
    public void setI(double kI) {
        this.kI = kI;
    }
    public void setD(double kD) {
        this.kD = kD;
    }    
    public void setF(double kF) {
        this.kF = kF;
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
        return isAtSetpoint;
    }

    public void startThread() {
        if (!isRunning) {
            thread = new Thread(this);
            isRunning = true;
            I = 0;
            lastFrameTime = System.currentTimeMillis();

            if(isVelocity){
                setPoint = 0.0;
            }else{
                setPoint = getSensor.getAsDouble();
            }
        
            thread.start();
            System.out.println("Starting Thread");
        }
    }

    public void stopThread() {
        isRunning = false;
        currentOutput = 0.0;
    }

    public void setSetpoint(double setPoint) {
        this.setPoint = setPoint;
    }

    public double getSetpoint(){
        return setPoint;
    }

    public double getOutput() {
        return currentOutput;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                double currentSensor = getSensor.getAsDouble();
                double error;
                if(isContinuous){
                    double errorBound = (continuousHighRange - continuousLowRange) / 2;
                    error = MathUtil.inputModulus(setPoint - currentSensor, -errorBound, errorBound);
                }else{
                    error = setPoint - currentSensor;
                }

                if(Math.abs(error) < tolerance){
                    isAtSetpoint = true;
                    currentOutput = 0.0;
                }else{long currentTime = System.currentTimeMillis();
                    P = error;
                    I += P;
                    D = (currentSensor - lastSensor) / (currentTime - lastFrameTime);
                    F = setPoint;

                    //clamp integral 
                    //XXX: hardcoded clamp, may need to change in the future if reused
                    I = Math.min(Math.max(I, -0.5), 0.5);
    
                    currentOutput = P * kP + I * kI - D * kD + F * kF;
                    lastFrameTime = System.currentTimeMillis();
                }
                //System.out.printf("P: %.02f | I: %.02f | D: %.02f | Out: %.02f \n", P, I, D, currentOutput);
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        currentOutput = 0.0;
        System.out.println("Thread ending");
    }
}