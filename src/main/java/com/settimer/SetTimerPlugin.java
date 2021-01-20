package com.settimer;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.Image;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
	name = "Set Timer",
	description = "Panel to show TzKal-Zuk timer",
	tags = {"timer", "inferno", "pvm"}
)
public class SetTimerPlugin extends Plugin
{
	public static Image ICON;
	private SetTimerPanel panel;
	private boolean inInferno = false;
	private static final String CONFIG_GROUP = "settimer";
	private static final String HIDE_KEY = "hide";
	private static final int INFERNO_REGION_ID = 9043;

	@Getter(AccessLevel.PACKAGE)
	private NavigationButton navButton;

	@Inject
	private Client client;

	@Inject
	private SetTimerConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	@Override
	protected void startUp() throws Exception
	{
		panel = injector.getInstance(SetTimerPanel.class);
		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "/tzkal-zuk.png");
		ICON = new ImageIcon(icon).getImage();
		navButton = NavigationButton.builder()
				.tooltip("Set Timer")
				.icon(icon)
				.priority(6)
				.panel(panel)
				.build();
		if (inInferno || !config.hide())
		{
			clientToolbar.addNavigation(navButton);
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick) {
		inInferno = isInInferno();
		if (!inInferno && config.hide())
		{
			clientToolbar.removeNavigation(navButton);
		}
		else if (inInferno && config.hide())
		{
			clientToolbar.addNavigation(navButton);
		}
		else if (!inInferno && !config.hide())
		{
			clientToolbar.addNavigation(navButton);
		}
		else if (inInferno && !config.hide())
		{
			clientToolbar.addNavigation(navButton);
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		return;
	}

	@Provides
	SetTimerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SetTimerConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (!event.getGroup().equals(CONFIG_GROUP)) {
			return;
		}

		if (event.getKey().equals(HIDE_KEY)) {
			clientToolbar.addNavigation(navButton);
		}
	}

	private boolean isInInferno()
	{
		return client.getMapRegions() != null && ArrayUtils.contains(client.getMapRegions(), INFERNO_REGION_ID);
	}
}
