package com.saintsrobotics.shoppingkart.vision;

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
	private SwerveControl control;
	private State currentState;
	private State lastState;
	private Timer timer;
	private int holdFrames;

	private final double TRANSLATION_SETPOINT = -1.03;
	private final double DISTANCE_SETPOINT = -14.72;

	public DockTask() {

		this.pidTranslationSender = new PidSender();
		this.pidTranslationController = new PIDController(-0.05, 0.0, 0.0, this.pidTranslationSender,
				(output) -> this.pidTranslationOutput = output);
		this.pidTranslationController.setSetpoint(TRANSLATION_SETPOINT);
		this.pidTranslationController.setAbsoluteTolerance(1.0);
		this.pidTranslationController.setOutputRange(-0.3, 0.3);
		this.pidTranslationController.setInputRange(-20, 20);
		this.pidTranslationController.setContinuous(false);
		this.pidTranslationController.reset();
		this.pidTranslationController.enable();

		this.pidDistanceSender = new PidSender();
		this.pidDistanceController = new PIDController(-0.2, 0.0, 0.0, this.pidDistanceSender,
				(output) -> this.pidDistanceOutput = output);
		this.pidDistanceController.setSetpoint(DISTANCE_SETPOINT);
		this.pidDistanceController.setAbsoluteTolerance(0.5);
		this.pidDistanceController.setOutputRange(-0.3, 0.3);
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
			if (limelight.getEntry("ty").getDouble(0) > -0.5) {
				this.pidDistanceController.setSetpoint(0.0);
				this.pidTranslationController.setSetpoint(0.0);
			} else if (limelight.getEntry("ty").getDouble(0) < -0.5) {
				this.pidDistanceController.setSetpoint(this.DISTANCE_SETPOINT);
				this.pidTranslationController.setSetpoint(this.TRANSLATION_SETPOINT);
			}
		}
		this.currentState = State.HOLD;
	}

	public double[] hold() {
		if (limelight.getEntry("tv").getDouble(0) == 1) {

			this.pidTranslationSender.setValue(limelight.getEntry("tx").getDouble(0));
			this.pidDistanceSender.setValue(limelight.getEntry("ty").getDouble(0));

			SmartDashboard.putBoolean("Distance on Target", this.pidDistanceController.onTarget());
			SmartDashboard.putBoolean("Center on Target", this.pidTranslationController.onTarget());

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
