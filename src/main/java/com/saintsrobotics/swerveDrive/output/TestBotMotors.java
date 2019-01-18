package com.saintsrobotics.swerveDrive.output;

import com.github.dozer.output.Motor;
import com.github.dozer.output.MotorGroup;
import com.github.dozer.output.MotorSimple;
import edu.wpi.first.wpilibj.Talon;

public class TestBotMotors extends RobotMotors {
  
  @Override
  public void init() {
    this.leftBack = new MotorRamping(new Talon(3), true);
    this.leftFront = new MotorRamping(new Talon(1), true);
    this.rightBack = new MotorRamping(new Talon(5), false);
    this.rightFront = new MotorRamping(new Talon(7), false);
    
    this.leftDrive = new MotorGroup(this.leftBack, this.leftFront);
    this.rightDrive = new MotorGroup(this.rightBack, this.rightFront);
    
    this.leftBackTurner = new MotorSimple(new Talon(4), true);
    this.leftFrontTurner = new MotorSimple(new Talon(2), true);
    this.rightBackTurner = new MotorSimple(new Talon(6), true);
    this.rightFrontTurner = new MotorSimple(new Talon(8), true);
    
//    this.turners = new MotorGroup(this.leftBackTurner, this.leftFrontTurner, this.rightBackTurner, this.rightFrontTurner);
//    this.drive = new MotorGroup(this.leftBack, this.leftFront, this.rightBack, this.rightFront);
   
    this.allMotors = new Motor[] {
        this.leftDrive,
        this.rightDrive,
        this.leftBack,
        this.leftFront,
        this.rightBack,
        this.rightFront,
        this.leftBackTurner,
        this.leftFrontTurner,
        this.rightBackTurner,
        this.rightFrontTurner,
//        this.turners,
//        this.drive,
   };
   this.rampedMotors = new MotorRamping[] {
       (MotorRamping) this.leftBack,
       (MotorRamping) this.rightBack,
       (MotorRamping) this.leftFront,
       (MotorRamping) this.rightFront,
//       (MotorRamping) this.leftBackTurner,
   };
  }
}
