package com.saintsrobotics.swerveDrive.util;

public class AngleUtilities {

    /**
     * converts cartesian vector to polar vectos
     * @param coords coordinates of the two-dimensional cartesian vector {x, y}
     * @return returns in the format of {heading, velocity}
     */
    public static double[] cartesianToPolar(double[] coords) {
        double[] polar = new double[2];
        polar[0] = findAngle(coords[0], coords[1]);
        polar[1] = Math.sqrt(Math.pow(coords[0], 2) + Math.pow(coords[1], 2));
        return polar;
    }

    /**
     * finds angle between zero and the terminal point
     * in degrees clockwise, relative to the positive y-axis
     * @param x the x coordinate of the terminal point
     * @param y the y coordinate of the terminal point
     * @return said angle (see above)
     */
    public static double findAngle(double x, double y) {
        // finds angle from y-axis to given coordinates
        if (y == 0 && x < 0)
            return 270.00;
        if (y == 0 && x > 0)
            return 90.00;

        if (y >= 0)
            return (360 + Math.toDegrees(Math.atan(x / y))) % 360;

        else
            return 180 + Math.toDegrees(Math.atan(x / y));
    }
}