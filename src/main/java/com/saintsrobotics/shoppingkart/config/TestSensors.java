package com.saintsrobotics.shoppingkart.config;

import com.saintsrobotics.shoppingkart.drive.TurnConfiguration;
import com.saintsrobotics.shoppingkart.util.AbsoluteEncoder;
import com.saintsrobotics.shoppingkart.util.DistanceEncoder;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DigitalInput;

public class TestSensors extends Sensors {

	@Override
	public void init() {
		this.leftFrontEncoder = new AbsoluteEncoder(1, -82, true);
		this.rightFrontEncoder = new AbsoluteEncoder(0, 144, true);
		this.leftBackEncoder = new AbsoluteEncoder(2, -56, true);
		this.rightBackEncoder = new AbsoluteEncoder(3, 13, true);

		this.leftFrontTurnConfig = new TurnConfiguration(this.leftFrontEncoder);
		this.leftBackTurnConfig = new TurnConfiguration(this.leftBackEncoder);
		this.rightFrontTurnConfig = new TurnConfiguration(this.rightFrontEncoder);
		this.rightBackTurnConfig = new TurnConfiguration(this.rightBackEncoder);

		this.gyro = new ADXRS450_Gyro();

		this.liftEncoder = new DistanceEncoder(2, 3, 146.58, true);
		this.lifterDown = new DigitalInput(1);
		this.lifterUp = new DigitalInput(0);

		this.kicker = new AbsoluteEncoder(4, 0, true);

		this.arms = new AbsoluteEncoder(5, 0, false);

	}
}