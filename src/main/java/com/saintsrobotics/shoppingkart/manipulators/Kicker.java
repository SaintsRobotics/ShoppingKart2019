package com.saintsrobotics.shoppingkart.manipulators;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunContinuousTask;
import com.github.dozer.output.Motor;
import com.saintsrobotics.shoppingkart.util.AbsoluteEncoder;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Kicker extends RunContinuousTask {
    private BooleanSupplier trigger;
    private Motor kicker;
    private AbsoluteEncoder encoder;
    private double upperBound;
    private double lowerBound;
    private double backPass;

    /**
     * @param trigger    digital button on a controller
     * @param kicker     motor that gets spun
     * @param encoder    assumes that the encoder value increases as the kicker is
     *                   spun up/out. because of the upper and lower bounds, the
     *                   encoder offset doesn't need to be calibrated
     * @param upperBound the value that the kicker stops at when kicking
     * @param lowerBound the rest position (zero)
     * @param backPass   the position to which the kicker goes before it kicks
     */
    public Kicker(BooleanSupplier trigger, Motor kicker, AbsoluteEncoder encoder, double upperBound, double lowerBound,
            double backPass) {
        this.trigger = trigger;
        this.kicker = kicker;
        this.encoder = encoder;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
        this.backPass = backPass;
    }

    @Override
    // Change the encoder value for Kicker
    protected void runForever() {
        wait.until(this.trigger);
        // this.kicker.set(-0.1);
        // SmartDashboard.putString("kicker status", "backpassing");

        // wait.until(() -> this.encoder.getRotation() <= this.backPass);
        this.kicker.set(1);
        SmartDashboard.putString("kicker status", "kicking");

        wait.until(() -> this.encoder.getRotation() >= this.upperBound);
        this.kicker.set(-0.2);
        SmartDashboard.putString("kicker status", "reversing");

        wait.until(() -> this.encoder.getRotation() <= this.lowerBound);
        this.kicker.set(0);
        SmartDashboard.putString("kicker status", "standing by");
    }
}