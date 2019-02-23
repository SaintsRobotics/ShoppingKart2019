/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.saintsrobotics.shoppingkart.lift;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunContinuousTask;

public class ToHeight extends RunContinuousTask {
    private BooleanSupplier trigger;
    private LiftControl liftControl;
    private double targetHeight;

    /**
     * 
     * @param trigger      what triggers the task
     * @param liftInput    LiftInput class
     * @param targetHeight lift height
     */
    public ToHeight(BooleanSupplier trigger, LiftControl liftControl, double targetHeight) {
        this.trigger = trigger;
        this.liftControl = liftControl;
        this.targetHeight = targetHeight;
    }

    @Override
    public void runForever() {
        while (true) {
            wait.until(this.trigger);
            this.liftControl.setHeight(this.targetHeight);
        }
    }
}