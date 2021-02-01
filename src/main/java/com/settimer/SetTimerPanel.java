package com.settimer;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.border.EmptyBorder;
import javax.swing.*;

import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@Slf4j
@Singleton
public class SetTimerPanel extends PluginPanel {
    public static SetTimer setTimer;
    private final SetTimerPlugin plugin;
    private final SetTimerConfig config;

    private static JLabel timerLabel;
    private static JButton button;
    public static ButtonState state = ButtonState.WAITING;

    enum ButtonState {
        WAITING (1 << 0),
        STARTED (1 << 1),
        PAUSED (1 << 2),
        RESUMED  (1 << 3);

        private int state;

        ButtonState(int state)
        {
            this.state = state;
        }
    };

    @Inject
    public SetTimerPanel(SetTimerPlugin plugin, SetTimerConfig config, ItemManager itemManager, InfoBoxManager infoBoxManager)
    {
        this.plugin = plugin;
        this.config = config;
        this.setTimer = new SetTimer(itemManager.getImage(ItemID.TZREKZUK), plugin);

        setLayout(new BorderLayout(0, 4));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(new EmptyBorder(8, 8, 8, 8));
        JPanel mainContent = new JPanel(new BorderLayout());

        button = new JButton("Start");
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
        button.setForeground(Color.WHITE);
        button.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, ColorScheme.DARK_GRAY_COLOR),
                new EmptyBorder(20, 4, 20, 4)
        ));

        timerLabel = new JLabel("3:30", SwingConstants.CENTER);
        timerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));

        button.addActionListener(e -> {
            switch (state){
                case WAITING:
                    setTimer.CreateTimer();
                    button.setText("Pause");
                    state = ButtonState.STARTED;
                    if (config.infobox())
                    {
                        infoBoxManager.addInfoBox(setTimer);
                    }
                    return;
                case STARTED:
                    setTimer.CancelTimer();
                    button.setText("Resume");
                    state = ButtonState.PAUSED;
                    break;
                case PAUSED:
                    setTimer.CreateTimer();
                    button.setText("Reset");
                    state = ButtonState.RESUMED;
                    break;
                case RESUMED:
                    setTimer.CancelTimer();
                    button.setText("Start");
                    state = ButtonState.WAITING;
                    infoBoxManager.removeIf(SetTimer.class::isInstance);
                    break;
                default:
                    break;
            }
        });

        mainContent.add(timerLabel, BorderLayout.NORTH);
        mainContent.add(button, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);
    }

    public static boolean isActive()
    {
        return state != ButtonState.WAITING;
    }

    public static void AdvanceState()
    {
        button.doClick();
    }

    public static void UpdateTimerLabel(int seconds)
    {
        final int minutes = seconds / 60;
        timerLabel.setText(setTimer.getText());
    }

    public static void Reset()
    {
        state = ButtonState.WAITING;
        setTimer.CancelTimer();
        button.setText("Start");
    }
}