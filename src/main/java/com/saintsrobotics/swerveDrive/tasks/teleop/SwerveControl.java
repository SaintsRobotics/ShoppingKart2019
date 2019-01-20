package com.saintsrobotics.swerveDrive.tasks.teleop;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.input.OI.XboxInput;
import com.saintsrobotics.swerveDrive.output.SwerveWheel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

public class SwerveControl extends RunEachFrameTask {
  private XboxInput xboxInput;
  private SwerveWheel w1;
  private SwerveWheel w2;
  private SwerveWheel w3;
  private SwerveWheel w4;
  double[] speedMultiplier = {0.25, 1};
  private int speedMultiplierPosition = 0;

  private ADXRS450_Gyro gyro;


  public SwerveControl(XboxInput xboxInput, SwerveWheel w1, SwerveWheel w2, SwerveWheel w3,
      SwerveWheel w4, ADXRS450_Gyro gyro) {
    this.xboxInput = xboxInput;
    this.w1 = w1;
    this.w2 = w2;
    this.w3 = w3;
    this.w4 = w4;
    this.gyro = gyro;
  }

  @Override
  public void runEachFrame() {
    if (xboxInput.A()) {
      this.speedMultiplierPosition += 1;
      this.speedMultiplierPosition %= speedMultiplier.length;
    }

    double leftStickX = xboxInput.leftStickX();
    double leftStickY = -xboxInput.leftStickY();
    double rightStickX = xboxInput.rightStickX();
//    double xAxis = xboxInput.DPAD_LEFT() ? -1 : xboxInput.DPAD_RIGHT() ? 1 : 0;
//    double yAxis = xboxInput.DPAD_UP() ? 1 : xboxInput.DPAD_DOWN() ? -1 : 0;

    leftStickX *= this.speedMultiplier[this.speedMultiplierPosition];
    leftStickY *= this.speedMultiplier[this.speedMultiplierPosition];

    double maxRad = Math.max(Math.max(Math.max(w1.getRadius(), w2.getRadius()), w3.getRadius()),
        w4.getRadius());
    SwerveWheel.setMaxRadius(maxRad);

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

     w1.setRotationHeadingAndVelocity(leftStickX, leftStickY, rightStickX);
     w2.setRotationHeadingAndVelocity(leftStickX, leftStickY, rightStickX);
     w3.setRotationHeadingAndVelocity(leftStickX, leftStickY, rightStickX);
     w4.setRotationHeadingAndVelocity(leftStickX, leftStickY, rightStickX);
     
  }
}
