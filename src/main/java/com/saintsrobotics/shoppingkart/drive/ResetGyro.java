/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.saintsrobotics.shoppingkart.drive;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunContinuousTask;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;

public class ResetGyro extends RunContinuousTask {
	private BooleanSupplier trigger;
	private ADXRS450_Gyro gyro;
	private SwerveControl swerveControl;

	public ResetGyro(BooleanSupplier trigger, ADXRS450_Gyro gyro, SwerveControl swerveControl) {
		this.trigger = trigger;
		this.gyro = gyro;
		this.swerveControl = swerveControl;
	}

	@Override
	public void runForever() {
		while (true) {
			wait.until(this.trigger);
			this.gyro.reset();
			this.swerveControl.setRobotTargetHead(0.0);
		}
	}
}