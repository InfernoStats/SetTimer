package com.settimer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("settimer")
public interface SetTimerConfig extends Config
{
	@ConfigItem(
		keyName = "hide",
		name = "Hide when outside of the Inferno",
		description = "Don't show the button in the sidebar when you're not in the Inferno"
	)
	default boolean hide()
	{
		return true;
	}
}
