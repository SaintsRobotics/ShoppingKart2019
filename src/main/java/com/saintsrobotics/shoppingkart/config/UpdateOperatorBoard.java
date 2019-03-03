package com.saintsrobotics.shoppingkart.config;

import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.saintsrobotics.shoppingkart.Robot;

public class UpdateOperatorBoard extends RunEachFrameTask {

    OperatorBoard board;

    public UpdateOperatorBoard(OperatorBoard board) {
        this.board = board;
    }

    @Override
    protected void runEachFrame() {
        this.board.update();
    }

}