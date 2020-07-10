package com.kylin.progressbutton;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kylin.progressbutton.databinding.ActivityMainBinding;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private int progress;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.progress.setOnClickListener(new ProgressButton.OnClickListener() {
            @Override
            public void onClick(int state) {
                switch (state) {
                    case ProgressButton.STATE_NORMAL:
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                progress += 1;
                                binding.progress.setProgress(progress);
                            }
                        }, 0, 100);
                        break;
                    case ProgressButton.STATE_PROGRESS:
                        if ("继续".equals(binding.progress.getText())) {
                            binding.progress.setProgressText(null);
                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    progress += 1;
                                    binding.progress.setProgress(progress);
                                }
                            }, 0, 100);
                        } else {
                            timer.cancel();
                            binding.progress.setProgressText("继续");
                        }
                        break;
                    case ProgressButton.STATE_FINISH:
                        Toast.makeText(MainActivity.this, "下载完成，打开文件", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        binding.reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = 0;
                timer.cancel();
                binding.progress.setProgressText(null);
                binding.progress.setProgress(0);
            }
        });
        binding.colorRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progress.setForegroundColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        });
        binding.colorGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progress.setForegroundColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        });
        binding.colorBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progress.setForegroundColor(getResources().getColor(android.R.color.holo_blue_dark));
            }
        });
        binding.seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.progress.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.seekBarCorner.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.progress.setCorner(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.seekBarBorder.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.progress.setBorder(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}