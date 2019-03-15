package com.saintsrobotics.shoppingkart.config;

import com.github.dozer.input.OI.Input;
import com.github.dozer.input.OI.XboxInput;

public class OperatorBoard extends Input {

    private Button[] buttons = new Button[21];

    private ButtonGroup liftGroup;
    private ButtonGroup armGroup;

    private Button[] nonGroupButtons;

    private XboxInput xboxInput;

    private int pin;

    public OperatorBoard(int pin) {
        super(pin);
        this.pin = pin;

    }

    private boolean isBoard = true;

    @Override
    public void init() {
        if (isBoard) {
            super.init();
            for (int i = 1; i < buttons.length; i++) {
                buttons[i] = new Button(joystick, i);
            }
            this.armGroup = new ButtonGroup(
                    new Button[] { this.buttons[1], this.buttons[4], this.buttons[5], this.buttons[6], });
            this.liftGroup = new ButtonGroup(new Button[] { this.buttons[2], this.buttons[3], this.buttons[10],
                    this.buttons[12], this.buttons[13], this.buttons[14], this.buttons[15], this.buttons[16],
                    this.buttons[17], this.buttons[18], this.buttons[19] });
            this.nonGroupButtons = new Button[] { this.buttons[7], this.buttons[8], this.buttons[9], this.buttons[11] };
        } else {

            xboxInput = new XboxInput(pin);
            for (int i = 1; i < buttons.length; i++) {
                buttons[i] = new Button(joystick, 0);
            }
            super.init();
        }
    }

    public boolean armsHardstop() {
        if (!isBoard) {
        }
        return buttons[1].get();
    }

    public boolean lowerLift() {
        if (!isBoard) {
            return this.xboxInput.Y();
        }
        return buttons[2].get();
    }

    public boolean lowerLiftBack() {
        return buttons[3].get();
    }

    public boolean armsPickUp() {
        if (!isBoard) {
            return this.xboxInput.X();
        }
        return buttons[4].get();
    }

    public boolean armsRest() {
        if (!isBoard) {
            return this.xboxInput.B();
        }
        return buttons[5].get();
    }

    public boolean armsOut() {
        if (!isBoard) {
            return this.xboxInput.A();
        }
        return buttons[6].get();
    }

    public boolean intakeIn() {
        if (!isBoard) {
            return this.xboxInput.RB();
        }
        return buttons[7].get();
    }

    public boolean kicker() {
        if (!isBoard) {
            return this.xboxInput.LB();
        }
        return buttons[8].get();
    }

    public boolean intakeOut() {
        if (!isBoard) {
            return this.xboxInput.SELECT();
        }
        return buttons[9].get();
    }

    public boolean button10() {
        return buttons[10].get();
    }

    public boolean pidOff() {
        return buttons[11].get();
    }

    public boolean hatch3() {
        if (!isBoard) {
            return this.xboxInput.START();
        }
        return buttons[12].get();
    }

    public boolean cargo3() {
        return buttons[13].get();
    }

    public boolean hatch2() {
        return buttons[14].get();
    }

    public boolean cargo2() {
        return buttons[15].get();
    }

    public boolean hatch1() {
        return buttons[16].get();
    }

    public boolean cargo1() {
        return buttons[17].get();
    }

    public boolean liftBottom() {
        return buttons[18].get();
    }

    public boolean cargoBall() {
        return buttons[19].get();
    }

    public boolean button20() {
        return buttons[20].get();
    }

    public double liftX() {
        return deadzone(-joystick.getRawAxis(0));
    }

    public double liftY() {
        if (!isBoard) {
            return this.xboxInput.leftTrigger() - this.xboxInput.rightTrigger();
        }
        return deadzone(-joystick.getRawAxis(1));
    }

    public void update() {
        if (!isBoard) {
            return;
        }
        for (Button b : nonGroupButtons) {
            b.update();
        }
        armGroup.check();
        liftGroup.check();

    }

}