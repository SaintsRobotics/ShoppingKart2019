package com.saintsrobotics.shoppingkart.util;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.saintsrobotics.shoppingkart.util.Motors;

public class UpdateMotors extends RunEachFrameTask {
	private Motors motors;

	public UpdateMotors(Motors motors) {
		this.motors = motors;
	}

	@Override
	protected void runEachFrame() {
		motors.update();
	}
}
