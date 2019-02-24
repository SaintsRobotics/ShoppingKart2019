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

public class LiftInput extends RunEachFrameTask {
    private XboxInput xboxInput;
    private BooleanSupplier resetTrigger;
    private boolean isResetting;
    private LiftControl liftControl;
    private final double XBOX_MULTIPLIER = 1;

    /**
     * 
     * @param xboxInput controller
     * @param offset    height of the arms at the lower limit switch
     */
    public LiftInput(XboxInput xboxInput, BooleanSupplier resetTrigger, LiftControl liftControl) {
        this.xboxInput = xboxInput;
        this.resetTrigger = resetTrigger;
        this.liftControl = liftControl;
    }

    private double readXbox() {
        // Robot.instance.flags.liftEncoderValue = liftEncoder.getDistance();

        return (this.xboxInput.rightTrigger() - this.xboxInput.leftTrigger()) * this.XBOX_MULTIPLIER;
    }

    @Override
    protected void runEachFrame() {
        double speed = this.readXbox();
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

        this.liftControl.setControlSpeed(speed);
    }
}