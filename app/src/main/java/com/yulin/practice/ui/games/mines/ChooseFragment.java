package com.yulin.practice.ui.games.mines;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yulin.practice.databinding.FragmentChooseBinding;

public class ChooseFragment extends Fragment {



    public static ChooseFragment newInstance() {
        return new ChooseFragment();
    }

    //定义接口
    public interface OnChooseFragmentListener {
        public void onChoose(int type);
    }

    // 持有接口引用的变量
    private OnChooseFragmentListener mListener;

    // 在onAttach中设置监听器
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChooseFragmentListener) {
            mListener = (OnChooseFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    private FragmentChooseBinding binding;


    //在onCreateView绑定按钮事件
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChooseBinding.inflate(inflater, container, false);
        binding.buttonChooseEasy.setOnClickListener(v -> mListener.onChoose(MineSweepingViewModel.TYPE_EASY));
        binding.buttonChooseMiddle.setOnClickListener(v -> mListener.onChoose(MineSweepingViewModel.TYPE_MIDDLE));
        binding.buttonChooseHard.setOnClickListener(v -> mListener.onChoose(MineSweepingViewModel.TYPE_HARD));
        return binding.getRoot();
    }
}