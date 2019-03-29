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
    private OperatorBoard xboxInput;

    public ClimbTest(MotorRamping motor, AbsoluteEncoder encoder, OperatorBoard xboxInput) {
        this.motor = motor;
        this.encoder = encoder;
        this.xboxInput = xboxInput;
    }

    @Override
    protected void runEachFrame() {
        double x = -this.xboxInput.liftY();
        this.motor.set(x);
        SmartDashboard.putNumber("control input", x);
        SmartDashboard.putNumber("test climb motor", this.motor.get());
        SmartDashboard.putNumber("test climb encoder", this.encoder.getRotation());
        this.motor.update();
    }
}