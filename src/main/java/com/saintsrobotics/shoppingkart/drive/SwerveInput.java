/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.saintsrobotics.shoppingkart.drive;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.input.OI.XboxInput;
import com.saintsrobotics.shoppingkart.vision.DockTask;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Task to switch between inputs for SwerveControl
 */
public class SwerveInput extends RunEachFrameTask {
	private XboxInput xboxInput;
	private ADXRS450_Gyro gyro;
	private SwerveControl control;
	private DockTask dock;

	private final double SPEED_GAIN = .75;
	private final double BOOST_GAIN = 1;
	private final double TURN_GAIN = .7;

	private State currentState;
	private State lastState;

	private double[] docking;

	public SwerveInput(XboxInput xboxInput, ADXRS450_Gyro gyro, SwerveControl control, DockTask dock) {
		this.xboxInput = xboxInput;
		this.gyro = gyro;
		this.control = control;
		this.dock = dock;
		this.docking = new double[2];
		this.currentState = State.START_CONTROLLER;
	}

	/**
	 * gets translation and rotation input from the xbox controller
	 * 
	 * @return an array in the format of {leftStickX, leftSticyY, rightStickX}
	 */
	public double[] readXboxInput() {
		double leftStickX = xboxInput.leftStickX();
		double leftStickY = -xboxInput.leftStickY();
		double rightStickX = xboxInput.rightStickX();

		// dead zone
		if (Math.abs(rightStickX) <= 0.15) {
			rightStickX = 0;
		}

		if (this.xboxInput.LB()) {
			leftStickX *= this.BOOST_GAIN;
			leftStickY *= this.BOOST_GAIN;
			rightStickX *= this.BOOST_GAIN;
		} else {
			leftStickX *= this.SPEED_GAIN;
			leftStickY *= this.SPEED_GAIN;
			rightStickX *= this.TURN_GAIN;
		}

		// Straight forward
		if (this.xboxInput.leftTrigger() > 0.25) {
			leftStickX = 0;
			leftStickY = this.xboxInput.leftTrigger() * this.SPEED_GAIN;
			rightStickX = 0;
		}

		// Straight backward
		if (this.xboxInput.rightTrigger() > 0.25) {
			leftStickX = 0;
			leftStickY = (this.xboxInput.rightTrigger() * this.SPEED_GAIN) * -1;
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

		return new {leftStickX, leftStickY, rightStickX};
	}

	public double[] readDockTaskInput() {
		return this.docking;
	}

	private void doController() {
		double[] xboxValues = readXboxInput();
		double leftStickX = xboxValues[0];
		double leftStickY = xboxValues[1];
		double rightStickX = xboxValues[2];
		this.control.setTranslationVector(leftStickX, leftStickY);
		this.control.setRotationVector(rightStickX);
	}

	private void doStartDocking() {
		NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(0);
		NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(0);
		this.dock.init();
	}

	private void doDocking() {
		this.docking = this.dock.run();
		this.control.setTranslationVector(this.docking[0], this.docking[1]);
		this.control.setRotationVector(0);
	}

	private void doStartController() {
		// NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(1);
		// NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(1);
	}

	public boolean isXboxNotZero(double[] vals) {
		return !(vals[0] > -0.1 && vals[0] < 0.1) || !(vals[1] > -0.1 && vals[1] < 0.1)
				|| !(vals[2] > -0.1 && vals[2] < 0.1);
	}

	@Override
	// Pass the values into SwerveControl
	public void runEachFrame() {
		if (currentState != lastState) {
			lastState = currentState;
		}
		SmartDashboard.putString("swerve input state", this.currentState.toString());

		switch (this.currentState) {
		case CONTROLLER:
			doController();
			if (xboxInput.SELECT()) {
				this.currentState = State.START_DOCKING;
			}
			// NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(0);
			// NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(0);

			break;
		case START_DOCKING:
			doStartDocking();
			this.currentState = State.DOCKING;
			break;
		case DOCKING:
			doDocking();
			double[] xboxInput = readXboxInput();
			SmartDashboard.putNumber("docking hold frames", dock.getHoldFrames());

			if (isXboxNotZero(xboxInput))
				this.currentState = State.START_CONTROLLER;

			break;
		case START_CONTROLLER:
			doStartController();
			this.currentState = State.CONTROLLER;
			break;
		}
	}

	private static enum State {
		CONTROLLER, START_DOCKING, DOCKING, START_CONTROLLER
	}
}
