package com.yulin.practice.ui.games.mines;


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.yulin.practice.R;
import com.yulin.practice.databinding.FragmentPlayBinding;

import java.util.Timer;
import java.util.TimerTask;

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

    private Timer mTimer;

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
                    layoutParams.leftMargin = 5;
                }
                if (i > 0) {
                    layoutParams.topMargin = 5;
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
                    //插旗模式
                    if (mViewModel.isFlagType()) {
                        flagPiece(minePiece);
                        return;
                    }
                    //如果已经插旗了，则不操作
                    if (minePiece.isFlag()) {
                        return;
                    }
                    //打开雷块
                    openPiece(minePiece);
                    if (minePiece.getState() == -1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        AlertDialog alertDialog = builder.setTitle("炸弹").setMessage("你点到炸弹，失败了！").setPositiveButton("退出", new DialogInterface.OnClickListener() {
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
        Button button = binding.playButtonFlag;
        button.setOnClickListener(v -> {
            mViewModel.setFlagType(!mViewModel.isFlagType());
            if (mViewModel.isFlagType()) {
                button.setText("取消插旗");
            } else {
                button.setText("插旗模式");
            }
            refreshFlagType();
        });
        binding.playTextFinish.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("退出").setMessage("您真的要退出当前游戏吗！").setPositiveButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Activity activity = getActivity();
                    if (activity instanceof MineSweepingActivity) {
                        MineSweepingActivity mineSweepingActivity = (MineSweepingActivity) activity;
                        mineSweepingActivity.showChooseFragment();
                    }
                }
            }).setNegativeButton("继续", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mViewModel.setType(mViewModel.getType());
                    initPlayView();
                }
            }).create().show();
        });
        //计时
        if (mTimer != null) {
            mTimer.cancel();
        }
        mViewModel.setTime(0);
        refreshTimeView(0);
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    mViewModel.addTime();
                    refreshTimeView(mViewModel.getTime());
                }
                return false;
            }
        });
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 500, 1000);
    }

    private void refreshTimeView(long time) {
        long second = time % 60;
        long minute = time / 60;
        long hour = 0;
        String s = "";
        String m = "";
        String h = "";
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
            h = alignTime(hour);
            s = alignTime(second);
            m = alignTime(minute);
            binding.playTextTime.setText(h+":"+m+":"+s);
            return;
        }
        s = alignTime(second);
        m = alignTime(minute);
        binding.playTextTime.setText(m+":"+s);
    }

    private String alignTime(long t) {
        String s;
        if (t < 10) {
            s = "0" + t;
        } else {
            s = "" + t;
        }
        return s;
    }

    private void refreshFlagType() {
        for (int i = 0; i < mViewModel.getRows(); i++) {
            for (int j = 0; j < mViewModel.getCols(); j++) {
                MinePiece minePiece = mViewModel.getMinePiece(i, j);
                //没有打开且没有插旗的块
                if (!minePiece.isFlag() && !minePiece.isOpen()) {
                    if (mViewModel.isFlagType()) {
                        minePiece.getView().setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_flag_dark));
                    } else {
                        minePiece.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mines_button_close));
                    }
                }
            }
        }
    }

    private boolean isSuccess() {
        for (int i = 0; i < mViewModel.getRows(); i++) {
            for (int j = 0; j < mViewModel.getCols(); j++) {
                MinePiece minePiece = mViewModel.getMinePiece(i, j);
                //如果还有非雷的块未打开，则还未成功
                if (minePiece.getState() != -1 && !minePiece.isOpen()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void flagPiece(MinePiece minePiece) {
        if (minePiece.isFlag()) {
            minePiece.getView().setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_flag_dark));
        } else {
            minePiece.getView().setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_flag_light));
        }
        minePiece.setFlag(!minePiece.isFlag());
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
        if (isSuccess()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            AlertDialog alertDialog = builder.setTitle("成功").setMessage("恭喜你找出了所有的雷！").setPositiveButton("退出", new DialogInterface.OnClickListener() {
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