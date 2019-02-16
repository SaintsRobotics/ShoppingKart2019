package com.saintsrobotics.swerveDrive.input;

import com.github.dozer.input.OI.XboxInput;

public class OI {
	public XboxInput xboxInput = new XboxInput(0);
	public XboxInput oppInput = new XboxInput(1);

	public OI() {
		this.xboxInput.init();
		this.oppInput.init();
	}
}
