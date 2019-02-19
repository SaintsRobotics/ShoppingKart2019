package com.saintsrobotics.swerveDrive.tasks;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunContinuousTask;
import com.saintsrobotics.swerveDrive.input.AbsoluteEncoder;
import com.saintsrobotics.swerveDrive.tasks.teleop.ArmsInput;

public class ResetArmsEncoder extends RunContinuousTask {
    private BooleanSupplier trigger;
    private AbsoluteEncoder encoder;
    private ArmsInput armsInput;

    public ResetArmsEncoder(BooleanSupplier trigger, AbsoluteEncoder encoder, ArmsInput armsInput) {
        this.trigger = trigger;
        this.encoder = encoder;
        this.armsInput = armsInput;
    }

    @Override
    protected void runForever() {
        wait.until(this.trigger);
        this.armsInput.setOffset(this.encoder.getRotation());
    }
}