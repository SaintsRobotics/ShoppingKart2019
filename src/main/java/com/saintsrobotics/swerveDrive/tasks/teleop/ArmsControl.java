package com.saintsrobotics.swerveDrive.tasks.teleop;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.output.Motor;
import com.saintsrobotics.swerveDrive.Robot;
import com.saintsrobotics.swerveDrive.input.AbsoluteEncoder;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ArmsControl extends RunEachFrameTask {
    private BooleanSupplier fullIn;
    private BooleanSupplier engaged;
    private BooleanSupplier fullOut;
    private BooleanSupplier motorPause;

    private double resetSpeed;

    private Motor motor;

    private double targetPosition;

    private static double offset;
    private static final double fullInOffset = -201;
    private static final double engagedOffset = -153;
    private static final double fullOutOffset = -10;
    private static double fullInPosition;
    private static double engagedPosition;
    private static double fullOutPosition;

    private PIDController pidController;
    private double pidOutput;

    /**
     * @param encoder assumes that as the arms spin in, the encoder decreases
     */
    public ArmsControl(BooleanSupplier fullIn, BooleanSupplier engaged, BooleanSupplier fullOut,
            BooleanSupplier motorPause, AbsoluteEncoder encoder, Motor motor) {
        this.fullIn = fullIn;
        this.engaged = engaged;
        this.fullOut = fullOut;
        this.motorPause = motorPause;
        setOffset(331); // where the hard stop is

        this.motor = motor;

        this.targetPosition = fullInPosition;

        this.pidController = new PIDController(0.03, 0.0, 0.0, encoder, (output) -> this.pidOutput = output);

        this.pidController.setAbsoluteTolerance(2.0);
        this.pidController.setOutputRange(-0.75, 0.75);
        this.pidController.setInputRange(0, 360);
        this.pidController.reset();
        this.pidController.enable();
    }

    /**
     * 
     * @param n where the encoders are is the location of the hard stop (not full
     *          out)
     */
    public void setOffset(double n) {
        offset = n;
        fullInPosition = offset + fullInOffset;
        engagedPosition = offset + engagedOffset;
        fullOutPosition = offset + fullOutOffset;
    }

    /**
     * used by reset arms task to proposed arms speed
     */
    public void setResetSpeed(double s) {
        this.resetSpeed = s;
    }

    /**
     * gets speed proposed by reset arms task
     */
    public double getResetSpeed() {
        return this.resetSpeed;
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

        if (this.getResetSpeed() != 0) {
            speed = this.getResetSpeed();
            this.targetPosition = fullInPosition;
        }

        if (motorPause.getAsBoolean()) {
            speed = 0;
            this.targetPosition = fullInPosition;
        }

        SmartDashboard.putNumber("arms pid output", this.pidOutput);
        SmartDashboard.putNumber("arms speed", speed);
        SmartDashboard.putNumber("arms target", this.targetPosition);

        this.motor.set(speed);
    }

}
