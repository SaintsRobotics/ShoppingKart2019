package com.saintsrobotics.shoppingkart.config;

import com.github.dozer.output.Motor;
import com.saintsrobotics.shoppingkart.util.MotorRamping;
import com.saintsrobotics.shoppingkart.util.Motors;

public abstract class RobotMotors extends Motors {
	public Motor leftDrive;
	public Motor rightDrive;
	public Motor leftBack;
	public Motor leftFront;
	public Motor rightBack;
	public Motor rightFront;

	public Motor leftBackTurner;
	public Motor leftFrontTurner;
	public Motor rightBackTurner;
	public Motor rightFrontTurner;

	public Motor lifter;
	public Motor intake;
	public Motor arms;

	public Motor kicker;

	protected Motor[] allMotors;
	protected MotorRamping[] rampedMotors;

	public RobotMotors() {

	}

	public void stopAll() {
		for (Motor motor : this.allMotors)
			motor.stop();
	}

	public void update() {
		for (MotorRamping motor : this.rampedMotors)
			motor.update();
	}
}