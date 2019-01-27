package com.saintsrobotics.swerveDrive.output;

import com.github.dozer.output.Motor;
import com.saintsrobotics.swerveDrive.util.PIDReceiver;
import com.saintsrobotics.swerveDrive.util.TurnConfiguration;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;

public class SwerveWheel {
	private Motor driveMotor;
	private Motor turnMotor;
	public double targetHead;
	public double targetVelocity;
	// x and y coordinates of wheel and location of pivot point on robot
	private double[] wheelLoc = new double[2];
	private double[] pivotLoc = new double[2];
	private double[] rotationVector;
	// distance of wheel from pivot
	private double radius;

	private PIDReceiver headingPidReceiver;
	private PIDController headingPidController;
	private PIDSource encoder;

	public SwerveWheel(Motor driveMotor, Motor turnMotor, TurnConfiguration pidConfig, double[] wheelLoc,
			double[] pivotLoc) {
		this.driveMotor = driveMotor;
		this.turnMotor = turnMotor;
		this.encoder = pidConfig.encoder;

		this.wheelLoc = wheelLoc;
		this.pivotLoc = pivotLoc;

		this.rotationVector = new double[] { this.wheelLoc[1] - this.pivotLoc[1], this.pivotLoc[0] - this.wheelLoc[0] };

		this.radius = Math.sqrt(Math.pow((this.wheelLoc[0] - this.pivotLoc[0]), 2)
				+ Math.pow((this.wheelLoc[1] - this.pivotLoc[1]), 2));

		this.headingPidReceiver = new PIDReceiver();
		this.headingPidController = new PIDController(pidConfig.forwardHeadingKP, pidConfig.forwardHeadingKI,
				pidConfig.forwardHeadingKD, pidConfig.encoder, headingPidReceiver);
		this.headingPidController.setAbsoluteTolerance(pidConfig.forwardHeadingTolerance);
		this.headingPidController.setOutputRange(-01, 01);
		this.headingPidController.setInputRange(0, 360);
		this.headingPidController.setContinuous();
		this.headingPidController.reset();
		this.headingPidController.enable();
	}

	public void setHeadAndVelocity(double targetHead, double targetVelocity) {
		double diff = 0.0;
		double currentHead = this.encoder.pidGet();
		if (Math.abs(targetHead - currentHead) > 180) {
			diff = 360 - Math.abs(targetHead - currentHead);
		} else {
			diff = Math.abs(targetHead - currentHead);
		}
		if (diff > 90) {
			targetHead += 180;
			targetHead %= 360;
			targetVelocity = -targetVelocity;
		}
		this.targetVelocity = targetVelocity;
		this.targetHead = targetHead;

		// this was in RunEachFrame
		this.driveMotor.set(this.targetVelocity);
		this.headingPidController.setSetpoint(this.targetHead);
		this.turnMotor.set(this.headingPidReceiver.getOutput());
	}

	/**
	 * Gets distance of the swerve wheel from the pivot point
	 * 
	 * @return The distance of the wheel to the pivot point
	 */
	public double getRadius() {
		return this.radius;
	}

	/**
	 * vector returned is used for calculating head and velocity (cartesian form)
	 * for each wheel the direction of the vector is fully calculated, and so is
	 * part of its magnitude the rest of the calculation is done in SwerveControl
	 * because we need to know the SwerveWheel's siblings' states
	 * 
	 * @return a partially-calculated rotation vector, unique to each wheel
	 */
	public double[] getRotationVector() {
		return this.rotationVector;
	}
}
