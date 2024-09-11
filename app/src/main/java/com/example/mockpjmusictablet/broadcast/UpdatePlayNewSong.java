package com.example.mockpjmusictablet.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mockpjmusictablet.Utils.Const;
import com.example.mockpjmusictablet.data.view_model.SongViewModel;

public class UpdatePlayNewSong extends BroadcastReceiver {
    private final SongViewModel viewModel;

    public UpdatePlayNewSong(SongViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Const.ACTION_SEND_DATA.equals(intent.getAction())) {
            String title = intent.getStringExtra(Const.KEY_TITLE_SONG);
            viewModel.setSongName(title);
            String path = intent.getStringExtra(Const.KEY_PATH);
            viewModel.setPath(path);
        }
    }
}
