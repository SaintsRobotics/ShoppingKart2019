package com.saintsrobotics.shoppingkart.drive;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.Preferences;

public class TurnConfiguration {
	public double forwardDistanceKP;
	public double forwardDistanceKI;
	public double forwardDistanceKD;
	public double forwardHeadingKP;
	public double forwardHeadingKI;
	public double forwardHeadingKD;
	public double forwardHeadingTolerance;
	public double forwardDistanceTolerance;
	public PIDSource encoder;

	public TurnConfiguration(PIDSource encoder) {
		Preferences prefs = Preferences.getInstance();
		this.forwardHeadingKP = prefs.getDouble("forwardHeadingKP", 0.018);
		this.forwardHeadingKI = prefs.getDouble("forwardHeadingKI", 0.0); // default not in SmartDashboard
		this.forwardHeadingKD = prefs.getDouble("forwardHeadingKD", 0.0);

		this.forwardHeadingTolerance = 2.5;
		this.forwardDistanceTolerance = 2;
		this.encoder = encoder;
	}
}