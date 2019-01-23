package com.saintsrobotics.swerveDrive.tasks.teleop;

import com.github.dozer.coroutine.helpers.RunContinuousTask;
import com.saintsrobotics.swerveDrive.Robot;

import org.opencv.core.Rect;

public class DockTask extends RunContinuousTask{

    private Rect targetOne;
    private Rect targetTwo;
    private Object imgLock = new Object();

    public DockTask(Rect targetOne, Rect targetTwo, Object imgLock) {
        this.targetOne = targetOne;
        this.targetTwo = targetTwo;
        this.imgLock = imgLock;
    }

    protected void runForever() {
        wait.until(() -> Robot.instance.oi.xboxInput.START());

        //write PID loop here
    }
}