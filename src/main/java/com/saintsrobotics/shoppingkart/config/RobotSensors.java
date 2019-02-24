package com.saintsrobotics.shoppingkart.config;

import com.saintsrobotics.shoppingkart.drive.TurnConfiguration;
import com.saintsrobotics.shoppingkart.util.AbsoluteEncoder;
import com.saintsrobotics.shoppingkart.util.DistanceEncoder;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DigitalInput;

public abstract class RobotSensors {
	public AbsoluteEncoder rightFrontEncoder;
	public AbsoluteEncoder leftFrontEncoder;
	public AbsoluteEncoder rightBackEncoder;
	public AbsoluteEncoder leftBackEncoder;

	public TurnConfiguration leftFrontTurnConfig;
	public TurnConfiguration leftBackTurnConfig;
	public TurnConfiguration rightFrontTurnConfig;
	public TurnConfiguration rightBackTurnConfig;

	public ADXRS450_Gyro gyro;

	public DistanceEncoder liftEncoder;
	public DigitalInput lifterUp;
	public DigitalInput lifterDown;
	public AbsoluteEncoder arms;

	public AbsoluteEncoder kicker;

	public abstract void init();
}