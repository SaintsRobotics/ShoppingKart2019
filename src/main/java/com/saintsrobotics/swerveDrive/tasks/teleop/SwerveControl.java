package com.saintsrobotics.swerveDrive.tasks.teleop;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.input.OI.XboxInput;
import com.saintsrobotics.swerveDrive.output.SwerveWheel;
import com.saintsrobotics.swerveDrive.util.AngleUtilities;
import com.saintsrobotics.swerveDrive.util.PIDReceiver;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveControl extends RunEachFrameTask {
	private static final double SPEED_GAIN = 0.75;
	private static final double TURN_GAIN = 0.25;

	private XboxInput xboxInput;
	private SwerveWheel[] wheels;
	private boolean isTurning;
	private double maxRad;
	private double turnCoefficient;

	private ADXRS450_Gyro gyro;

	private PIDReceiver headingPidReceiver;
	private PIDController headingPidController;

	private double translationX;
	private double translationY;
	private double rotationX;

	public SwerveControl(XboxInput xboxInput, SwerveWheel[] wheels, ADXRS450_Gyro gyro) {
		this.xboxInput = xboxInput;
		this.wheels = wheels;

		for (SwerveWheel s : wheels) {
			if (s.getRadius() > this.maxRad) {
				this.maxRad = s.getRadius();
			}
		}
		this.turnCoefficient = this.TURN_GAIN / this.maxRad;

		this.gyro = gyro;
		this.headingPidReceiver = new PIDReceiver();
		this.headingPidController = new PIDController(0.1, 0.0, 0.0, this.gyro, headingPidReceiver);
		this.headingPidController.setAbsoluteTolerance(2.0);
		this.headingPidController.setOutputRange(-1, 1);
		this.headingPidController.setInputRange(0, 360);
		this.headingPidController.setContinuous();
		this.headingPidController.reset();
		this.headingPidController.enable();
	}

	public void setTranslationVector(double x, double y) {
		this.translationX = x;
		this.translationY = y;
	}

	public void setRotationVector(double x) {
		this.rotationX = x;
	}

	public void setRobotTargetHead(double n) {
		this.headingPidController.setSetpoint(n);
	}

	@Override
	public void runEachFrame() {
		// double now = Timer.getFPGATimestamp();
		// SmartDashboard.putNumber("elapsed time", now - time);
		// this.time = now;

		this.translationX *= SPEED_GAIN;
		this.translationY *= SPEED_GAIN;

		// // dead zone
		// if (Math.abs(rotationX) <= 0.15) {
		// rotationX = 0;
		// }

		// Gyro coords are continuous so this restricts it to 360
		double currentHead = ((this.gyro.getAngle() % 360) + 360) % 360;

		// this.robotTargetHead = AngleUtilities.findAngle(rotationX, rightStickY);
		double rotationInput = this.headingPidReceiver.getOutput();
		if (this.rotationX != 0.0) {
			rotationInput = this.rotationX;
			this.isTurning = true;
		} else if (this.rotationX == 0.0 && this.isTurning) {
			this.headingPidController.setSetpoint(currentHead);
			this.isTurning = false;
		}

		// combination of rotation and translation vectors for each of the four
		// SwerveWheels
		// ultimately will be a polar vector {heading, velocity}
		double[][] vectors = new double[wheels.length][2];
		for (int i = 0; i < wheels.length; i++) {
			vectors[i][0] = wheels[i].getRotationVector()[0] * this.turnCoefficient * rotationInput + translationX;
			vectors[i][1] = wheels[i].getRotationVector()[1] * this.turnCoefficient * rotationInput + translationY;
			vectors[i] = AngleUtilities.cartesianToPolar(vectors[i]);
			wheels[i].setHeadAndVelocity(vectors[i][0], vectors[i][1]);
		}

		SmartDashboard.putNumber("gyro ", ((this.gyro.getAngle() % 360) + 360) % 360);
		SmartDashboard.putNumber("error ", this.headingPidController.getError());
		SmartDashboard.putNumber("output ", this.headingPidReceiver.getOutput());
	}
}