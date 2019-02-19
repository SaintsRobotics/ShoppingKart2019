package com.saintsrobotics.swerveDrive.tasks.teleop;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.output.Motor;
import com.saintsrobotics.swerveDrive.Robot;
import com.saintsrobotics.swerveDrive.input.AbsoluteEncoder;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ArmsInput extends RunEachFrameTask {
    private BooleanSupplier fullIn;
    private BooleanSupplier engaged;
    private BooleanSupplier fullOut;
    private BooleanSupplier estop;

    private Motor motor;

    private double targetPosition;

    private static final double fullInPosition = 79;
    private static final double engagedPosition = 111;
    private static final double fullOutPosition = 259;

    private PIDController pidController;
    private double pidOutput;

    public ArmsInput(BooleanSupplier fullIn, BooleanSupplier engaged, BooleanSupplier fullOut, BooleanSupplier estop,
            AbsoluteEncoder encoder, Motor motor) {
        this.fullIn = fullIn;
        this.engaged = engaged;
        this.fullOut = fullOut;
        this.estop = estop;

        this.motor = motor;

        this.targetPosition = this.fullInPosition;

        this.pidController = new PIDController(0.03, 0.0, 0.0, encoder, (output) -> this.pidOutput = output);

        this.pidController.setAbsoluteTolerance(2.0);
        this.pidController.setOutputRange(-0.75, 0.75);
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
        double speed = this.pidOutput;
        if (estop.getAsBoolean()) {
            speed = 0;
            this.targetPosition = fullInPosition;
        }

        this.motor.set(speed);
    }

}
