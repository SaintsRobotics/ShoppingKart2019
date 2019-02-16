/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.saintsrobotics.swerveDrive.util;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunContinuousTask;
import com.saintsrobotics.swerveDrive.Robot;

import edu.wpi.first.wpilibj.DriverStation;

public class ResetGyro extends RunContinuousTask {
	private BooleanSupplier trigger;

	public ResetGyro(BooleanSupplier trigger) {
		this.trigger = trigger;
	}

	@Override
	public void runForever() {
		while (true) {
			DriverStation.reportWarning("wait", false);
			wait.until(this.trigger);
			Robot.instance.sensors.gyro.reset();
			Robot.instance.swerveControl.setRobotTargetHead(0.0);
			DriverStation.reportWarning("gyro reset", false);
		}
	}
}