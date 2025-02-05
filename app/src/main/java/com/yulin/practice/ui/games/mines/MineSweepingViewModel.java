package com.yulin.practice.ui.games.mines;

import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

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
    private int rows;
    private int cols;
    private int mines;
    private int[][] domain;

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
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
                rows = 15;
                cols = 10;
                mines = 10;
                break;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                setDomain();
            }
        }).start();
    }

    private void setDomain() {
        domain = new int[rows][cols];
        //初始化整个区域都为0
        IntStream.range(0, rows).forEach(i -> Arrays.fill(domain[i], 0));
        int w = 0;
        Random random = new Random();
        //随机安放地雷
        while (w != mines) {
            int a = random.nextInt(rows);
            int b = random.nextInt(cols);
            if (-1 == domain[a][b]) {
                continue;
            }
            domain[a][b] = -1;
            w++;
        }
        //给所有位置填充数值
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                //如果本身是雷，则跳过
                if (domain[i][j] == -1) {
                    continue;
                }
                //获取当前位置为中心的九宫格区域的地雷
                int count = 0;
                //上面
                if (i > 0) {
                    //正上方
                    if (domain[i - 1][j] == -1) {
                        count++;
                    }
                    //左上方
                    if (j > 0 && domain[i - 1][j - 1] == -1) {
                        count++;
                    }
                    //右上方
                    if (j < cols - 1 && domain[i - 1][j + 1] == -1) {
                        count++;
                    }
                }
                //左方
                if (j > 0 && domain[i][j - 1] == -1) {
                    count++;
                }
                //右方
                if (j < cols - 1 && domain[i][j + 1] == -1) {
                    count++;
                }
                //下面
                if (i < rows - 1) {
                    //正下方
                    if (domain[i + 1][j] == -1) {
                        count++;
                    }
                    //左下方
                    if (j > 0 && domain[i + 1][j - 1] == -1) {
                        count++;
                    }
                    //右下方
                    if (j < cols - 1 && domain[i + 1][j + 1] == -1) {
                        count++;
                    }
                }
                domain[i][j] = count;
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

    public int[][] getDomain() {
        return domain;
    }
    public int getDomainItem(int row,int col) {
        return domain[row][col];
    }
}