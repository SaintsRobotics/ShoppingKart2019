/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.saintsrobotics.swerveDrive.input;

import edu.wpi.first.wpilibj.Encoder;

public class DistanceEncoder extends Encoder {
  private double ticksPerUnit;
  
  public DistanceEncoder(int port1, int port2, double ticksPerUnit, boolean reversed) {
    super(port1, port2, reversed);
    this.ticksPerUnit = ticksPerUnit;
  }
  
  @Override
  public double getDistance() {
    return super.get() / this.ticksPerUnit;
  }
  
  @Override
  public double pidGet() {
    return super.get() / this.ticksPerUnit;
  }
}
