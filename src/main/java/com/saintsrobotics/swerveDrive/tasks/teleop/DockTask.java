package com.saintsrobotics.swerveDrive.tasks.teleop;

import java.sql.Driver;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.saintsrobotics.swerveDrive.Robot;

import org.opencv.core.Rect;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DockTask extends RunEachFrameTask{

    private Rect targetOne;
    private Rect targetTwo;
    private Object imgLock;
    private SwerveControl sc;

    public DockTask(Rect targetOne, Rect targetTwo, Object imgLock) {
        this.targetOne = targetOne;
        this.targetTwo = targetTwo;
        this.imgLock = imgLock;
        //this.sc = sc;
    }

    @Override
    protected void runEachFrame() {
        synchronized (imgLock) {
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
}