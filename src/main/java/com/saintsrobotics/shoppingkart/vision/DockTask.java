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
	private PidSender pidDistanceSender;

	private PIDController pidTranslation;
	private PIDController pidDistance;

	private PidConfig cargoTranslationConfig;
	private PidConfig cargoDistanceConfig;
	private PidConfig hatchTranslationConfig;
	private PidConfig hatchDistanceConfig;

	private double pidTranslationOutput;
	private double pidDistanceOutput;
	private NetworkTable limelight;
	private State currentState;
	private State lastState;
	private Timer timer;
	private int holdFrames;

	// treate as finals
	private double CARGO_TRANSLATION_SETPOINT;
	private double CARGO_DISTANCE_SETPOINT;
	private double HATCH_TRANSLATION_SETPOINT;
	private double HATCH_DISTANCE_SETPOINT;

	/**
	 * 
	 * @param cargoTranslationPid
	 * @param cargoDistancePid
	 * @param hatchTranslationPid
	 * @param hatchDistancePid
	 * @param hatchTranslation    setpoint
	 * @param hatchDistance       setpoint
	 * @param cargoTranslation    setpoint
	 * @param cargoDistance       setpoint
	 */
	public DockTask(PidConfig cargoTranslationPid, PidConfig cargoDistancePid, PidConfig hatchTranslationPid,
			PidConfig hatchDistancePid, double hatchTranslation, double hatchDistance, double cargoTranslation,
			double cargoDistance) {

		this.pidTranslationSender = new PidSender();
		this.pidDistanceSender = new PidSender();

		this.CARGO_TRANSLATION_SETPOINT = cargoTranslation;
		this.CARGO_DISTANCE_SETPOINT = cargoDistance;
		this.HATCH_TRANSLATION_SETPOINT = hatchTranslation;
		this.HATCH_DISTANCE_SETPOINT = hatchDistance;

		this.cargoTranslationConfig = cargoTranslationPid;
		this.cargoDistanceConfig = cargoDistancePid;
		this.hatchTranslationConfig = hatchTranslationPid;
		this.hatchDistanceConfig = hatchDistancePid;

		// pid configs default to cargo
		this.pidTranslation = new PIDController(cargoTranslationPid.kP, cargoTranslationPid.kI, cargoTranslationPid.kD,
				this.pidTranslationSender, (output) -> this.pidTranslationOutput = output);
		this.pidTranslation.setAbsoluteTolerance(cargoTranslationPid.tolerance);
		this.pidTranslation.setOutputRange(-0.25, 0.25);
		this.pidTranslation.setInputRange(-20, 20);
		this.pidTranslation.setContinuous(false);

		this.pidDistance = new PIDController(cargoDistancePid.kP, cargoDistancePid.kI, cargoDistancePid.kD,
				this.pidDistanceSender, (output) -> this.pidDistanceOutput = output);
		this.pidDistance.setAbsoluteTolerance(cargoDistancePid.tolerance);
		this.pidDistance.setOutputRange(-0.4, 0.4);
		this.pidDistance.setInputRange(-20, 20);
		this.pidDistance.setContinuous(false);

		this.holdFrames = 0;

		this.limelight = NetworkTableInstance.getDefault().getTable("limelight");
		this.currentState = State.WARMUP_CAMERA;
		this.timer = new Timer();
	}

	public void init() {
		this.currentState = State.WARMUP_CAMERA;
		this.holdFrames = 0;
		limelight.getEntry("pipeline").setNumber(2);
	}

	public void resetPID() {
		this.pidTranslation.reset();
		this.pidTranslation.enable();
		this.pidDistance.reset();
		this.pidDistance.enable();
	}

	public void doWarmupCamera() {
		if (limelight.getEntry("tv").getDouble(0) == 1) {
			if (limelight.getEntry("ty").getDouble(0) > 10) {
				this.pidTranslation.setP(this.cargoTranslationConfig.kP);
				this.pidDistance.setP(this.cargoDistanceConfig.kP);
				this.pidTranslation.setSetpoint(this.CARGO_TRANSLATION_SETPOINT);
				this.pidDistance.setSetpoint(this.CARGO_DISTANCE_SETPOINT);
				SmartDashboard.putString("vision setpoint", "cargo");
			} else if (limelight.getEntry("ty").getDouble(0) < 10) {
				this.pidTranslation.setP(this.hatchTranslationConfig.kP);
				this.pidDistance.setP(this.hatchDistanceConfig.kP);
				this.pidTranslation.setSetpoint(this.HATCH_TRANSLATION_SETPOINT);
				this.pidDistance.setSetpoint(this.HATCH_DISTANCE_SETPOINT);
				SmartDashboard.putString("vision setpoint", "hatch");
			}
			resetPID();

			this.currentState = State.HOLD;
		}
	}

	public double[] hold() {
		if (limelight.getEntry("tv").getDouble(0) == 1) {

			this.pidTranslationSender.setValue(limelight.getEntry("tx").getDouble(0));
			this.pidDistanceSender.setValue(limelight.getEntry("ta").getDouble(0));

			SmartDashboard.putNumber("translation dock pid error", this.pidTranslation.getError());
			SmartDashboard.putNumber("distance dock pid error", this.pidDistance.getError());
			SmartDashboard.putNumber("translation dock pid output", this.pidTranslationOutput);
			SmartDashboard.putNumber("distance dock pid output", this.pidDistanceOutput);
			SmartDashboard.putNumber("translation dock pid setpoint", this.pidTranslation.getSetpoint());
			SmartDashboard.putNumber("distance dock pid setpoint", this.pidDistance.getSetpoint());

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
