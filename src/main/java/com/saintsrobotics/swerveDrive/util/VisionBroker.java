package com.saintsrobotics.swerveDrive.util;

import org.opencv.core.Rect;

public class VisionBroker {

    private Rect[] targets;

    public VisionBroker() {
        this.targets = new Rect[2];
    }

    public synchronized Rect[] getRects() {
        return targets;
    }

    public synchronized void setRects(Rect targetOne, Rect targetTwo) {
        this.targets[0] = targetOne;
        this.targets[1] = targetTwo;
    }

}