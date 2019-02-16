package com.saintsrobotics.swerveDrive.input;

import com.github.dozer.input.sensors.Potentiometer;
import com.saintsrobotics.swerveDrive.util.TurnConfiguration;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DigitalInput;

public abstract class Sensors {
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

	public AbsoluteEncoder arms;

	public abstract void init();
}