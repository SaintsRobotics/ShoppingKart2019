package com.saintsrobotics.shoppingkart.config;

import edu.wpi.first.wpilibj.Joystick;

public class Button {

    private int pin;

    private Joystick joystick;

    public Button(Joystick joystick, int pin) {
        this.pin = pin;
        this.joystick = joystick;
    }

    public boolean get() {
        return joystick.getRawButton(pin);
    }

    public void setState(boolean state) {
        joystick.setOutput(pin, state);
    }

    public void update() {
        if (joystick == null) {
            return;
        }
        if (joystick.getRawButton(pin)) {
            setState(true);
            return;
        }
        setState(false);
    }
}