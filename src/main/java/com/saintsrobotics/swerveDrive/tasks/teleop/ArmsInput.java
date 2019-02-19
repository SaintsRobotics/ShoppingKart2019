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
    private BooleanSupplier motorPause;

    private Motor motor;

    private double targetPosition;

    private static double offset;
    private static double fullInPosition = 0;
    private static double engagedPosition = 48;
    private static double fullOutPosition = 205;

    private PIDController pidController;
    private double pidOutput;

    public ArmsInput(BooleanSupplier fullIn, BooleanSupplier engaged, BooleanSupplier fullOut,
            BooleanSupplier motorPause, AbsoluteEncoder encoder, Motor motor) {
        this.fullIn = fullIn;
        this.engaged = engaged;
        this.fullOut = fullOut;
        this.motorPause = motorPause;

        this.motor = motor;

        this.targetPosition = fullInPosition;

        this.pidController = new PIDController(0.03, 0.0, 0.0, encoder, (output) -> this.pidOutput = output);

        this.pidController.setAbsoluteTolerance(2.0);
        this.pidController.setOutputRange(-0.75, 0.75);
        this.pidController.setInputRange(0, 360);
        this.pidController.reset();
        this.pidController.enable();
    }

    public void setOffset(double n) {
        offset = n;
        fullInPosition += offset;
        engagedPosition += offset;
        fullOutPosition += offset;
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
        if (motorPause.getAsBoolean()) {
            speed = 0;
            this.targetPosition = fullInPosition;
        }
        SmartDashboard.putNumber("arms pid output", this.pidOutput);

        this.motor.set(speed);
    }

}
