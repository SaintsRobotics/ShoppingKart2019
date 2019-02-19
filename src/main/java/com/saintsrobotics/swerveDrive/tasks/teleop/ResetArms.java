package com.saintsrobotics.swerveDrive.tasks.teleop;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunContinuousTask;
import com.github.dozer.output.Motor;
import com.saintsrobotics.swerveDrive.input.AbsoluteEncoder;
import com.saintsrobotics.swerveDrive.tasks.teleop.ArmsControl;;

public class ResetArms extends RunContinuousTask {
    private BooleanSupplier trigger;
    private Motor motor;
    private AbsoluteEncoder encoder;
    private ArmsControl armsControl;

    /**
     * opens the arms while the trigger is pressed when trigger is released, the new
     * offset is slightly less than where the arms are currently
     * 
     */
    public ResetArms(BooleanSupplier trigger, Motor motor, AbsoluteEncoder encoder, ArmsControl armsControl) {
        this.trigger = trigger;
        this.motor = motor;
        this.encoder = encoder;
        this.armsControl = armsControl;
    }

    @Override
    protected void runForever() {
        wait.until(this.trigger);
        this.motor.set(0.4);
        wait.until(() -> !this.trigger.getAsBoolean());
        this.motor.set(0);
        armsControl.setOffset(this.encoder.getRotation());
    }
}