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

    public DetatchPanel(BooleanSupplier trigger, ArmsControl armsControl, LiftControl liftControl,
            DistanceEncoder encoder) {
        this.trigger = trigger;
        this.armsControl = armsControl;
        this.liftControl = liftControl;
        this.encoder = encoder;
    }

    @Override
    protected void runForever() {
        wait.until(this.trigger);
        this.liftControl.setHeight(this.encoder.getDistance() - 5); // remove magic number
        this.armsControl.setTarget(-208); // remove magic number
    }
}