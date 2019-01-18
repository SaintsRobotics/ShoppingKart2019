package com.saintsrobotics.swerveDrive.tasks.teleop;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.input.OI.XboxInput;
import com.saintsrobotics.swerveDrive.output.SwerveWheel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveControl extends RunEachFrameTask {
  private XboxInput xboxInput;
  private SwerveWheel w1;
  private SwerveWheel w2;
  private SwerveWheel w3;
  private SwerveWheel w4;
  double[] speedMultiplier = {0.25, 1};
  private int speedMultiplierPosition = 0;


  public SwerveControl(XboxInput xboxInput, SwerveWheel w1, SwerveWheel w2, SwerveWheel w3,
      SwerveWheel w4) {
    this.xboxInput = xboxInput;
    this.w1 = w1;
    this.w2 = w2;
    this.w3 = w3;
    this.w4 = w4;
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

     w1.setRotationHeadingAndVelocity(leftStickX, leftStickY, rightStickX);
     w2.setRotationHeadingAndVelocity(leftStickX, leftStickY, rightStickX);
     w3.setRotationHeadingAndVelocity(leftStickX, leftStickY, rightStickX);
     w4.setRotationHeadingAndVelocity(leftStickX, leftStickY, rightStickX);
     
  }
}
