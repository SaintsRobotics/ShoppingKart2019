package com.saintsrobotics.swerveDrive.input;

import com.saintsrobotics.swerveDrive.util.TurnConfiguration;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DigitalInput;

public class TestSensors extends Sensors {

	@Override
	public void init() {
		// this.leftFrontEncoder = new AbsoluteEncoder(2, -65, true);
		// this.rightFrontEncoder = new AbsoluteEncoder(1, -202, true);
		// this.leftBackEncoder = new AbsoluteEncoder(3, 10, true);
		// this.rightBackEncoder = new AbsoluteEncoder(0, -109, true);

		this.leftFrontEncoder = new AbsoluteEncoder(0, -109, true);
		this.rightFrontEncoder = new AbsoluteEncoder(3, 10, true);
		this.leftBackEncoder = new AbsoluteEncoder(1, -202, true);
		this.rightBackEncoder = new AbsoluteEncoder(2, -65, true);

		this.leftFrontTurnConfig = new TurnConfiguration(this.leftFrontEncoder);
		this.leftBackTurnConfig = new TurnConfiguration(this.leftBackEncoder);
		this.rightFrontTurnConfig = new TurnConfiguration(this.rightFrontEncoder);
		this.rightBackTurnConfig = new TurnConfiguration(this.rightBackEncoder);

		this.gyro = new ADXRS450_Gyro();

		this.liftEncoder = new DistanceEncoder(0, 1, 0, false);
		this.lifterDown = new DigitalInput(8);
		this.lifterUp = new DigitalInput(7);

		// SMASH
		this.intake = new DigitalInput(19);
				// these parameter values (port #s etc) may need to be changed
	}
}