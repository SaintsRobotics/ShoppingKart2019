package com.saintsrobotics.swerveDrive.output;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.github.dozer.output.Motor;
import com.github.dozer.output.MotorRamping;

public abstract class Motors {
  private List<MotorRamping> rampedMotors;
  private List<Motor> motors;

  public Motors() {
  }

  public abstract void init();

  public void stopAll() {
    for (Motor motor : this.motors)
      motor.stop();
  }

  public void update() {
    for (MotorRamping motor : this.rampedMotors)
      motor.update();
  }
}
