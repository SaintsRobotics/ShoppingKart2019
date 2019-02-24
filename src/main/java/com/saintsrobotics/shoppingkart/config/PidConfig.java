package com.saintsrobotics.shoppingkart.config;

public class PidConfig {
    public final double kP;
    public final double kI;
    public final double kD;
    public final double tolerance;

    public PidConfig(double kP, double kI, double kD, double tolerance) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.tolerance = tolerance;
    }
}