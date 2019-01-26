package com.saintsrobotics.swerveDrive.tasks.teleop;

import java.sql.Driver;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.saintsrobotics.swerveDrive.Robot;
import com.saintsrobotics.swerveDrive.util.VisionBroker;

import org.opencv.core.Rect;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DockTask extends RunEachFrameTask{

    private VisionBroker broker;
    private SwerveControl sc;

    public DockTask(VisionBroker broker, SwerveControl sc) {
        this.broker = broker;
        this.sc = sc;
    }

    @Override
    protected void runEachFrame() {
        Rect targetOne = broker.getRects()[0];
        Rect targetTwo = broker.getRects()[1];
        if (targetOne == null || targetTwo == null) {
            DriverStation.reportWarning("No rectangles", false);
            return;
        }
        DriverStation.reportWarning("Yes rectangles", false);
        Double t1 = targetOne.tl().x;
        Double t2 = targetTwo.br().x;
        SmartDashboard.putNumber("Target 1", targetOne.tl().x);
        SmartDashboard.putNumber("Target 2", targetTwo.br().x);
        SmartDashboard.putNumber("Center", t1 + (t2-t1) );
        
        //write PID loop here
        
    }
}