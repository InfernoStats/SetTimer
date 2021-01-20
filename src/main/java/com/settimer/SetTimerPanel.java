package com.settimer;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.border.EmptyBorder;
import javax.swing.*;

import java.util.Timer;
import java.util.TimerTask;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

@Slf4j
@Singleton
public class SetTimerPanel extends PluginPanel {
    private Timer timer;
    private int currentTime = 3 * 60 + 30;
    private final int setTime = 3 * 60 + 30;
    private final int jadTime = 1 * 60 + 45;

    private final SetTimerPlugin plugin;
    private final SetTimerConfig config;

    private ButtonState state = ButtonState.WAITING;

    enum ButtonState {
        WAITING (1 << 0),
        STARTED (1 << 1),
        PAUSED (1 << 2),
        RESUMED  (1 << 3);

        private int state;

        public int getState()
        {
            return this.state;
        }

        private ButtonState(int state)
        {
            this.state = state;
        }
    };

    private void CancelTimer(JLabel timerLabel)
    {
        timer.cancel();
        if (state == ButtonState.STARTED)
        {
            currentTime += jadTime;
        }
        else if (state == ButtonState.RESUMED)
        {
            currentTime = setTime;
        }
        UpdateTimerLabel(timerLabel, currentTime);
    }

    private void CreateTimer(JLabel timerLabel)
    {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (currentTime-- == 0)
                {
                    currentTime = setTime;
                }
                UpdateTimerLabel(timerLabel, currentTime);
            }
        }, 0, 1000);
    }

    private void UpdateTimerLabel(JLabel label, int seconds)
    {
        final int minutes = seconds / 60;
        label.setText(String.format("%01d:%02d", minutes, seconds % 60));
    }

    @Inject
    public SetTimerPanel(SetTimerPlugin plugin, SetTimerConfig config)
    {
        this.plugin = plugin;
        this.config = config;

        setLayout(new BorderLayout(0, 4));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(new EmptyBorder(8, 8, 8, 8));
        JPanel mainContent = new JPanel(new BorderLayout());

        JButton button = new JButton("Start");
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
        button.setForeground(Color.WHITE);
        button.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, ColorScheme.DARK_GRAY_COLOR),
                new EmptyBorder(20, 4, 20, 4)
        ));

        JLabel timerLabel = new JLabel("3:30", SwingConstants.CENTER);
        timerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));

        button.addActionListener(e -> {
            switch (state){
                case WAITING:
                    CreateTimer(timerLabel);
                    button.setText("Pause");
                    state = ButtonState.STARTED;
                    return;
                case STARTED:
                    CancelTimer(timerLabel);
                    button.setText("Resume");
                    state = ButtonState.PAUSED;
                    break;
                case PAUSED:
                    CreateTimer(timerLabel);
                    button.setText("Reset");
                    state = ButtonState.RESUMED;
                    break;
                case RESUMED:
                    CancelTimer(timerLabel);
                    button.setText("Start");
                    state = ButtonState.WAITING;
                    break;
                default:
                    break;
            }
        });

        mainContent.add(timerLabel, BorderLayout.NORTH);
        mainContent.add(button, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);
    }
}