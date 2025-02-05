package com.yulin.practice.ui.games;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.yulin.practice.R;
import com.yulin.practice.databinding.FragmentGamesBinding;
import com.yulin.practice.ui.games.mines.MineSweepingActivity;

public class GamesFragment extends Fragment {

    private FragmentGamesBinding binding;

    private GridView mGridView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GamesViewModel gamesViewModel =
                new ViewModelProvider(this).get(GamesViewModel.class);

        binding = FragmentGamesBinding.inflate(inflater, container, false);
        mGridView = binding.gridGames;
        mGridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (null == convertView) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_games, parent, false);
                }
                ImageView imageView = convertView.findViewById(R.id.grid_item_image);
                TextView textView = convertView.findViewById(R.id.grid_item_text);
                textView.setText(getResources().getText(R.string.mine_sweeping));
                return convertView;
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (0 == position) {
                    Intent intent = new Intent(getActivity(), MineSweepingActivity.class);
                    startActivity(intent);
                }
            }
        });
        return binding.getRoot();
    }
}