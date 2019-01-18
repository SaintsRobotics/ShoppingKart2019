 package com.saintsrobotics.swerveDrive.tasks.teleop;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.saintsrobotics.swerveDrive.Robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ArcadeDrive extends RunEachFrameTask {
  @Override
  public void runEachFrame() {
    
    double forward = -Robot.instance.oi.xboxInput.leftStickY() * (Robot.instance.oi.xboxInput.B()? 1 : 0.6);
    double turn = -Robot.instance.oi.xboxInput.rightStickX() * 0.75;
    Robot.instance.motors.leftDrive.set((forward - turn) );
    Robot.instance.motors.rightDrive.set((forward + turn));
  }
}
