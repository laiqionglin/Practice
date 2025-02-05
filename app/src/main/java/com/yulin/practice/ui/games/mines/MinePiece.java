package com.yulin.practice.ui.games.mines;

import android.widget.Button;

public class MinePiece {
    private int state;
    private boolean isOpen;
    private Button button;
    public MinePiece(){
        state=0;
        isOpen=false;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }
}
