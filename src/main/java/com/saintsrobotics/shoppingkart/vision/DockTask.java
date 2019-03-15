package com.saintsrobotics.shoppingkart.vision;

import com.saintsrobotics.shoppingkart.config.PidConfig;
import com.saintsrobotics.shoppingkart.drive.SwerveControl;
import com.saintsrobotics.shoppingkart.util.PidSender;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DockTask {

	private PidSender pidTranslationSender;
	private PIDController pidTranslationController;
	private PidSender pidDistanceSender;
	private PIDController pidDistanceController;
	private double pidTranslationOutput;
	private double pidDistanceOutput;
	private NetworkTable limelight;
	private State currentState;
	private State lastState;
	private Timer timer;
	private int holdFrames;

	private final double HATCH_TRANSLATION_SETPOINT;
	private final double HATCH_DISTANCE_SETPOINT;
	private final double CARGO_TRANSLATION_SETPOINT;
	private final double CARGO_DISTANCE_SETPOINT;

	/**
	 * 
	 * @param translationPidConfig
	 * @param distancePicConfig
	 * @param hatchTranslation
	 * @param hatchDistance
	 * @param cargoTranslation
	 * @param cargoDistance
	 */
	public DockTask(PidConfig translationPidConfig, PidConfig distancePicConfig, double hatchTranslation,
			double hatchDistance, double cargoTranslation, double cargoDistance) {
		this.HATCH_TRANSLATION_SETPOINT = hatchTranslation;
		this.HATCH_DISTANCE_SETPOINT = hatchDistance;
		this.CARGO_TRANSLATION_SETPOINT = cargoTranslation;
		this.CARGO_DISTANCE_SETPOINT = cargoDistance;

		this.pidTranslationSender = new PidSender();
		this.pidTranslationController = new PIDController(translationPidConfig.kP, translationPidConfig.kI,
				translationPidConfig.kD, this.pidTranslationSender, (output) -> this.pidTranslationOutput = output);
		// this.pidTranslationController.setSetpoint(HATCH_TRANSLATION_SETPOINT);
		this.pidTranslationController.setAbsoluteTolerance(translationPidConfig.tolerance);
		this.pidTranslationController.setOutputRange(-0.25, 0.25);
		this.pidTranslationController.setInputRange(-20, 20);
		this.pidTranslationController.setContinuous(false);
		this.pidTranslationController.reset();
		this.pidTranslationController.enable();

		this.pidDistanceSender = new PidSender();
		this.pidDistanceController = new PIDController(distancePicConfig.kP, distancePicConfig.kI, distancePicConfig.kD,
				this.pidDistanceSender, (output) -> this.pidDistanceOutput = output);
		// this.pidDistanceController.setSetpoint(HATCH_DISTANCE_SETPOINT);
		this.pidDistanceController.setAbsoluteTolerance(distancePicConfig.tolerance);
		this.pidDistanceController.setOutputRange(-0.25, 0.25);
		this.pidDistanceController.setInputRange(-20, 20);
		this.pidDistanceController.setContinuous(false);
		this.pidDistanceController.reset();
		this.pidDistanceController.enable();

		this.holdFrames = 0;

		this.limelight = NetworkTableInstance.getDefault().getTable("limelight");
		this.currentState = State.WARMUP_CAMERA;
		this.timer = new Timer();
	}

	public void init() {
		this.currentState = State.WARMUP_CAMERA;
		this.holdFrames = 0;
	}

	public void resetPID() {
		this.pidTranslationController.reset();
		this.pidTranslationController.enable();
		this.pidDistanceController.reset();
		this.pidDistanceController.enable();
	}

	public void doWarmupCamera() {
		// timer.start();
		resetPID();
		// while (!timer.hasPeriodPassed(1)) {

		// }
		// timer.stop();
		limelight.getEntry("pipeline").setNumber(2);
		if (limelight.getEntry("tv").getDouble(0) == 1) {
			if (limelight.getEntry("ty").getDouble(0) > 1) {
				this.pidDistanceController.setSetpoint(this.CARGO_DISTANCE_SETPOINT);
				this.pidTranslationController.setSetpoint(this.CARGO_TRANSLATION_SETPOINT);
				SmartDashboard.putString("vision setpoint", "cargo");
			} else if (limelight.getEntry("ty").getDouble(0) < 1) {
				this.pidDistanceController.setSetpoint(this.HATCH_DISTANCE_SETPOINT);
				this.pidTranslationController.setSetpoint(this.HATCH_TRANSLATION_SETPOINT);
				SmartDashboard.putString("vision setpoint", "hatch");
			}
		}
		this.currentState = State.HOLD;
	}

	public double[] hold() {
		if (limelight.getEntry("tv").getDouble(0) == 1) {

			this.pidTranslationSender.setValue(limelight.getEntry("tx").getDouble(0));
			this.pidDistanceSender.setValue(limelight.getEntry("ty").getDouble(0));

			SmartDashboard.putNumber("distance dock pid error", this.pidDistanceController.getError());
			SmartDashboard.putNumber("translation dock pid error", this.pidTranslationController.getError());

			if (Math.abs(this.pidTranslationOutput) < 0.1 && Math.abs(pidDistanceOutput) < 0.1) {
				this.holdFrames++;
			} else {
				this.holdFrames = 0;
			}

			return new double[] { this.pidTranslationOutput, this.pidDistanceOutput };
		}

		return new double[] { 0.0, 0.0 };
	}

	public double getHoldFrames() {
		return this.holdFrames;
	}

	public double[] run() {
		if (currentState != lastState) {
			SmartDashboard.putString("dock state", currentState.toString());
			lastState = currentState;
		}
		double[] vector = new double[2];
		switch (this.currentState) {
		case WARMUP_CAMERA:
			doWarmupCamera();
			break;
		case HOLD:
			vector = hold();
			break;
		}
		return vector;
	}

	private static enum State {
		WARMUP_CAMERA, HOLD
	}
}
