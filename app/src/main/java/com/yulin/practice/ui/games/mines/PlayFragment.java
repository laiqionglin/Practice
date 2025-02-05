package com.yulin.practice.ui.games.mines;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yulin.practice.databinding.FragmentPlayBinding;

public class PlayFragment extends Fragment {

    private MineSweepingViewModel mViewModel;

    public static PlayFragment newInstance() {
        return new PlayFragment();
    }

    private FragmentPlayBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding=FragmentPlayBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }



}