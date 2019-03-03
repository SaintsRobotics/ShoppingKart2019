package com.saintsrobotics.shoppingkart.config;

import com.github.dozer.input.OI.XboxInput;

public class OI {
	public XboxInput xboxInput = new XboxInput(0);
	public OperatorBoard oppInput = new OperatorBoard(1);

	public OI() {
		this.xboxInput.init();
		this.oppInput.init();
	}
}
