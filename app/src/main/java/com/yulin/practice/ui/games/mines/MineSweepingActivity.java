package com.yulin.practice.ui.games.mines;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.yulin.practice.databinding.ActivityMineSweepingBinding;

import java.util.List;

public class MineSweepingActivity extends AppCompatActivity implements ChooseFragment.OnChooseFragmentListener {

    private ActivityMineSweepingBinding binding;

    private FrameLayout mFrameLayout;
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
        // 锁定为纵向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mFrameLayout = binding.sweepingFrameLayout;
        mViewModel = new ViewModelProvider(this).get(MineSweepingViewModel.class);
        showChooseFragment();
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                List<Fragment> fragments = fragmentManager.getFragments();
                if (fragments != null) {
                    Fragment fragment = fragments.get(0);
                    if (fragment instanceof PlayFragment) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MineSweepingActivity.this);
                        builder.setTitle("炸弹").setMessage("你点到炸弹，失败了！").setPositiveButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showChooseFragment();
                            }
                        }).setNegativeButton("继续", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
                    } else {
                        finish();
                    }
                }
            }
        });
    }

    /**
     * 显示选择界面
     */
    public void showChooseFragment() {
        showActionBar();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mFrameLayout.getId(), new ChooseFragment());
        transaction.commit();
    }

    /**
     * 显示游戏界面，需要携带难度
     *
     * @param type 游戏难度
     */
    public void showPlayFragment(int type) {
        mViewModel.setType(type);
        hideActionBar();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mFrameLayout.getId(), new PlayFragment());
        transaction.commit();
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
     *
     * @param type 游戏难度
     */
    @Override
    public void onChoose(int type) {
        showPlayFragment(type);
    }
}