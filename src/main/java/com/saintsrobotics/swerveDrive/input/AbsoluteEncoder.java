package com.saintsrobotics.swerveDrive.input;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class AbsoluteEncoder implements PIDSource {
//  this class is mainly a wrapper for accessing the current position of rotation that the motor is at
//  it uses the potentiometer values as voltage and correlates them to rotation degrees
  
  private AnalogInput analogIn;
  private double offset; //the offset from zero for each motor
  private PIDSourceType sourceType;
  private double voltageToDegrees;
  
  public AbsoluteEncoder(int channel, double offset, boolean isInverted) {
    analogIn = new AnalogInput(channel);
    this.offset = offset;
    this.sourceType = PIDSourceType.kDisplacement;
    
    if (isInverted) {
      this.offset += 360;
      this.voltageToDegrees = -72;
    } else {
      this.voltageToDegrees = 72;
    }
  }
  
  public double getRotation() { //in degrees
    return (this.analogIn.getVoltage()*this.voltageToDegrees + offset) % 360;
    
  }

  @Override
  public void setPIDSourceType(PIDSourceType pidSource) {
    this.sourceType = pidSource;
  }

  @Override
  public PIDSourceType getPIDSourceType() {
    return this.sourceType;
  }

  @Override
  public double pidGet() {
    return this.getRotation();
  }
  
}
