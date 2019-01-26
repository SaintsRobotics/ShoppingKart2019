package com.saintsrobotics.swerveDrive.tasks.teleop;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.input.OI.XboxInput;
import com.saintsrobotics.swerveDrive.output.SwerveWheel;
import com.saintsrobotics.swerveDrive.input.Sensors;
import com.saintsrobotics.swerveDrive.input.TestSensors;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import com.saintsrobotics.swerveDrive.util.PIDReceiver;

import edu.wpi.first.wpilibj.Timer;

public class SwerveControl extends RunEachFrameTask {
  private XboxInput xboxInput;
  private SwerveWheel w1;
  private SwerveWheel w2;
  private SwerveWheel w3;
  private SwerveWheel w4;
  double speedMultiplier = 0.5;
  private double robotTargetHead;
  private double time;

  private ADXRS450_Gyro gyro;

  private PIDReceiver headingPidReceiver;
  private PIDController headingPidController;
  private PIDSource encoder;

  private boolean isTurning;


  public SwerveControl(XboxInput xboxInput, SwerveWheel w1, SwerveWheel w2, SwerveWheel w3,
      SwerveWheel w4, ADXRS450_Gyro gyro) {
    this.xboxInput = xboxInput;
    this.w1 = w1;
    this.w2 = w2;
    this.w3 = w3;
    this.w4 = w4;

    double maxRad = Math.max(Math.max(Math.max(w1.getRadius(), w2.getRadius()), w3.getRadius()),
    w4.getRadius());
    SwerveWheel.setMaxRadius(maxRad);

    this.gyro = gyro;
    // this.gyro.calibrate();
    // this.gyro.reset();

    this.headingPidReceiver = new PIDReceiver();
    this.headingPidController = new PIDController(0.1, 0.0, 0.0,
        this.gyro, headingPidReceiver);
    this.headingPidController.setAbsoluteTolerance(2.0);
    this.headingPidController.setOutputRange(-1, 1);
    this.headingPidController.setInputRange(0, 360);
    this.headingPidController.setContinuous();
    this.headingPidController.reset();
    this.headingPidController.enable();
    this.robotTargetHead = 0;

    time = Timer.getFPGATimestamp();
  }

  public void setRobotTargetHead(double n) {
    this.headingPidController.setSetpoint(n);
  }

  @Override
  public void runEachFrame() {
    double now = Timer.getFPGATimestamp();
    SmartDashboard.putNumber("elapsed time", now-time);
    time = now;

    
    double leftStickX = xboxInput.leftStickX();
    double leftStickY = -xboxInput.leftStickY();
    double rightStickX = xboxInput.rightStickX();
    
    leftStickX *= this.speedMultiplier;
    leftStickY *= this.speedMultiplier;

    SmartDashboard.putNumber("rightStickX", rightStickX);
    if (Math.abs(rightStickX) <= 0.15) {
      rightStickX = 0;
    }

    //Gyro coords are continuous so this restricts it to 360
    double currentHead = ((this.gyro.getAngle() % 360) + 360) % 360;

    //this.robotTargetHead = SwerveWheel.findAngle(rightStickX, rightStickY);
    double rotationVector = this.headingPidReceiver.getOutput();
    if (rightStickX != 0.0) {
      rotationVector = rightStickX;
      this.isTurning = true;
    }
    else if (rightStickX == 0.0 && this.isTurning) {
      this.headingPidController.setSetpoint(currentHead);
      this.isTurning = false;
    }
    
    // this.robotTargetHead += rightStickX * 0.5;
    // this.robotTargetHead = ((this.robotTargetHead % 360) + 360) % 360;
    // SmartDashboard.putNumber("robotTargetHead ", this.robotTargetHead);
    // SmartDashboard.putNumber("rightStickX", rightStickX);
    // this.headingPidController.setSetpoint(this.robotTargetHead);

    //Absolute control
    if(this.xboxInput.RB()) {
     //Gyro coords are continous so this restricts it to 360 degrees
     double robotAngle = ((this.gyro.getAngle() % 360) + 360) % 360;

     //Temporary save of x and y pre-translation
     double tempX = leftStickX;
     double tempY = leftStickY;

     //Overwriting x and y
     leftStickX = (tempX * Math.cos(Math.toRadians(robotAngle))) - (tempY * Math.sin(Math.toRadians(robotAngle)));
     leftStickY = (tempX * Math.sin(Math.toRadians(robotAngle))) + (tempY * Math.cos(Math.toRadians(robotAngle)));
    }

     w1.setRotationHeadingAndVelocity(leftStickX, leftStickY, rotationVector);
     w2.setRotationHeadingAndVelocity(leftStickX, leftStickY, rotationVector);
     w3.setRotationHeadingAndVelocity(leftStickX, leftStickY, rotationVector);
     w4.setRotationHeadingAndVelocity(leftStickX, leftStickY, rotationVector);
     
     SmartDashboard.putNumber("gyro ", ((this.gyro.getAngle() % 360) + 360) % 360);
     SmartDashboard.putNumber("error ", this.headingPidController.getError());
     SmartDashboard.putNumber("output ", this.headingPidReceiver.getOutput());
  }
}
