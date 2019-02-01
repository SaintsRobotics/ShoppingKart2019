/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.saintsrobotics.swerveDrive.tasks.teleop;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.input.OI.XboxInput;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;

/**
 * Task to switch between inputs for SwerveControl
 */
public class SwerveInput extends RunEachFrameTask {
	private XboxInput xboxInput;
	private ADXRS450_Gyro gyro;
	private SwerveControl control;

	private final double SPEED_GAIN = .5;
	private final double TURN_GAIN = .5;

	public SwerveInput(XboxInput xboxInput, ADXRS450_Gyro gyro, SwerveControl control) {
		this.xboxInput = xboxInput;
		this.gyro = gyro;
		this.control = control;
	}

	/**
	 * gets translation and rotation input from the xbox controller
	 * 
	 * @return an array in the format of {leftStickX, leftSticyY, rightStickX}
	 */
	public double[] readXboxInput() {
		double[] xboxValues = new double[3];
		double leftStickX = xboxInput.leftStickX() * this.SPEED_GAIN;
		double leftStickY = -xboxInput.leftStickY() * this.SPEED_GAIN;
		double rightStickX = xboxInput.rightStickX() * this.TURN_GAIN;

		// dead zone
		if (Math.abs(rightStickX) <= 0.15) {
			rightStickX = 0;
		}

		// Absolute control
		if (this.xboxInput.RB()) {
			// Gyro coords are continous so this restricts it to 360 degrees
			double robotAngle = ((this.gyro.getAngle() % 360) + 360) % 360;

			// Temporary save of x and y pre-translation
			double tempX = leftStickX;
			double tempY = leftStickY;

			// Overwriting x and y
			leftStickX = (tempX * Math.cos(Math.toRadians(robotAngle)))
					- (tempY * Math.sin(Math.toRadians(robotAngle)));
			leftStickY = (tempX * Math.sin(Math.toRadians(robotAngle)))
					+ (tempY * Math.cos(Math.toRadians(robotAngle)));
		}

		xboxValues[0] = leftStickX;
		xboxValues[1] = leftStickY;
		xboxValues[2] = rightStickX;
		return xboxValues;
	}

	@Override
	// Pass the values into SwerveControl
	public void runEachFrame() {
		double[] xboxValues = readXboxInput();
		double leftStickX = xboxValues[0];
		double leftStickY = xboxValues[1];
		double rightStickX = xboxValues[2];
		this.control.setTranslationVector(leftStickX, leftStickY);
		this.control.setRotationVector(rightStickX);
	}
}
