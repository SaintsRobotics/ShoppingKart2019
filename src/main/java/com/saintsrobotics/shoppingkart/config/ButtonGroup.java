package com.saintsrobotics.shoppingkart.config;

public class ButtonGroup {

    private Button[] buttons;

    private int previous = 0;

    public ButtonGroup(Button[] buttons) {
        this.buttons = buttons;
    }

    public void check() {
        for (int i = 0; i < this.buttons.length; i++) {
            if (this.buttons[i].get()) {
                if (i != previous) {
                    this.buttons[previous].setState(false);
                    this.buttons[i].setState(true);
                    previous = i;
                }
            }
        }
    }

}
