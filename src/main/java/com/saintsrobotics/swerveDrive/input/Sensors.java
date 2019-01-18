package com.saintsrobotics.swerveDrive.input;

import com.saintsrobotics.swerveDrive.util.TurnConfiguration;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DigitalInput;
import com.saintsrobotics.swerveDrive.input.AbsoluteEncoder;
public abstract class Sensors {
  public AbsoluteEncoder rightFrontEncoder;
  public AbsoluteEncoder leftFrontEncoder;
  public AbsoluteEncoder rightBackEncoder;
  public AbsoluteEncoder leftBackEncoder;

  public TurnConfiguration leftFrontTurnConfig;
  public TurnConfiguration leftBackTurnConfig;
  public TurnConfiguration rightFrontTurnConfig;
  public TurnConfiguration rightBackTurnConfig;
  
  public abstract void init();
}