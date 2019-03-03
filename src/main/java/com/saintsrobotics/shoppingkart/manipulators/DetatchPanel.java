package com.saintsrobotics.shoppingkart.manipulators;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunContinuousTask;
import com.saintsrobotics.shoppingkart.arms.ArmsControl;
import com.saintsrobotics.shoppingkart.lift.LiftControl;
import com.saintsrobotics.shoppingkart.util.DistanceEncoder;

public class DetatchPanel extends RunContinuousTask {
    private BooleanSupplier trigger;
    private ArmsControl armsControl;
    private LiftControl liftControl;
    private DistanceEncoder encoder;
    private double time;
    private double rest;

    /**
     * 
     * @param time the amount of seconds it waits to signal the arms
     * @param rest the position the arms go to after the lift lowers (full in)
     */
    public DetatchPanel(BooleanSupplier trigger, ArmsControl armsControl, LiftControl liftControl,
            DistanceEncoder encoder, double time, double rest) {
        this.trigger = trigger;
        this.armsControl = armsControl;
        this.liftControl = liftControl;
        this.encoder = encoder;
        this.time = time;
        this.rest = rest;
    }

    @Override
    protected void runForever() {
        wait.until(this.trigger);
        this.liftControl.setHeight(this.encoder.getDistance() - 1);
        wait.forSeconds(this.time);
        this.armsControl.setTarget(this.rest);
    }
}