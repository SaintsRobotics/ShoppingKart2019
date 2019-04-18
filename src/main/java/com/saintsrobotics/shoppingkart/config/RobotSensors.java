package com.saintsrobotics.shoppingkart.config;

import com.saintsrobotics.shoppingkart.util.AbsoluteEncoder;
import com.saintsrobotics.shoppingkart.util.DistanceEncoder;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DigitalInput;

public class RobotSensors {
	public AbsoluteEncoder rightFrontEncoder;
	public AbsoluteEncoder leftFrontEncoder;
	public AbsoluteEncoder rightBackEncoder;
	public AbsoluteEncoder leftBackEncoder;

	public ADXRS450_Gyro gyro;

	public DistanceEncoder liftEncoder;
	public DigitalInput lifterUp;
	public DigitalInput lifterDown;



	public AbsoluteEncoder kicker;

	public RobotSensors(Config robotConfig) {
		this.leftFrontEncoder = buildAbsoluteEncoder(robotConfig, "encoders.drive.leftFront");
		this.rightFrontEncoder = buildAbsoluteEncoder(robotConfig, "encoders.drive.rightFront");
		this.leftBackEncoder = buildAbsoluteEncoder(robotConfig, "encoders.drive.leftBack");
		this.rightBackEncoder = buildAbsoluteEncoder(robotConfig, "encoders.drive.rightBack");

		this.gyro = new ADXRS450_Gyro();

		this.liftEncoder = buildDistanceEncoder(robotConfig, "encoders.lift");
		this.lifterDown = new DigitalInput(robotConfig.getInt("limits.lift.down.port"));
		this.lifterUp = new DigitalInput(robotConfig.getInt("limits.lift.up.port"));

		this.kicker = buildAbsoluteEncoder(robotConfig, "encoders.kicker");

	}

	private static AbsoluteEncoder buildAbsoluteEncoder(Config robotConfig, String keyPrefix) {
		return new AbsoluteEncoder(robotConfig.getInt(keyPrefix + ".port"),
				robotConfig.getDouble(keyPrefix + ".offset"), robotConfig.getBoolean(keyPrefix + ".inverted"));
	}

	private static DistanceEncoder buildDistanceEncoder(Config robotConfig, String keyPrefix) {
		return new DistanceEncoder(robotConfig.getInt(keyPrefix + ".port1"), robotConfig.getInt(keyPrefix + ".port2"),
				robotConfig.getDouble(keyPrefix + ".ticksPerUnit"), robotConfig.getBoolean(keyPrefix + ".reversed"));
	}
}