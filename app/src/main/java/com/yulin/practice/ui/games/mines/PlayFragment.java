package com.yulin.practice.ui.games.mines;


import android.graphics.Color;
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

//    private Button[][] mButtons;

    private void initPlayView() {
        TableLayout mTableLayout = binding.playLayoutTable;
//        mButtons = new Button[mViewModel.getRows()][mViewModel.getCols()];
        for (int i = 0; i < mViewModel.getRows(); i++) {
            //创建一个新的tableRow
            TableRow tableRow = new TableRow(getContext());
            for (int j = 0; j < mViewModel.getCols(); j++) {
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
//                mButtons[i][j] = button;
                int finalI = i;
                int finalJ = j;
                button.setOnClickListener(V -> {
                    button.setText(String.valueOf(mViewModel.getDomainItem(finalI, finalJ)));
                    button.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.mines_button_open));
                });
            }
            mTableLayout.addView(tableRow);
        }
    }


}