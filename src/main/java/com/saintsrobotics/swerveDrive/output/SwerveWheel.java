package com.saintsrobotics.swerveDrive.output;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.output.Motor;
import com.saintsrobotics.swerveDrive.Robot;
import com.saintsrobotics.swerveDrive.input.AbsoluteEncoder;
import com.saintsrobotics.swerveDrive.util.TurnConfiguration;
import com.saintsrobotics.swerveDrive.util.PIDReceiver;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveWheel extends RunEachFrameTask {
  private Motor driveMotor;
  private Motor turnMotor;
  public double targetHead;
  public double targetVelocity;
  //x and y coordinates of wheel and location of pivot point on robot
  private double[] wheelLoc = new double[2];
  private double[] pivotLoc = new double[2];
  // distance of wheel from pivot
  private double radius;
  // right stick sensitivity
  private final static double TURN_GAIN = 0.25;
  // the maximum distance from one wheel to the pivot point
  private static double MAX_RADIUS;

  private PIDReceiver headingPidReceiver;
  private PIDController headingPidController;
  private String name;
  private PIDSource encoder;

  public SwerveWheel(String name, Motor driveMotor, Motor turnMotor, TurnConfiguration pidConfig,
      double[] wheelLoc, double[] pivotLoc) {
    this.name = name;
    this.driveMotor = driveMotor;
    this.turnMotor = turnMotor;
    this.encoder = pidConfig.encoder;

    this.wheelLoc = wheelLoc;
    this.pivotLoc = pivotLoc;

    this.radius = Math.sqrt(Math.pow((this.wheelLoc[0] - this.pivotLoc[0]), 2)
        + Math.pow((this.wheelLoc[1] - this.pivotLoc[1]), 2));

    this.headingPidReceiver = new PIDReceiver();
    this.headingPidController = new PIDController(pidConfig.forwardHeadingKP, pidConfig.forwardHeadingKI, pidConfig.forwardHeadingKD,
        pidConfig.encoder, headingPidReceiver);
    this.headingPidController.setAbsoluteTolerance(pidConfig.forwardHeadingTolerance);
    this.headingPidController.setOutputRange(-01, 01);
    this.headingPidController.setInputRange(0, 360);
    this.headingPidController.setContinuous();
    this.headingPidController.reset();
    this.headingPidController.enable();
  }

  public void setHeadAndVelocity(double targetHead, double targetVelocity) {
    double diff = 0.0;
    double currentHead = this.encoder.pidGet();
    if (Math.abs(targetHead - currentHead) > 180) {
      diff = 360 - Math.abs(targetHead - currentHead);
    } else {
      diff = Math.abs(targetHead - currentHead);
    }
    if (diff > 90) {
      targetHead += 180;
      targetHead %= 360;
      targetVelocity = -targetVelocity;
    }
    this.targetVelocity = targetVelocity;
    this.targetHead = targetHead;

    // SmartDashboard.putNumber(this.name + " targetHead", this.targetHead);
    // SmartDashboard.putNumber(this.name + " targetVelocity", this.targetVelocity);
  }

  public void setRotationHeadingAndVelocity(double leftStickX, double leftStickY,
      double rightStickX) {
    double[] rotationVector = new double[2];
    rotationVector[0] = (this.wheelLoc[1] - this.pivotLoc[1]) / this.radius;
    rotationVector[1] = (this.pivotLoc[0] - this.wheelLoc[0]) / this.radius;

    rotationVector[0] *= (TURN_GAIN / MAX_RADIUS) * this.radius * rightStickX;
    rotationVector[1] *= (TURN_GAIN / MAX_RADIUS) * this.radius * rightStickX;

    double[] totalVector = {leftStickX + rotationVector[0], leftStickY + rotationVector[1]};
    double[] polar = cartesianToPolar(totalVector);
    
    this.setHeadAndVelocity(polar[0], polar[1]);
  }

  private static double[] cartesianToPolar(double[] coords) {
    //returns in the format of {heading, velocity}
    double[] polar = new double[2];
    polar[0] = findAngle(coords[0], coords[1]);
    polar[1] = Math.sqrt(Math.pow(coords[0], 2) + Math.pow(coords[1], 2));
    return polar;
  }

  public static double findAngle(double x, double y) {
    // finds angle from y-axis to given coordinates
    if (y == 0 && x < 0)
      return 270.00;
    if (y == 0 && x > 0)
      return 90.00;

    if (y >= 0)
      return (360 + Math.toDegrees(Math.atan(x / y))) % 360;

    else
      return 180 + Math.toDegrees(Math.atan(x / y));
  }

  public double getRadius() {
    return this.radius;
  }

  public static void setMaxRadius(double newRadius) {
    SwerveWheel.MAX_RADIUS = newRadius;
  }

  public double getTurningEncoder() {
    return this.encoder.pidGet();
  }

  @Override
  public void runEachFrame() {
    this.driveMotor.set(this.targetVelocity);
    this.headingPidController.setSetpoint(this.targetHead);
    double headingOutput = this.headingPidReceiver.getOutput();
    this.turnMotor.set(headingOutput);
  }
}
