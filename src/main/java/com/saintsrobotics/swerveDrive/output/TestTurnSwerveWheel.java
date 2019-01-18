//this class is for the purposes of testing and debugging

package com.saintsrobotics.swerveDrive.output;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.input.OI.XboxInput;
import com.github.dozer.output.Motor;
import com.saintsrobotics.swerveDrive.input.AbsoluteEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TestTurnSwerveWheel extends RunEachFrameTask{
  XboxInput xb;
  Motor turner;
  AbsoluteEncoder encoder;

  public TestTurnSwerveWheel(XboxInput xb, Motor turner, AbsoluteEncoder encoder) {
    this.xb = xb;
    this.turner = turner;
    this.encoder = encoder;
  }
  
  
  @Override
  protected void runEachFrame() {
    double xAxis = xb.leftStickX() * 0.25;
    this.turner.set(xAxis);
    SmartDashboard.putNumber("encoder ", this.encoder.getRotation());

  }

}
