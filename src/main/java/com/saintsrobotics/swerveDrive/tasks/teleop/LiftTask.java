/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.saintsrobotics.swerveDrive.tasks.teleop;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.saintsrobotics.swerveDrive.Robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftTask extends RunEachFrameTask {

	@Override
	protected void runEachFrame() {
		Robot.instance.flags.liftEncoderValue = Robot.instance.sensors.liftEncoder.getDistance();
		double movementAmount = Robot.instance.oi.xboxInput.rightTrigger() - Robot.instance.oi.xboxInput.leftTrigger();
		SmartDashboard.putNumber("LIFT", Robot.instance.sensors.liftEncoder.getDistance());
		double speed = 0.3;
		if (Robot.instance.sensors.liftEncoder.getDistance() > 2 && movementAmount > 0.3) {
			movementAmount = 0.2;
		}
		if (Robot.instance.sensors.liftEncoder.getDistance() < 0.3 && movementAmount < -0.3) {
			movementAmount = -0.3;
		}
		if (!Robot.instance.sensors.lifterUp.get() && movementAmount > 0) {
			movementAmount = 0;
			Robot.instance.motors.lifter.stop();
		}
		if (!Robot.instance.sensors.lifterDown.get() && movementAmount < 0) {
			movementAmount = 0;
			Robot.instance.sensors.liftEncoder.reset();
			Robot.instance.motors.lifter.stop();
		}
		Robot.instance.motors.lifter.set(movementAmount + 0.05);
	}
}