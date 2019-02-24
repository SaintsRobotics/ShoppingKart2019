/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.saintsrobotics.shoppingkart.tests;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.input.OI.XboxInput;
import com.github.dozer.output.Motor;

public class SimpleLiftTask extends RunEachFrameTask {
	private XboxInput xboxInput;
	private Motor lifterMotor;

	public SimpleLiftTask(XboxInput xboxInput, Motor lifterMotor) {
		this.xboxInput = xboxInput;
		this.lifterMotor = lifterMotor;
	}

	@Override
	protected void runEachFrame() {
		double speedMultiplier = .25;
		double movementAmount = this.xboxInput.rightTrigger() - this.xboxInput.leftTrigger();
		movementAmount *= speedMultiplier;
		this.lifterMotor.set(movementAmount);
	}
}