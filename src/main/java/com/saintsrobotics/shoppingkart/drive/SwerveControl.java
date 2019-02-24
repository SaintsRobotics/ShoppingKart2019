package com.saintsrobotics.shoppingkart.drive;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.saintsrobotics.shoppingkart.drive.SwerveWheel;
import com.saintsrobotics.shoppingkart.util.AngleUtilities;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.PIDController;

public class SwerveControl extends RunEachFrameTask {
	private static final double SPEED_COEF = 1;

	private SwerveWheel[] wheels;
	private boolean isTurning;
	private double maxRad;

	private ADXRS450_Gyro gyro;

	private PIDController headingPidController;
	private double headingPidOutput;

	private double translationX;
	private double translationY;
	private double rotationX;

	public SwerveControl(SwerveWheel[] wheels, ADXRS450_Gyro gyro) {
		this.wheels = wheels;

		for (SwerveWheel s : wheels) {
			if (s.getRadius() > this.maxRad) {
				this.maxRad = s.getRadius();
			}
		}

		this.gyro = gyro;
		this.headingPidController = new PIDController(0.0120, 0.0, 0.01, this.gyro,
				(output) -> this.headingPidOutput = output);
		this.headingPidController.setAbsoluteTolerance(2.0);
		this.headingPidController.setOutputRange(-1, 1);
		this.headingPidController.setInputRange(0, 360);
		this.headingPidController.setContinuous();
		this.headingPidController.reset();
		this.headingPidController.enable();
	}

	/**
	 * sets horizontal and vertical movement vectors
	 * 
	 * @param x direction (and magnitude) of the horizontal-movement vector
	 * @param y direction (and magnitude) of the vertical-movement vector
	 */
	public void setTranslationVector(double x, double y) {
		this.translationX = x;
		this.translationY = y;
	}

	/**
	 * sets the magnitude and direction of rotation vector (eg. turn stick input)
	 * 
	 * @param x the magnitude and direction of the raw rotation vector
	 */
	public void setRotationVector(double x) {
		this.rotationX = x;
	}

	/**
	 * sends target heading to pid, in degrees will maintain heading over time, and
	 * is an absolute position
	 * 
	 * @param n the target heading in degrees
	 */
	public void setRobotTargetHead(double n) {
		this.headingPidController.setSetpoint(n);
	}

	@Override
	public void runEachFrame() {

		// Gyro coords are continuous so this restricts it to 360
		double currentHead = ((this.gyro.getAngle() % 360) + 360) % 360;

		double rotationInput = this.headingPidOutput;
		if (this.rotationX != 0.0) {
			rotationInput = this.rotationX;
			this.isTurning = true;
		} else if (this.rotationX == 0.0 && this.isTurning) {
			this.headingPidController.setSetpoint(currentHead);
			this.isTurning = false;
		}

		// Doing math with each of the vectors for the SwerveWheels
		// Calculating the rotation vector, then adding that to the translation vector
		// Converting them to polar vectors
		double[][] vectors = new double[wheels.length][2];
		for (int i = 0; i < wheels.length; i++) {
			vectors[i][0] = wheels[i].getRotationVector()[0] * (1 / this.maxRad) * rotationInput + translationX;
			vectors[i][1] = wheels[i].getRotationVector()[1] * (1 / this.maxRad) * rotationInput + translationY;
			vectors[i] = AngleUtilities.cartesianToPolar(vectors[i]);
		}

		// If any of the velocities are greater than SPEED_COEF, then scale them all
		// down
		boolean needsScale = false;
		double maxVelocity = 0; // an arbitrary value
		int v = 0; // index used for traversing the vectors array
		while (!needsScale && v < vectors.length) {
			needsScale = vectors[v][1] > SwerveControl.SPEED_COEF;
			maxVelocity = Math.max(maxVelocity, vectors[v][1]);
			v++;
		}
		if (needsScale) {
			for (double[] i : vectors) {
				i[1] /= maxVelocity;
			}
		}

		if (Math.abs(maxVelocity) < 0.05) {
			for (int i = 0; i < wheels.length; i++) {
				wheels[i].setVelocity(0.0);
			}
		} else {
			for (int i = 0; i < wheels.length; i++) {
				wheels[i].setHeadAndVelocity(vectors[i][0], vectors[i][1]);
			}
		}
	}
}
