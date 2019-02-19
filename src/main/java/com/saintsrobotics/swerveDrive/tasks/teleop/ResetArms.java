package com.saintsrobotics.swerveDrive.tasks.teleop;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunContinuousTask;
import com.github.dozer.output.Motor;
import com.saintsrobotics.swerveDrive.input.AbsoluteEncoder;
import com.saintsrobotics.swerveDrive.tasks.teleop.ArmsControl;;

public class ResetArms extends RunContinuousTask {
    private BooleanSupplier trigger;
    private AbsoluteEncoder encoder;
    private ArmsControl armsControl;

    /**
     * opens the arms while the trigger is pressed when trigger is released, the new
     * offset is slightly less than where the arms are currently
     * 
     */
    public ResetArms(BooleanSupplier trigger, AbsoluteEncoder encoder, ArmsControl armsControl) {
        this.trigger = trigger;
        this.encoder = encoder;
        this.armsControl = armsControl;
    }

    @Override
    protected void runForever() {
        wait.until(this.trigger);
        this.armsControl.setResetSpeed(0.5);
        wait.until(() -> !this.trigger.getAsBoolean());
        this.armsControl.setResetSpeed(0);
        armsControl.setOffset(this.encoder.getRotation());
    }
}