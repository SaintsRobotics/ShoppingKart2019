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
	private double offset;

	public DistanceEncoder(int port1, int port2, double ticksPerUnit, boolean reversed) {
		super(port1, port2, reversed);
		this.ticksPerUnit = ticksPerUnit;
		this.offset = 0;
	}

	@Override
	public double getDistance() {
		return super.get() / this.ticksPerUnit + this.offset;
	}

	@Override
	public double pidGet() {
		return this.getDistance();
	}

	public void setOffset(double n) {
		this.offset = n;
	}
}
