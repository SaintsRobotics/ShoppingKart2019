package com.saintsrobotics.swerveDrive.util;

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
    //this.forwardDistanceKP =  prefs.getDouble("forwardDistanceKP", 0.35);
    //this.forwardDistanceKI = prefs.getDouble("forwardDistanceKI",0.00001);
    //this.forwardDistanceKD = prefs.getDouble("forwardDistanceKD", 0.476); //default not in SmartDashboard
    this.forwardHeadingKP = prefs.getDouble("forwardHeadingKP", 0.02);
    this.forwardHeadingKI = prefs.getDouble("forwardHeadingKI", 0.1); //default not in SmartDashboard
    this.forwardHeadingKD = prefs.getDouble("forwardHeadingKD", 0.0);

    this.forwardHeadingTolerance = 2.5;
    this.forwardDistanceTolerance = 2;
    this.encoder = encoder;
  }
  

}
