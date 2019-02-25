/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.saintsrobotics.shoppingkart.lift;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.output.Motor;
import com.saintsrobotics.shoppingkart.util.DistanceEncoder;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftControl extends RunEachFrameTask {
    private Motor lifter;
    private double controlSpeed;
    private DistanceEncoder encoder;
    private DigitalInput lifterUp;
    private DigitalInput lifterDown;
    private boolean isLifting;

    private PIDController pidController;
    private double pidOutput;

    public LiftControl(Motor lifter, DistanceEncoder encoder, DigitalInput lifterUp, DigitalInput lifterDown) {
        this.lifter = lifter;
        this.encoder = encoder;
        this.lifterUp = lifterUp;
        this.lifterDown = lifterDown;

        encoder.setOffset(17);

        this.pidController = new PIDController(0.1, 0, 0, this.encoder, (output) -> this.pidOutput = output);
        // this.picController.setAbsoluteTolerance();
        this.pidController.setOutputRange(-1, 1);
        this.pidController.reset();
        this.pidController.enable();
        this.pidController.setSetpoint(encoder.getDistance());
    }

    /**
     * used by ToHeight
     * 
     * @param height target height
     */
    public void setHeight(double height) {
        this.pidController.setSetpoint(height);
    }

    /**
     * used by LiftInput to manually control the lift
     * 
     * @param speed positive is up; negative is down
     */
    public void setControlSpeed(double speed) {
        this.controlSpeed = speed;
    }

    /**
     * 
     * @return whether or not the lift is at its lower limit
     */
    public boolean isAtBottom() {
        return !this.lifterDown.get();
    }

    @Override
    protected void runEachFrame() {
        double liftInput = this.pidOutput;

        if (this.controlSpeed != 0.0) {
            liftInput = this.controlSpeed;
            this.isLifting = true;
        } else if (this.isLifting) {
            this.pidController.setSetpoint(this.encoder.getDistance());
            this.isLifting = false;
        }

        // the limit switches are inverted (yay.)
        // if there's a magnet nearby, they return false
        if ((!this.lifterUp.get() && liftInput > 0) || (!this.lifterDown.get() && liftInput < 0)) {
            liftInput = 0;
        }

        if (!this.lifterDown.get()) {
            this.encoder.reset();
        }

        // SmartDashboard.putNumber("liftInput", liftInput);
        // SmartDashboard.putNumber("lift encoder", this.encoder.getDistance());
        // SmartDashboard.putBoolean("lifterUp", !this.lifterUp.get());
        // SmartDashboard.putBoolean("lifterDown", !this.lifterDown.get());
        // SmartDashboard.putBoolean("lifting", this.isLifting);

        SmartDashboard.putNumber("pid setpoint", this.pidController.getSetpoint());
        // SmartDashboard.putNumber("pid output", this.pidOutput);
        // SmartDashboard.putNumber("lift input", liftInput);

        this.lifter.set(liftInput);
    }
}