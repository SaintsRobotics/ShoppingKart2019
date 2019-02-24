package com.saintsrobotics.shoppingkart.arms;

import java.util.function.BooleanSupplier;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.output.Motor;
import com.saintsrobotics.shoppingkart.util.AbsoluteEncoder;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ArmsControl extends RunEachFrameTask {
    private BooleanSupplier motorPause;

    private double resetSpeed;

    private Motor motor;

    private static double offset = 285; // location of the hard stop

    private PIDController pidController;
    private double pidOutput;

    /**
     * @param motorPause trigger that pauses motor speed
     * @param encoder    assumes that as the arms spin in, the encoder decreases
     */
    public ArmsControl(BooleanSupplier motorPause, AbsoluteEncoder encoder, Motor motor) {
        this.motorPause = motorPause;

        this.motor = motor;

        this.pidController = new PIDController(0.025, 0.0, 0.0, encoder, (output) -> this.pidOutput = output);
        this.pidController.setAbsoluteTolerance(2.0);
        this.pidController.setOutputRange(-0.5, 0.5);
        this.pidController.setInputRange(0, 360);
        this.pidController.reset();
        this.pidController.enable();

        this.setTarget(-208);
    }

    /**
     * 
     * @param n where the encoder is at the location of the hard stop (passed full
     *          out)
     */
    public void setOffset(double n) {
        offset = n;
    }

    /**
     * sets pid setpoint to an encoder positon
     * 
     * @param d the offset of the target position relative to the hard stop
     */
    public void setTarget(double d) {
        this.pidController.setSetpoint(offset + d);
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
            speed = this.resetSpeed;
            this.setTarget(-208); // use a config constant instead of double
        }

        if (motorPause.getAsBoolean()) {
            speed = 0;
            this.setTarget(-208); // use a config constant instead of double
        }
        SmartDashboard.putNumber("arms target", this.pidController.getSetpoint());
        SmartDashboard.putNumber("arms speed", speed);
        this.motor.set(speed);
    }

}
