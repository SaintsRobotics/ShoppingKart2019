package com.saintsrobotics.swerveDrive.input;

import com.saintsrobotics.swerveDrive.Robot;
import com.saintsrobotics.swerveDrive.util.TurnConfiguration;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DigitalInput;
import com.saintsrobotics.swerveDrive.input.AbsoluteEncoder;

public class TestSensors extends Sensors {

  @Override
  public void init() {
    this.leftFrontEncoder = new AbsoluteEncoder(2, -65, true);
    this.rightFrontEncoder = new AbsoluteEncoder(1, -202, true);
    this.leftBackEncoder = new AbsoluteEncoder(3, 10, true);
    this.rightBackEncoder = new AbsoluteEncoder(0, -109, true);
    
    this.leftFrontTurnConfig = new TurnConfiguration(this.leftFrontEncoder);
    this.leftBackTurnConfig = new TurnConfiguration(this.leftBackEncoder);
    this.rightFrontTurnConfig = new TurnConfiguration(this.rightFrontEncoder);
    this.rightBackTurnConfig = new TurnConfiguration(this.rightBackEncoder);
  }
}
