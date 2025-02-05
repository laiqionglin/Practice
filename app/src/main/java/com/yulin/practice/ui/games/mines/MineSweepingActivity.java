package com.yulin.practice.ui.games.mines;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.yulin.practice.databinding.ActivityMineSweepingBinding;

public class MineSweepingActivity extends AppCompatActivity implements ChooseFragment.OnChooseFragmentListener {

    private ActivityMineSweepingBinding binding;

    private FrameLayout mFrameLayout;
    private FragmentTransaction mTransaction;
    private MineSweepingViewModel mViewModel;

    /**
     * 初始化所需数据，并显示选择界面
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.
     *                           <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMineSweepingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mFrameLayout = binding.sweepingFrameLayout;
        mTransaction = getSupportFragmentManager().beginTransaction();
        mViewModel = new ViewModelProvider(this).get(MineSweepingViewModel.class);
        showChooseFragment();
    }

    /**
     * 显示选择界面
     */
    public void showChooseFragment() {
        mTransaction.replace(mFrameLayout.getId(), new ChooseFragment());
        mTransaction.commit();
        showActionBar();
    }

    /**
     * 显示游戏界面，需要携带难度
     *
     * @param type 游戏难度
     */
    public void showPlayFragment(int type) {
        mViewModel.setType(type);
        mTransaction.replace(mFrameLayout.getId(), new ChooseFragment());
        mTransaction.commit();
        hideActionBar();
    }

    /**
     * 显示标题栏
     */
    private void showActionBar() {
        if (null != getSupportActionBar()) {
            getSupportActionBar().show();
        }
    }
    /**
     * 隐藏标题栏
     */
    private void hideActionBar() {
        if (null != getSupportActionBar()) {
            getSupportActionBar().hide();
        }
    }

    /**
     * 实现ChooseFragment接口的选择方法
     * @param type 游戏难度
     */
    @Override
    public void onChoose(int type) {
        showPlayFragment(type);
    }
}