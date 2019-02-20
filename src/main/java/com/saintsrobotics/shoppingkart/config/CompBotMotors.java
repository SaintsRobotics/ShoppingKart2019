package com.saintsrobotics.shoppingkart.config;

import com.github.dozer.output.Motor;
import com.github.dozer.output.MotorSimple;
import com.saintsrobotics.shoppingkart.config.RobotMotors;
import com.saintsrobotics.shoppingkart.util.MotorRamping;

import edu.wpi.first.wpilibj.Talon;

public class CompBotMotors extends RobotMotors {

    @Override
    public void init() {
        this.leftBack = new MotorRamping(new Talon(2), true);
        this.leftFront = new MotorRamping(new Talon(1), true);
        this.rightBack = new MotorRamping(new Talon(3), false);
        this.rightFront = new MotorRamping(new Talon(0), false);

        this.leftBackTurner = new MotorSimple(new Talon(6), true);
        this.leftFrontTurner = new MotorSimple(new Talon(5), true);
        this.rightBackTurner = new MotorSimple(new Talon(7), true);
        this.rightFrontTurner = new MotorSimple(new Talon(4), true);

        this.lifter = new MotorSimple(new Talon(8), false);
        this.intake = new MotorSimple(new Talon(9), true);
        this.arms = new MotorSimple(new Talon(11), false);
        this.kicker = new MotorSimple(new Talon(10), true);

        // this.turners = new MotorGroup(this.leftBackTurner, this.leftFrontTurner,
        // this.rightBackTurner, this.rightFrontTurner);
        // this.drive = new MotorGroup(this.leftBack, this.leftFront, this.rightBack,
        // this.rightFront);

        this.allMotors = new Motor[] { /* this.leftDrive, this.rightDrive, */ this.leftBack, this.leftFront,
                this.rightBack, this.rightFront, this.leftBackTurner, this.leftFrontTurner, this.rightBackTurner,
                this.rightFrontTurner, this.lifter,
                // this.turners,
                // this.drive,
        };
        this.rampedMotors = new MotorRamping[] { (MotorRamping) this.leftBack, (MotorRamping) this.rightBack,
                (MotorRamping) this.leftFront, (MotorRamping) this.rightFront,
                // (MotorRamping) this.leftBackTurner,
        };
    }
}