package com.yulin.practice.ui.games.mines;

import androidx.lifecycle.ViewModel;

public class MineSweepingViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    /**
     * 游戏难度，简单
     */
    public static final int TYPE_EASY = 1;
    /**
     * 游戏难度，中等
     */
    public static final int TYPE_MIDDLE = 2;
    /**
     * 游戏难度，困难
     */
    public static final int TYPE_HARD = 3;


    /**
     * 游戏难度，默认为1
     */
    private int mType = 1;

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }
}