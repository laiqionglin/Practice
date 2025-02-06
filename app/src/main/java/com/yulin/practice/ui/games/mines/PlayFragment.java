package com.yulin.practice.ui.games.mines;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(getActivity()).get(MineSweepingViewModel.class);
        initPlayView();
        return binding.getRoot();
    }

    private void initPlayView() {
        mTableLayout = binding.playLayoutTable;
        mTableLayout.removeAllViews();
        for (int i = 0; i < mViewModel.getRows(); i++) {
            //创建一个新的tableRow
            TableRow tableRow = new TableRow(getContext());
            for (int j = 0; j < mViewModel.getCols(); j++) {
                //创建一个view，并添加到表格
                View view = new TextView(getContext());
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mines_button_close));
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(100, 100);
                if (j > 0) {
                    layoutParams.leftMargin = 1;
                }
                if (i > 0) {
                    layoutParams.topMargin = 1;
                }
                view.setLayoutParams(layoutParams);
                int finalI = i;
                int finalJ = j;
                view.setOnClickListener(V -> {
                    MinePiece minePiece = mViewModel.getMinePiece(finalI, finalJ);
                    //如果已经打开，则不做操作
                    if (minePiece.isOpen()) {
                        return;
                    }
                    //打开雷块
                    openPiece(minePiece);
                    if (minePiece.getState() == -1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        AlertDialog alertDialog=builder.setTitle("炸弹").setMessage("你点到炸弹，失败了！").setPositiveButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Activity activity = getActivity();
                                if (activity instanceof MineSweepingActivity) {
                                    MineSweepingActivity mineSweepingActivity = (MineSweepingActivity) activity;
                                    mineSweepingActivity.showChooseFragment();
                                }
                            }
                        }).setNegativeButton("再来", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mViewModel.setType(mViewModel.getType());
                                initPlayView();
                            }
                        }).create();
                        alertDialog.setCancelable(false);//按返回键不取消
                        alertDialog.setCanceledOnTouchOutside(false);//点击对话框之外区域不取消
                        alertDialog.show();
                    }
                    if (minePiece.getState() == 0) {
                        //自动打开周围没有雷的区域
                        openNear(finalI, finalJ);
                    }
                });
                tableRow.addView(view);
                mViewModel.getMinePiece(i, j).setView(view);
            }
            mTableLayout.addView(tableRow);
        }
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