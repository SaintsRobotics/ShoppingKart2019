package com.saintsrobotics.shoppingkart.config;

public class Settings {

    public double[] rightFrontLoc;
    public double[] leftFrontLoc;
    public double[] leftBackLoc;
    public double[] rightBackLoc;
    public double[] pivotLoc;

    public double armsHardstop;
    public double armsFullout;
    public double armsHatch;
    public double armsFullin;

    public double liftOffset;

    public double kickerUpperbound;
    public double kickerLowerbound;

    public PidConfig wheelAnglePidConfig;
    public PidConfig headingPidConfig;
    public PidConfig liftPidConfig;
    public PidConfig armsPidConfig;
    public PidConfig dockPidConfig;

    public Settings(Config robotConfig) {
        this.rightFrontLoc = buildLoc(robotConfig, "settings.location.rightFront");
        this.leftFrontLoc = buildLoc(robotConfig, "settings.location.leftFront");
        this.leftBackLoc = buildLoc(robotConfig, "settings.location.leftBack");
        this.rightBackLoc = buildLoc(robotConfig, "settings.location.rightBack");
        this.pivotLoc = buildLoc(robotConfig, "settings.location.pivot");

        this.armsHardstop = robotConfig.getDouble("settings.arms.hardstop");
        this.armsFullout = robotConfig.getDouble("settings.arms.fullout");
        this.armsHatch = robotConfig.getDouble("settings.arms.hatch");
        this.armsFullin = robotConfig.getDouble("settings.arms.fullin");

        this.liftOffset = robotConfig.getDouble("settings.lift.offset");

        this.kickerUpperbound = robotConfig.getDouble("settings.kicker.upperbound");
        this.kickerLowerbound = robotConfig.getDouble("settings.kicker.lowerbound");

        this.wheelAnglePidConfig = buildPidConfig(robotConfig, "settings.pids.wheelAngle");
        this.headingPidConfig = buildPidConfig(robotConfig, "settings.pids.heading");
        this.liftPidConfig = buildPidConfig(robotConfig, "settings.pids.lift");
        this.armsPidConfig = buildPidConfig(robotConfig, "settings.pids.arms");
        this.dockPidConfig = buildPidConfig(robotConfig, "settings.pids.dock");
    }

    private static double[] buildLoc(Config robotConfig, String keyPrefix) {
        return new double[] { robotConfig.getDouble(keyPrefix + ".x"), robotConfig.getDouble(keyPrefix + ".y") };
    }

    private static PidConfig buildPidConfig(Config robotConfig, String keyPrefix) {
        return new PidConfig(robotConfig.getDouble(keyPrefix + ".kP"), robotConfig.getDouble(keyPrefix + ".kI"),
                robotConfig.getDouble(keyPrefix + ".kD"), robotConfig.getDouble(keyPrefix + ".tolerance"));
    }
}
