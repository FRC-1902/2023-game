package frc.robot;

import java.util.function.DoubleSupplier;

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

    public PID(DoubleSupplier doubleSupplier, double kP, double kI, double kD, double kF) {
        getSensor = doubleSupplier;
        setPoint = 0.0;
        lastSensor = 0.0;
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
        isRunning = false;
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

    public void startThread() {
        if (!isRunning) {
            thread = new Thread();
            isRunning = true;
            I = 0;
            lastFrameTime = System.currentTimeMillis();
        
            thread.start();
        }
    }

    public void stopThread() {
        isRunning = false;
        currentOutput = 0.0;
    }

    public void setSetpoint(double setPoint) {
        this.setPoint = setPoint;
    }

    public double getOutput() {
        return currentOutput;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                double currentSensor = getSensor.getAsDouble();
                long currentTime = System.currentTimeMillis();
                P = setPoint - currentSensor;
                I += P;
                D = (currentSensor - lastSensor) / (currentTime - lastFrameTime);
                F = setPoint;

              currentOutput = P * kP + I * kI - D * kD + F * kF;
                lastFrameTime = System.currentTimeMillis();

                System.out.printf("P: %.02f | I: %.02f | D: %.02f | Out: %.02f \n", P, I, D, currentOutput);
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        currentOutput = 0.0;
    }
}
