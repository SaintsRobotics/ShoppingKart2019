package com.saintsrobotics.shoppingkart.config;

public class Settings {

    public double[] rightFrontLoc;
    public double[] leftFrontLoc;
    public double[] leftBackLoc;
    public double[] rightBackLoc;
    public double[] pivotLoc;



    public double liftCargo1;
    public double liftCargo2;
    public double liftCargo3;
    public double liftHatch1;
    public double liftHatch2;
    public double liftHatch3;
    public double liftHatch0;
    public double liftCargoShip;

    public double liftUpperThrottle;
    public double liftLowerThrottle;

    public double liftOffset;
    public double liftWait;

    public double kickerUpperbound;
    public double kickerLowerbound;
    public double kickerBackpass;

    public double cargoTranslationTarget;
    public double cargoDistanceTarget;
    public double hatchTranslationTarget;
    public double hatchDistanceTarget;

    public PidConfig wheelAnglePidConfig;
    public PidConfig headingPidConfig;
    public PidConfig liftPidConfig;
    public PidConfig dockCargoTranslation;
    public PidConfig dockCargoDistance;
    public PidConfig dockHatchTranslation;
    public PidConfig dockHatchDistance;

    public Settings(Config robotConfig) {
        this.rightFrontLoc = buildLoc(robotConfig, "settings.location.rightFront");
        this.leftFrontLoc = buildLoc(robotConfig, "settings.location.leftFront");
        this.leftBackLoc = buildLoc(robotConfig, "settings.location.leftBack");
        this.rightBackLoc = buildLoc(robotConfig, "settings.location.rightBack");
        this.pivotLoc = buildLoc(robotConfig, "settings.location.pivot");

        this.liftCargo1 = robotConfig.getDouble("settings.lift.cargo1");
        this.liftCargo2 = robotConfig.getDouble("settings.lift.cargo2");
        this.liftCargo3 = robotConfig.getDouble("settings.lift.cargo3");
        this.liftHatch1 = robotConfig.getDouble("settings.lift.hatch1");
        this.liftHatch2 = robotConfig.getDouble("settings.lift.hatch2");
        this.liftHatch3 = robotConfig.getDouble("settings.lift.hatch3");
        this.liftHatch0 = robotConfig.getDouble("settings.lift.hatch0");
        this.liftCargoShip = robotConfig.getDouble("settings.lift.cargoship");
        this.liftUpperThrottle = robotConfig.getDouble("settings.lift.upperthrottle");
        this.liftLowerThrottle = robotConfig.getDouble("settings.lift.lowerthrottle");

        this.liftOffset = robotConfig.getDouble("settings.lift.offset");
        this.liftWait = robotConfig.getDouble("settings.lift.waittime");

        this.kickerUpperbound = robotConfig.getDouble("settings.kicker.upperbound");
        this.kickerLowerbound = robotConfig.getDouble("settings.kicker.lowerbound");
        this.kickerBackpass = robotConfig.getDouble("settings.kicker.backpass");

        this.cargoTranslationTarget = robotConfig.getDouble("settings.dock.cargoTranslation");
        this.cargoDistanceTarget = robotConfig.getDouble("settings.dock.cargoDistance");
        this.hatchTranslationTarget = robotConfig.getDouble("settings.dock.hatchTranslation");
        this.hatchDistanceTarget = robotConfig.getDouble("settings.dock.hatchDistance");

        this.wheelAnglePidConfig = buildPidConfig(robotConfig, "settings.pids.wheelAngle");
        this.headingPidConfig = buildPidConfig(robotConfig, "settings.pids.heading");
        this.liftPidConfig = buildPidConfig(robotConfig, "settings.pids.lift");
        this.dockCargoTranslation = buildPidConfig(robotConfig, "settings.pids.dock.cargo.translation");
        this.dockCargoDistance = buildPidConfig(robotConfig, "settings.pids.dock.cargo.distance");
        this.dockHatchTranslation = buildPidConfig(robotConfig, "settings.pids.dock.hatch.translation");
        this.dockHatchDistance = buildPidConfig(robotConfig, "settings.pids.dock.hatch.distance");
    }

    private static double[] buildLoc(Config robotConfig, String keyPrefix) {
        return new double[] { robotConfig.getDouble(keyPrefix + ".x"), robotConfig.getDouble(keyPrefix + ".y") };
    }

    private static PidConfig buildPidConfig(Config robotConfig, String keyPrefix) {
        return new PidConfig(robotConfig.getDouble(keyPrefix + ".kP"), robotConfig.getDouble(keyPrefix + ".kI"),
                robotConfig.getDouble(keyPrefix + ".kD"), robotConfig.getDouble(keyPrefix + ".tolerance"));
    }
}
