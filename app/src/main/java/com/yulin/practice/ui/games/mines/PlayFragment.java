package com.yulin.practice.ui.games.mines;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.yulin.practice.R;
import com.yulin.practice.databinding.FragmentPlayBinding;

public class PlayFragment extends Fragment {

    private MineSweepingViewModel mViewModel;

    public static PlayFragment newInstance() {
        return new PlayFragment();
    }

    private FragmentPlayBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(getActivity()).get(MineSweepingViewModel.class);
        initPlayView();
        return binding.getRoot();
    }

    private void initPlayView() {
        TableLayout mTableLayout = binding.playLayoutTable;
//        mButtons = new Button[mViewModel.getRows()][mViewModel.getCols()];
        for (int i = 0; i < mViewModel.getRows(); i++) {
            //创建一个新的tableRow
            TableRow tableRow = new TableRow(getContext());
            for (int j = 0; j < mViewModel.getCols(); j++) {
                //创建一个button，并添加到表格
                Button button = new Button(getContext());
                button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selector_mines_button_close));
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(100, 100);
                if (j > 0) {
                    layoutParams.leftMargin = 8;
                }
                if (i > 0) {
                    layoutParams.topMargin = 8;
                }
                button.setLayoutParams(layoutParams);
                tableRow.addView(button);
                mViewModel.getMinePiece(i, j).setButton(button);
                //监听雷区点击事件
                int finalI = i;
                int finalJ = j;
                button.setOnClickListener(V -> {
                    //如果已经打开，则不做操作
                    if (mViewModel.getMinePiece(finalI, finalJ).isOpen()) {
                        return;
                    }
                    //打开雷块
                    openPiece(mViewModel.getMinePiece(finalI, finalJ));
                    if (mViewModel.getMinePiece(finalI, finalJ).getState() == 0) {
                        //自动打开周围没有雷的区域
                        openNear(finalI, finalJ);
                    }
                    //后续需更新点到雷的操作
                });
            }
            mTableLayout.addView(tableRow);
        }
    }

    private void openPiece(MinePiece minePiece) {
        minePiece.setOpen(true);
        //被点击之后，不能在点击了
        Button button = minePiece.getButton();
        button.setEnabled(false);
        //显示附近雷数
        button.setText(String.valueOf(minePiece.getState()));
        button.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mines_button_open));
    }

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