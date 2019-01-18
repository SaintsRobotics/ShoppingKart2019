package com.saintsrobotics.swerveDrive.util;

import edu.wpi.first.wpilibj.PIDOutput;

public class PIDReceiver implements PIDOutput {

  private volatile double output;

  @Override
  public void pidWrite(double output) {
    this.output = output;
  }

  public double getOutput() {
    return this.output;
  }

}
