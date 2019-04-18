/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.saintsrobotics.shoppingkart.lift;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.input.OI.XboxInput;
import com.saintsrobotics.shoppingkart.config.OperatorBoard;
import com.saintsrobotics.shoppingkart.util.DistanceEncoder;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftInput extends RunEachFrameTask {
    private OperatorBoard xboxInput;
    private DistanceEncoder encoder;
    private double upperThrottle;
    private double lowerThrottle;
    private BooleanSupplier resetTrigger;
    private boolean isResetting;
    private LiftControl liftControl;
    private final double XBOX_MULTIPLIER = 1;

    /**
     * 
     * @param xboxInput controller
     */
    public LiftInput(OperatorBoard xboxInput, DistanceEncoder encoder, double upperThrottle, double lowerThrottle,
            BooleanSupplier resetTrigger, LiftControl liftControl) {
        this.xboxInput = xboxInput;
        this.encoder = encoder;
        this.upperThrottle = upperThrottle;
        this.lowerThrottle = lowerThrottle;
        this.resetTrigger = resetTrigger;
        this.liftControl = liftControl;
    }

    private double readXbox() {
        // Robot.instance.flags.liftEncoderValue = liftEncoder.getDistance();

        return xboxInput.liftY() * this.XBOX_MULTIPLIER;
    }

    @Override
    protected void runEachFrame() {
        double speed = this.readXbox();

        // control speed throttle
        if ((this.encoder.getDistance() <= this.lowerThrottle && speed < 0)
                || (this.encoder.getDistance() >= this.upperThrottle && speed > 0)) {
            speed *= .33;
        }

        if (this.resetTrigger.getAsBoolean()) {
            this.isResetting = true;
        }

        if (speed != 0) {
            this.isResetting = false;
        }

        if (this.isResetting) {
            speed = -0.4;
            if (this.liftControl.isAtBottom()) {
                speed = 0;
                this.isResetting = false;
            }
        }

        if (xboxInput.lowerLiftBack() | xboxInput.lowerLift()) {
            speed = 0;
        }
        SmartDashboard.putNumber("lift input speed", speed);
        this.liftControl.setControlSpeed(speed);
    }
}