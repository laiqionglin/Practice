package com.yulin.practice.ui.games.mines;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Random;

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
    private int type = 1;
    private int rows;
    private int cols;
    private int mines;
    private MinePiece[][] minePieces;
    private boolean isFlagType;
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void addTime() {
        time++;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        isFlagType = false;
        this.type = type;
        time = 0;
        switch (type) {
            case 2:
                rows = 35;
                cols = 20;
                mines = 100;
                break;
            case 3:
                rows = 23;
                cols = 15;
                mines = 50;
                break;
            default:
                rows = 11;
                cols = 8;
                mines = 10;
                break;
        }
        setMinePieces();
    }

    private void setMinePieces() {
        minePieces = new MinePiece[rows][cols];
        //初始化整个区域
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                minePieces[i][j] = new MinePiece();
            }
        }
        int w = 0;
        Random random = new Random();
        //随机安放地雷
        while (w != mines) {
            int a = random.nextInt(rows);
            int b = random.nextInt(cols);
            if (-1 == minePieces[a][b].getState()) {
                continue;
            }
            minePieces[a][b].setState(-1);
            w++;
        }
        //给所有位置填充数值
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                //如果本身是雷，则跳过
                if (minePieces[i][j].getState() == -1) {
                    continue;
                }
                //获取当前位置为中心的九宫格区域的地雷
                int count = 0;
                //上面
                if (i > 0) {
                    //正上方
                    if (minePieces[i - 1][j].getState() == -1) {
                        count++;
                    }
                    //左上方
                    if (j > 0 && minePieces[i - 1][j - 1].getState() == -1) {
                        count++;
                    }
                    //右上方
                    if (j < cols - 1 && minePieces[i - 1][j + 1].getState() == -1) {
                        count++;
                    }
                }
                //左方
                if (j > 0 && minePieces[i][j - 1].getState() == -1) {
                    count++;
                }
                //右方
                if (j < cols - 1 && minePieces[i][j + 1].getState() == -1) {
                    count++;
                }
                //下面
                if (i < rows - 1) {
                    //正下方
                    if (minePieces[i + 1][j].getState() == -1) {
                        count++;
                    }
                    //左下方
                    if (j > 0 && minePieces[i + 1][j - 1].getState() == -1) {
                        count++;
                    }
                    //右下方
                    if (j < cols - 1 && minePieces[i + 1][j + 1].getState() == -1) {
                        count++;
                    }
                }
                minePieces[i][j].setState(count);
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getMines() {
        return mines;
    }

    public MinePiece getMinePiece(int row, int col) {
        return minePieces[row][col];
    }

    public boolean isFlagType() {
        return isFlagType;
    }

    public void setFlagType(boolean flagType) {
        isFlagType = flagType;
    }
}