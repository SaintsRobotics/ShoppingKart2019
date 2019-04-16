package com.saintsrobotics.shoppingkart.config;

import com.github.dozer.output.Motor;
import com.github.dozer.output.MotorSimple;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.saintsrobotics.shoppingkart.util.MotorRamping;

import edu.wpi.first.wpilibj.Talon;

public class Motors {
	public Motor leftDrive;
	public Motor rightDrive;
	public Motor leftBack;
	public Motor leftFront;
	public Motor rightBack;
	public Motor rightFront;

	public Motor leftBackTurner;
	public Motor leftFrontTurner;
	public Motor rightBackTurner;
	public Motor rightFrontTurner;

	public Motor lifter;
	public Motor intake;
	public Motor arms;

	public CANSparkMax RightFrontSparkMax;
	public CANSparkMax LeftFrontSparkMax;
	public CANSparkMax RightBackSparkMax;
	public CANSparkMax LeftBackSparkMax;

	public Motor kicker;

	protected Motor[] allMotors;
	protected MotorRamping[] rampedMotors;

	public Motors(Config robotConfig) {
		// this.leftBack = buildTalonMotor(robotConfig, "motors.drive.leftBack", true);
		// this.leftFront = buildTalonMotor(robotConfig, "motors.drive.leftFront",
		// true);
		// this.rightBack = buildTalonMotor(robotConfig, "motors.drive.rightBack",
		// true);
		// this.rightFront = buildTalonMotor(robotConfig, "motors.drive.rightFront",
		// true);

		this.LeftFrontSparkMax = new CANSparkMax(2, MotorType.kBrushless);
		this.RightFrontSparkMax = new CANSparkMax(1, MotorType.kBrushless);
		this.LeftBackSparkMax = new CANSparkMax(3, MotorType.kBrushless);
		this.RightBackSparkMax = new CANSparkMax(4, MotorType.kBrushless);

		this.rightFront = new MotorRamping(this.RightFrontSparkMax, false);
		this.leftFront = new MotorRamping(this.LeftFrontSparkMax, true);
		this.leftBack = new MotorRamping(this.LeftBackSparkMax, true);
		this.rightBack = new MotorRamping(this.RightBackSparkMax, false);

		this.leftBackTurner = buildTalonMotor(robotConfig, "motors.drive.leftBackTurner", false);
		this.leftFrontTurner = buildTalonMotor(robotConfig, "motors.drive.leftFrontTurner", false);
		this.rightBackTurner = buildTalonMotor(robotConfig, "motors.drive.rightBackTurner", false);
		this.rightFrontTurner = buildTalonMotor(robotConfig, "motors.drive.rightFrontTurner", false);

		this.lifter = buildTalonMotor(robotConfig, "motors.lift", false);
		this.intake = buildTalonMotor(robotConfig, "motors.intake", false);
		this.arms = buildTalonMotor(robotConfig, "motors.arms", false);
		this.kicker = buildTalonMotor(robotConfig, "motors.kicker", false);

		this.allMotors = new Motor[] { this.leftBack, this.leftFront, this.rightBack, this.rightFront,
				this.leftBackTurner, this.leftFrontTurner, this.rightBackTurner, this.rightFrontTurner, this.lifter };
		this.rampedMotors = new MotorRamping[] { (MotorRamping) this.leftBack, (MotorRamping) this.rightBack,
				(MotorRamping) this.leftFront, (MotorRamping) this.rightFront };
	}

	public void stopAll() {
		for (Motor motor : this.allMotors)
			motor.stop();
	}

	public void update() {
		for (MotorRamping motor : this.rampedMotors)
			motor.update();
	}

	private static Motor buildTalonMotor(Config robotConfig, String keyPrefix, boolean isRamping) {
		int port = 0;
		boolean inverted = false;
		try {
			port = robotConfig.getInt(keyPrefix + ".port");
			inverted = robotConfig.getBoolean(keyPrefix + ".inverted");
		} catch (NumberFormatException e) {
			throw new NumberFormatException(keyPrefix);
		}

		if (isRamping) {
			return new MotorRamping(new Talon(port), inverted);
		}

		return new MotorSimple(new Talon(port), inverted);
	}
}