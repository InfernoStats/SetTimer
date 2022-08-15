package com.settimer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup(SetTimerConfig.GROUP)
public interface SetTimerConfig extends Config
{
	String GROUP = "settimer";

	@ConfigItem(
		keyName = "hide",
		name = "Hide when outside of the Inferno",
		description = "Don't show the button in the sidebar when you're not in the Inferno"
	)
	default boolean hide()
	{
		return true;
	}

	@ConfigItem(
		keyName = "infobox",
		name = "Show infobox when timer is active",
		description = "Display an infobox while the timer is active"
	)
	default boolean infobox()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hotkey",
		name = "Timer Hotkey",
		description = "Pressing this key combination will start and stop the timer",
		position = 1
	)
	default Keybind timerHotkey() {
		return new Keybind(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
	}
}
