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

	private double time;

	private ADXRS450_Gyro gyro;

	private PIDReceiver headingPidReceiver;
	private PIDController headingPidController;

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

		time = Timer.getFPGATimestamp();
	}

	public void setRobotTargetHead(double n) {
		this.headingPidController.setSetpoint(n);
	}

	@Override
	public void runEachFrame() {
		double now = Timer.getFPGATimestamp();
		SmartDashboard.putNumber("elapsed time", now - time);
		time = now;

		double leftStickX = xboxInput.leftStickX();
		double leftStickY = -xboxInput.leftStickY();
		double rightStickX = xboxInput.rightStickX();

		leftStickX *= this.SPEED_GAIN;
		leftStickY *= this.SPEED_GAIN;

		if (Math.abs(rightStickX) <= 0.15) {
			rightStickX = 0;
		}

		// Gyro coords are continuous so this restricts it to 360
		double currentHead = ((this.gyro.getAngle() % 360) + 360) % 360;

		// this.robotTargetHead = AngleUtilities.findAngle(rightStickX, rightStickY);
		double rotationInput = this.headingPidReceiver.getOutput();
		if (rightStickX != 0.0) {
			rotationInput = rightStickX;
			this.isTurning = true;
		} else if (rightStickX == 0.0 && this.isTurning) {
			this.headingPidController.setSetpoint(currentHead);
			this.isTurning = false;
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

		// combination of rotation and translation vectors for each of the four
		// SwerveWheels
		// ultimately will be a polar vector {heading, velocity}
		double[][] vectors = new double[wheels.length][2];
		for (int i = 0; i < wheels.length; i++) {
			vectors[i][0] = wheels[i].getRotationVector()[0] * this.turnCoefficient * rotationInput + leftStickX;
			vectors[i][1] = wheels[i].getRotationVector()[1] * this.turnCoefficient * rotationInput + leftStickY;
			vectors[i] = AngleUtilities.cartesianToPolar(vectors[i]);
			wheels[i].setHeadAndVelocity(vectors[i][0], vectors[i][1]);
		}

		SmartDashboard.putNumber("gyro ", ((this.gyro.getAngle() % 360) + 360) % 360);
		SmartDashboard.putNumber("error ", this.headingPidController.getError());
		SmartDashboard.putNumber("output ", this.headingPidReceiver.getOutput());
	}
}