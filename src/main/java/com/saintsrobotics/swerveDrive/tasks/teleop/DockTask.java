package com.saintsrobotics.swerveDrive.tasks.teleop;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.saintsrobotics.swerveDrive.util.PIDReceiver;
import com.saintsrobotics.swerveDrive.util.PidSender;
import com.saintsrobotics.swerveDrive.util.VisionBroker;

import org.opencv.core.Rect;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DockTask extends RunEachFrameTask {

	private VisionBroker broker;

	private PIDReceiver pidReciever;
	private PidSender pidSender;
	private PIDController pidController;

	private SwerveControl sc;

	public DockTask(VisionBroker broker, SwerveControl sc) {
		this.broker = broker;
		this.sc = sc;

		this.pidReciever = new PIDReceiver();
		this.pidSender = new PidSender();
		this.pidController = new PIDController(0.0, 0.0, 0.0, this.pidSender, pidReciever);
		this.pidController.setAbsoluteTolerance(2.0);
		this.pidController.setOutputRange(-1, 1);
		this.pidController.setInputRange(0, 360);
		this.pidController.setContinuous();
		this.pidController.reset();
		this.pidController.enable();
	}

	@Override
	protected void runEachFrame() {
		Rect targetOne = broker.getRects()[0];
		Rect targetTwo = broker.getRects()[1];
		if (targetOne == null || targetTwo == null) {
			DriverStation.reportWarning("No rectangles", false);
			return;
		}
		DriverStation.reportWarning("Yes rectangles", false);
		Double t1 = targetOne.tl().x;
		Double t2 = targetTwo.br().x;
		SmartDashboard.putNumber("Target 1", targetOne.tl().x);
		SmartDashboard.putNumber("Target 2", targetTwo.br().x);
		SmartDashboard.putNumber("Center", t1 + (t2 - t1));

		// write PID loop here
		this.pidSender.setValue((t2 + t1) / 2);
		this.pidController.setSetpoint(180); // any way to bring this value up a few abstractions?
	}
}