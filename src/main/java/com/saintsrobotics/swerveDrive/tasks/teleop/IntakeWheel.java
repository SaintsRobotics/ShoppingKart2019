/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.saintsrobotics.swerveDrive.tasks.teleop;

import com.github.dozer.coroutine.helpers.RunContinuousTask;
import com.saintsrobotics.swerveDrive.Robot;

public class IntakeWheel extends RunContinuousTask {

	@Override
	protected void runForever() {
		wait.until(() -> Robot.instance.oi.xboxInput.RB() && Robot.instance.sensors.intake.get()
				&& !Robot.instance.oi.xboxInput.LB());
		Robot.instance.motors.intake.set(1);
		wait.until(() -> !Robot.instance.oi.xboxInput.RB() || !Robot.instance.sensors.intake.get());
		Robot.instance.motors.intake.stop();
	}

}