package com.saintsrobotics.shoppingkart.tests;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.saintsrobotics.shoppingkart.config.OperatorBoard;
import com.saintsrobotics.shoppingkart.util.AbsoluteEncoder;
import com.saintsrobotics.shoppingkart.util.MotorRamping;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ClimbTest extends RunEachFrameTask {
    private MotorRamping motor;
    private AbsoluteEncoder encoder;
    private OperatorBoard board;
    private BooleanSupplier engageTrigger;

    public ClimbTest(MotorRamping motor, OperatorBoard board, BooleanSupplier engageTrigger) {
        this.motor = motor;
        // this.encoder = encoder;
        this.board = board;
        this.engageTrigger = engageTrigger;
    }

    @Override
    protected void runEachFrame() {
        double x = -this.board.liftX();
        if (this.engageTrigger.getAsBoolean()) {
            this.motor.set(x);
        }

        SmartDashboard.putNumber("control input", x);
        SmartDashboard.putNumber("test climb motor", this.motor.get());
        // SmartDashboard.putNumber("test climb encoder", this.encoder.getRotation());
        // this.motor.update();
    }
}