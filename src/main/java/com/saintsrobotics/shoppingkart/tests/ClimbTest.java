package com.saintsrobotics.shoppingkart.tests;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.input.OI.XboxInput;
import com.saintsrobotics.shoppingkart.config.OperatorBoard;
import com.saintsrobotics.shoppingkart.util.AbsoluteEncoder;
import com.saintsrobotics.shoppingkart.util.MotorRamping;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ClimbTest extends RunEachFrameTask {
    private MotorRamping motor;
    private AbsoluteEncoder encoder;
    private OperatorBoard oppInput;

    public ClimbTest(MotorRamping motor, AbsoluteEncoder encoder, OperatorBoard oppInput) {
        this.motor = motor;
        this.encoder = encoder;
        this.oppInput = oppInput;
    }

    @Override
    protected void runEachFrame() {
        double x = this.oppInput.liftY();
        double motorValue = this.motor.get();
        if (this.encoder.getRotation() < 190 && motorValue > 0) {
            this.motor.stop();
            x = 0;
        } else if (this.encoder.getRotation() > 260 && motorValue < 0) {
            this.motor.stop();
            x = 0;
        }
        this.motor.set(x);
        SmartDashboard.putNumber("control input", x);
        SmartDashboard.putNumber("test climb motor", this.motor.get());
        SmartDashboard.putNumber("test climb encoder", this.encoder.getRotation());
        this.motor.update();
    }
}