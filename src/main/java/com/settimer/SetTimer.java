package com.settimer;

import lombok.Getter;
import lombok.ToString;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.plugins.Plugin;

import java.awt.*;
import java.awt.image.BufferedImage;

import java.util.Timer;
import java.util.TimerTask;

@Getter
@ToString
public class SetTimer extends InfoBox
{
    private static Timer timer;
    private static int currentTime   = 3 * 60 + 30;
    private static final int setTime = 3 * 60 + 30;
    private static final int jadTime = 1 * 60 + 45;

    public SetTimer(BufferedImage image, Plugin plugin)
    {
        super(image, plugin);
    }

    public void CreateTimer()
    {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (currentTime-- == 0)
                {
                    currentTime = setTime;
                }
                SetTimerPanel.UpdateTimerLabel(currentTime);
            }
        }, 0, 1000);
    }

    public void CancelTimer()
    {
        timer.cancel();
        switch (SetTimerPanel.state)
        {
            case STARTED:
                currentTime += jadTime;
                break;
            case WAITING:
            case RESUMED:
                currentTime = setTime;
                break;
            case PAUSED:
            default:
                break;
        }
        SetTimerPanel.UpdateTimerLabel(currentTime);
    }

    @Override
    public String getText()
    {
        final int minutes = currentTime / 60;
        return String.format("%01d:%02d", minutes, currentTime % 60);
    }

    @Override
    public Color getTextColor()
    {
        if (currentTime < 10)
        {
            return Color.RED.brighter();
        }

        return Color.WHITE;
    }
}