package com.saintsrobotics.shoppingkart.arms;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.output.Motor;
import com.saintsrobotics.shoppingkart.Robot;
import com.saintsrobotics.shoppingkart.config.PidConfig;
import com.saintsrobotics.shoppingkart.util.AbsoluteEncoder;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ArmsControl extends RunEachFrameTask {
    private BooleanSupplier motorPause;

    private double resetSpeed;

    private Motor motor;

    private static double offset; // location of the hard stop
    private static double restPosition; // full in relative to hard stop

    private PIDController pidController;
    private double pidOutput;

    private boolean isResetting = false;

    /**
     * @param motorPause   trigger that pauses motor speed
     * @param encoder      assumes that as the arms spin in, the encoder decreases
     * @param offset       the value of the encoder when the arms are against the
     *                     hard stop
     * @param restPosition the position to which the arms will go after a reset or
     *                     disable (full in)
     * 
     * @param initPos      position upon enable (full out position)
     */
    public ArmsControl(BooleanSupplier motorPause, AbsoluteEncoder encoder, Motor motor, double offset,
            double restPosition, PidConfig pidConfig) {
        this.motorPause = motorPause;

        this.motor = motor;

        ArmsControl.offset = offset;
        ArmsControl.restPosition = restPosition;

        this.pidController = new PIDController(pidConfig.kP, pidConfig.kI, pidConfig.kD, encoder,
                (output) -> this.pidOutput = output);
        this.pidController.setAbsoluteTolerance(pidConfig.tolerance);
        this.pidController.setOutputRange(-0.85, 0.85);
        this.pidController.setInputRange(0, 360);
        this.pidController.reset();
        this.pidController.enable();

        this.pidController.setSetpoint(encoder.getRotation());
    }

    /**
     * 
     * @param n where the encoder is at the location of the hard stop (passed full
     *          out)
     */
    public void setOffset(double n) {
        DriverStation.reportWarning("arms offset " + offset, false);
        offset = n;
    }

    /**
     * sets pid setpoint to an encoder positon
     * 
     * @param d the offset of the target position relative to the hard stop
     */
    public void setTarget(double d) {
        this.pidController.setSetpoint(offset + d);
        // SmartDashboard.putNumber("arms target offset from hard stop", d);
    }

    /**
     * used by reset arms task to proposed arms speed
     */
    public void setResetSpeed(double s) {
        this.resetSpeed = s;
    }

    @Override
    protected void runEachFrame() {

        double speed = this.pidOutput;

        if (this.resetSpeed != 0) {
            this.isResetting = true;
            speed = this.resetSpeed;
        } else if (this.isResetting) { // no resset input, but still isResetting
            this.isResetting = false;
            this.setTarget(restPosition);
        }

        if (motorPause.getAsBoolean()) {
            speed = 0;
            this.setTarget(restPosition);
        }
        this.motor.set(speed);

        // SmartDashboard.putNumber("arms target", this.pidController.getSetpoint());
        SmartDashboard.putNumber("arms pid output", this.pidOutput);
        SmartDashboard.putNumber("arms pid error", this.pidController.getError());
        // SmartDashboard.putNumber("arms hard stop location", ArmsControl.offset);
    }

}
