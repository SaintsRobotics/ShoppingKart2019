/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.saintsrobotics.shoppingkart.tests;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.saintsrobotics.shoppingkart.Robot;

public class SimpleLiftTask extends RunEachFrameTask {

	@Override
	protected void runEachFrame() {
		double speedMultiplier = .25;
		double movementAmount = Robot.instance.oi.xboxInput.rightTrigger() - Robot.instance.oi.xboxInput.leftTrigger();
		movementAmount *= speedMultiplier;
		Robot.instance.motors.lifter.set(movementAmount);
	}
}