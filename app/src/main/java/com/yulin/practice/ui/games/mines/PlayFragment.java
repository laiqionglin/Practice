package com.yulin.practice.ui.games.mines;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.yulin.practice.R;
import com.yulin.practice.databinding.FragmentPlayBinding;

public class PlayFragment extends Fragment {

    private MineSweepingViewModel mViewModel;
    private TableLayout mTableLayout;

    public static PlayFragment newInstance() {
        return new PlayFragment();
    }

    private FragmentPlayBinding binding;
    private float scaleFactor = 1.f;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(getActivity()).get(MineSweepingViewModel.class);
        initPlayView();
        registerEvent();
        return binding.getRoot();
    }

    private void initPlayView() {
        mTableLayout = binding.playLayoutTable;
        for (int i = 0; i < mViewModel.getRows(); i++) {
            //创建一个新的tableRow
            TableRow tableRow = new TableRow(getContext());
            for (int j = 0; j < mViewModel.getCols(); j++) {
                //创建一个view，并添加到表格
                View view = new TextView(getContext());
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mines_button_close));
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(100, 100);
                if (j > 0) {
                    layoutParams.leftMargin = 6;
                }
                if (i > 0) {
                    layoutParams.topMargin = 6;
                }
                view.setLayoutParams(layoutParams);
                tableRow.addView(view);
                mViewModel.getMinePiece(i, j).setView(view);
            }
            mTableLayout.addView(tableRow);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void registerEvent() {
        View playView = binding.playLayoutPlay;
        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(0.3f, Math.min(scaleFactor, 3.0f)); // 限制缩放范围
                mTableLayout.setScaleX(scaleFactor);
                mTableLayout.setScaleY(scaleFactor);
                return true;
            }
        });

        playView.setOnTouchListener(new View.OnTouchListener() {
            private float initialX;
            private float initialY;
            private float initialTouchX;
            private float initialTouchY;
            private boolean isDragging = false;
            private int location[];

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() > 1) {
                    scaleGestureDetector.onTouchEvent(event);
                    return true;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isDragging = false; // 重置拖动状态，以防多点触控问题。
                        initialX = mTableLayout.getX();
                        initialY = mTableLayout.getY();
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        location = hasChildAtPoint(initialTouchX, initialTouchY);

                        if (location != null) {
                            MinePiece minePiece = mViewModel.getMinePiece(location[0], location[1]);
                            if (!minePiece.isOpen()) {
                                minePiece.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mines_button_close_pressed));
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!isDragging) { // 防止多点触控时的不必要移动。
                            isDragging = true; // 设置拖动状态。
                        } else { // 拖动中...更新位置。
                            float dx = event.getRawX() - initialTouchX;
                            float dy = event.getRawY() - initialTouchY;
                            mTableLayout.animate().x(initialX + dx).y(initialY + dy).setDuration(0) // 立即执行动画。
                                    .start(); // 开始动画。

                        }
                        break;
                    case MotionEvent.ACTION_UP: // 释放手指，结束拖动。通常不需要在这里做任何事情，除非你需要额外的行为。
                        if (location != null) {
                            MinePiece minePiece = mViewModel.getMinePiece(location[0], location[1]);
                            if (!minePiece.isOpen()) {
                                minePiece.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mines_button_close));
                                if (event.getEventTime() - event.getDownTime() < 500
                                        && Math.abs(event.getRawX() - initialTouchX) < 2.0
                                        && Math.abs(event.getRawY() - initialTouchY) < 2.0) {//时间短，没有移动，当做点击事件
                                    //打开雷块
                                    openPiece(minePiece);
                                    if (mViewModel.getMinePiece(location[0], location[1]).getState() == 0) {
                                        //自动打开周围没有雷的区域
                                        openNear(location[0], location[1]);
                                    }
                                }
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    public int[] hasChildAtPoint(float x, float y) {
        for (int i = 0; i < mViewModel.getRows(); i++) {
            for (int j = 0; j < mViewModel.getCols(); j++) {
                View view = mViewModel.getMinePiece(i, j).getView();
                if (isViewUnderPoint(view, x, y)) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private boolean isViewUnderPoint(View view, float x, float y) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        int viewX = location[0];
        int viewY = location[1];
        return x >= viewX && x < viewX + view.getWidth() && y >= viewY && y < viewY + view.getHeight();
    }

    /**
     * 打开块
     *
     * @param minePiece
     */
    private void openPiece(MinePiece minePiece) {
        minePiece.setOpen(true);
        //被点击之后，不能在点击了
        View view = minePiece.getView();
        view.setEnabled(false);
        //打开雷块
        switch (minePiece.getState()) {
            case -1:
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_mine_bom_120dp));
                break;
            case 1:
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_mine_1));
                break;
            case 2:
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_mine_2));
                break;
            case 3:
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_mine_3));
                break;
            case 4:
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_mine_4));
                break;
            case 5:
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_mine_5));
                break;
            case 6:
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_mine_6));
                break;
            case 7:
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_mine_7));
                break;
            case 8:
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_mine_8));
                break;
            default:
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mines_button_open));
                break;
        }
    }

    /**
     * 打开九宫格周边非雷的块
     *
     * @param row
     * @param col
     */
    private void openNear(int row, int col) {
        //正上方
        if (row > 0) {
            MinePiece minePiece = mViewModel.getMinePiece(row - 1, col);
            if (!minePiece.isOpen()) {
                //正上方
                if (minePiece.getState() > 0) {
                    openPiece(minePiece);
                } else if (minePiece.getState() == 0) {
                    openPiece(minePiece);
                    openNear(row - 1, col);
                }
            }
        }
        //左上方
        if (row > 0 && col > 0) {
            MinePiece minePiece = mViewModel.getMinePiece(row - 1, col - 1);
            if (!minePiece.isOpen()) {
                if (minePiece.getState() > 0) {
                    openPiece(minePiece);
                } else if (minePiece.getState() == 0) {
                    openPiece(minePiece);
                    openNear(row - 1, col - 1);
                }
            }
        }
        //右上方
        if (row > 0 && col < mViewModel.getCols() - 1) {
            MinePiece minePiece = mViewModel.getMinePiece(row - 1, col + 1);
            if (!minePiece.isOpen()) {
                if (minePiece.getState() > 0) {
                    openPiece(minePiece);
                } else if (minePiece.getState() == 0) {
                    openPiece(minePiece);
                    openNear(row - 1, col + 1);
                }
            }
        }
        //左方
        if (col > 0) {
            MinePiece minePiece = mViewModel.getMinePiece(row, col - 1);
            if (!minePiece.isOpen()) {
                if (minePiece.getState() > 0) {
                    openPiece(minePiece);
                } else if (minePiece.getState() == 0) {
                    openPiece(minePiece);
                    openNear(row, col - 1);
                }
            }
        }
        //右方
        if (col < mViewModel.getCols() - 1) {
            MinePiece minePiece = mViewModel.getMinePiece(row, col + 1);
            if (!minePiece.isOpen()) {
                if (minePiece.getState() > 0) {
                    openPiece(minePiece);
                } else if (minePiece.getState() == 0) {
                    openPiece(minePiece);
                    openNear(row, col + 1);
                }
            }
        }
        //正下方
        if (row < mViewModel.getRows() - 1) {
            MinePiece minePiece = mViewModel.getMinePiece(row + 1, col);
            if (!minePiece.isOpen()) {
                if (minePiece.getState() > 0) {
                    openPiece(minePiece);
                } else if (minePiece.getState() == 0) {
                    openPiece(minePiece);
                    openNear(row + 1, col);
                }
            }
        }
        //左下方
        if (row < mViewModel.getRows() - 1 && col > 0) {
            MinePiece minePiece = mViewModel.getMinePiece(row + 1, col - 1);
            if (!minePiece.isOpen()) {
                if (minePiece.getState() > 0) {
                    openPiece(minePiece);
                } else if (minePiece.getState() == 0) {
                    openPiece(minePiece);
                    openNear(row + 1, col - 1);
                }
            }
        }
        //右下方
        if (row < mViewModel.getRows() - 1 && col < mViewModel.getCols() - 1) {
            MinePiece minePiece = mViewModel.getMinePiece(row + 1, col + 1);
            if (!minePiece.isOpen()) {
                if (minePiece.getState() > 0) {
                    openPiece(minePiece);
                } else if (minePiece.getState() == 0) {
                    openPiece(minePiece);
                    openNear(row + 1, col + 1);
                }

            }
        }

    }
}