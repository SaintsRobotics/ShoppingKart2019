package com.saintsrobotics.swerveDrive.tasks.teleop;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.output.Motor;
import com.saintsrobotics.swerveDrive.Robot;
import com.saintsrobotics.swerveDrive.input.AbsoluteEncoder;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ArmsTask extends RunEachFrameTask {
    private BooleanSupplier fullIn;
    private BooleanSupplier engaged;
    private BooleanSupplier fullOut;

    private double targetPosition;

    // Find actual values later
    private static final double fullInPosition = 300;
    private static final double engagedPosition = 265;
    private static final double fullOutPosition = 57;


    private PIDController pidController;

    public ArmsTask(BooleanSupplier fullIn, BooleanSupplier engaged, BooleanSupplier fullOut, AbsoluteEncoder encoder,
            Motor motor) {
        this.fullIn = fullIn;
        this.engaged = engaged;
        this.fullOut = fullOut;

        this.targetPosition = fullInPosition;

        this.pidController = new PIDController(0.015, 0.0, 0.0, encoder, (output) -> motor.set(output));

        this.pidController.setSetpoint(this.targetPosition);
        this.pidController.setAbsoluteTolerance(2.0);
        this.pidController.setOutputRange(-0.5, 0.5);
        this.pidController.setInputRange(0, 360);
        this.pidController.reset();
        this.pidController.enable();
    }

    @Override
    protected void runEachFrame() {
        if (fullIn.getAsBoolean() && !engaged.getAsBoolean() && !fullOut.getAsBoolean())
            targetPosition = fullInPosition;
        else if (!fullIn.getAsBoolean() && engaged.getAsBoolean() && !fullOut.getAsBoolean())
            targetPosition = engagedPosition;
        else if (!fullIn.getAsBoolean() && !engaged.getAsBoolean() && fullOut.getAsBoolean())
            targetPosition = fullOutPosition;

        this.pidController.setSetpoint(targetPosition);
    }

}
