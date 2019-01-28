package com.saintsrobotics.swerveDrive.tasks.teleop;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.saintsrobotics.swerveDrive.output.SwerveWheel;
import com.saintsrobotics.swerveDrive.util.AngleUtilities;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.PIDController;

public class SwerveControl extends RunEachFrameTask {
	private static final double SPEED_GAIN = 0.75;
	private static final double TURN_GAIN = 0.25;

	private SwerveWheel[] wheels;
	private boolean isTurning;
	private double maxRad;
	private double turnCoefficient;

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
		this.turnCoefficient = this.TURN_GAIN / this.maxRad;

		this.gyro = gyro;
		this.headingPidController = new PIDController(0.1, 0.0, 0.0, this.gyro,
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

		this.translationX *= SPEED_GAIN;
		this.translationY *= SPEED_GAIN;

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
		// Finally, converting it to a polar vector
		double[][] vectors = new double[wheels.length][2];
		for (int i = 0; i < wheels.length; i++) {
			vectors[i][0] = wheels[i].getRotationVector()[0] * this.turnCoefficient * rotationInput + translationX;
			vectors[i][1] = wheels[i].getRotationVector()[1] * this.turnCoefficient * rotationInput + translationY;
			vectors[i] = AngleUtilities.cartesianToPolar(vectors[i]);
			wheels[i].setHeadAndVelocity(vectors[i][0], vectors[i][1]);
		}
	}
}