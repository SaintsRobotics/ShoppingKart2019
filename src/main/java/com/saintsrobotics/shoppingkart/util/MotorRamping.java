package com.saintsrobotics.shoppingkart.util;

import com.github.dozer.output.Motor;

import edu.wpi.first.wpilibj.SpeedController;

public class MotorRamping implements Motor {
	public static double MOTOR_RAMPING; // treat this as a final

	private SpeedController speedController;

	public MotorRamping(SpeedController speedController, boolean inverted) {
		this(speedController, inverted, 0.3);
	}

	public MotorRamping(SpeedController speedController, boolean inverted, double motorRamping) {
		this.speedController = speedController;
		this.speedController.setInverted(inverted);
		MOTOR_RAMPING = motorRamping;
	}

	private double setpoint = 0;
	private double current = 0;

	public double get() {
		return speedController.get();
	}

	public void set(double speed) {
		setpoint = speed;
	}

	public void stop() {
		speedController.stopMotor();
		setpoint = 0;
		current = 0;
	}

	public void update() {
		if (Math.abs(setpoint - current) < MOTOR_RAMPING) {
			current = setpoint;
		} else if (setpoint > current) {
			current += MOTOR_RAMPING;
		} else if (setpoint < current) {
			current -= MOTOR_RAMPING;
		}
		speedController.set(current);
	}
}
