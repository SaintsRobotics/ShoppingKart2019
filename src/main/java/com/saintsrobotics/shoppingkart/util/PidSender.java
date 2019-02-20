package com.saintsrobotics.shoppingkart.util;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class PidSender implements PIDSource {

	private PIDSourceType pidSourceType = PIDSourceType.kDisplacement;
	private double value = 0;

	@Override
	public PIDSourceType getPIDSourceType() {
		return this.pidSourceType;
	}

	@Override
	public double pidGet() {
		return this.value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		this.pidSourceType = pidSource;
	}

}