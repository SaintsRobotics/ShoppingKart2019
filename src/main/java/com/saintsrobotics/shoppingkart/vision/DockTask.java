package com.saintsrobotics.shoppingkart.vision;

import com.saintsrobotics.shoppingkart.util.PidSender;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DockTask {

	private PidSender pidSender;
	private PIDController pidController;
	private double pidTranslationOutput;
	private NetworkTable limelight;

	public DockTask() {

		this.pidSender = new PidSender();
		this.pidController = new PIDController(0.0003, 0.0, 0.0, this.pidSender,
				(output) -> this.pidTranslationOutput = output);
		this.pidController.setSetpoint(0.0);
		this.pidController.setAbsoluteTolerance(2.0);
		this.pidController.setOutputRange(-1, 1);
		this.pidController.setInputRange(0, 360);
		this.pidController.reset();
		this.pidController.enable();
		this.limelight = NetworkTableInstance.getDefault().getTable("limelight");
	}

	public void setPIDSetpoint() {

	}

	public double getOutput() {

		if (limelight.getEntry("tv").getDouble(0) == 1) {

			this.pidSender.setValue(limelight.getEntry("tx").getDouble(0));
		}
		SmartDashboard.putNumber("X Offset", limelight.getEntry("tx").getDouble(0));
		SmartDashboard.putNumber("Y Offset", limelight.getEntry("ty").getDouble(0));
		SmartDashboard.putNumber("Area offset", limelight.getEntry("ta").getDouble(0));
		SmartDashboard.putNumber("Translation Output", this.pidTranslationOutput);
		return this.pidTranslationOutput;
	}
}