package com.saintsrobotics.shoppingkart.arms;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunContinuousTask;

public class ArmsTarget extends RunContinuousTask {
    private BooleanSupplier trigger;
    private double positionOffset;
    private ArmsControl armsControl;

    /**
     * 
     * @param offset the number of degrees the target position is from the hard
     *               stop; should be a negative #
     */
    public ArmsTarget(BooleanSupplier trigger, double offset, ArmsControl armsControl) {
        this.trigger = trigger;
        this.positionOffset = offset;
        this.armsControl = armsControl;
    }

    @Override
    protected void runForever() {
        wait.until(this.trigger);
        this.armsControl.setTarget(this.positionOffset);
    }
}