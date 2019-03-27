/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.saintsrobotics.shoppingkart.lift;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.output.Motor;
import com.saintsrobotics.shoppingkart.config.PidConfig;
import com.saintsrobotics.shoppingkart.util.DistanceEncoder;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftControl extends RunEachFrameTask {
    private Motor lifter;
    private double controlSpeed;
    private DistanceEncoder encoder;
    private DigitalInput lifterUp;
    private DigitalInput lifterDown;
    private boolean isLifting;

    private double filteredCurrent = 0;
    private final double LPF_PROPORTION = 0.025;

    private double frameCount = 0; // # frames it's not been able to move
    private PowerDistributionPanel pewdiepie;

    private PIDController pidController;
    private double pidOutput;

    /**
     * 
     * @param upperThrottle location at which the motor speed is scaled down (top)
     * @param lowerThrottle location at which the motor speed is scaled down
     *                      (bottom)
     */
    public LiftControl(Motor lifter, DistanceEncoder encoder, DigitalInput lifterUp, DigitalInput lifterDown,
            PidConfig pidConfig) {
        this.lifter = lifter;
        this.encoder = encoder;
        this.lifterUp = lifterUp;
        this.lifterDown = lifterDown;

        this.pidController = new PIDController(pidConfig.kP, pidConfig.kI, pidConfig.kD, this.encoder,
                (output) -> this.pidOutput = output);
        this.pidController.setAbsoluteTolerance(pidConfig.tolerance);
        this.pidController.setOutputRange(-01, 01);
        this.pidController.reset();
        this.pidController.enable();
        this.pidController.setSetpoint(encoder.getDistance());

        this.pewdiepie = new PowerDistributionPanel();
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

    /**
     * "stuck" means that we've been feeding power to the motor, but based on
     * encoder inut, the motor hasn't been moving note that the frame count
     * increases each time the method is called
     * 
     * @return whether or not the lift can move
     */
    public boolean isStuck() {
        if (this.filteredCurrent > 20) {
            this.frameCount++;
        } else {
            this.frameCount = 0;
        }

        return this.frameCount > 10;
    }

    @Override
    protected void runEachFrame() {
        double liftInput = this.pidOutput;

        if (this.controlSpeed != 0.0) {
            liftInput = this.controlSpeed;
            this.isLifting = true;
        } else if (this.isLifting) {
            this.lifter.set(0);
            wait.forSeconds(0.01); // you're not lifting anyway
            // so it makes sure the lift stops so the setpoint can be properly set
            this.pidController.setSetpoint(this.encoder.getDistance());
            this.isLifting = false;
        }

        /**
         * the limit switches are inverted (yay.) if there's a magnet nearby, they
         * return false set lift encoder max cuz build is too lazy -- efficient -- to
         * move the limit switch
         */
        if (((!this.lifterUp.get() || this.encoder.getDistance() > 79) && liftInput > 0)
                || ((!this.lifterDown.get() || this.encoder.getDistance() < 17) && liftInput < 0)) {
            liftInput = 0;
        }

        if (this.isStuck()) {
            DriverStation.reportError("lift is stuck", false);
            this.lifter.set(0);
            wait.forSeconds(1);
            this.pidController.setSetpoint(this.encoder.getDistance());
            liftInput *= .25;
        }

        this.filteredCurrent = this.LPF_PROPORTION * this.pewdiepie.getCurrent(3)
                + (1 - this.LPF_PROPORTION) * this.filteredCurrent;

        if (!this.lifterDown.get()) {
            this.encoder.reset();
        }

        // SmartDashboard.putNumber("liftInput", liftInput);
        // SmartDashboard.putNumber("lift frame count", this.frameCount);

        SmartDashboard.putNumber("lift pid setpoint", this.pidController.getSetpoint());
        // SmartDashboard.putNumber("lift pid output", this.pidOutput);
        // SmartDashboard.putNumber("lift current", this.pewdiepie.getCurrent(3));
        // SmartDashboard.putNumber("filtered lift current", this.filteredCurrent);

        this.lifter.set(liftInput);
    }
}