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
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.Image;
import java.awt.event.KeyEvent;
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
	private static final String INFOBOX_KEY = "infobox";
	private static final int INFERNO_REGION_ID = 9043;

	@Getter(AccessLevel.PACKAGE)
	private NavigationButton navButton;

	@Inject
	private Client client;

	@Inject
	private SetTimerConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ItemManager itemManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private KeyManager keyManager;

	private final HotkeyListener setTimerKeyListener = new HotkeyListener(() -> config.timerHotkey()) {
		@Override
		public void keyPressed(KeyEvent e) {
			if (config.timerHotkey().matches(e)) {
				if (!inInferno && config.hide())
				{
					return;
				}
				SetTimerPanel.AdvanceState();
			}
		}
	};

	@Override
	protected void startUp() throws Exception
	{
		final BufferedImage panelIcon = ImageUtil.getResourceStreamFromClass(getClass(), "/tzkal-zuk.png");

		panel = injector.getInstance(SetTimerPanel.class);
		ICON = new ImageIcon(panelIcon).getImage();
		navButton = NavigationButton.builder()
				.tooltip("Set Timer")
				.icon(panelIcon)
				.priority(6)
				.panel(panel)
				.build();
		if (inInferno || !config.hide())
		{
			clientToolbar.addNavigation(navButton);
		}

		keyManager.registerKeyListener(setTimerKeyListener);
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		inInferno = isInInferno();
		if (!inInferno && config.hide())
		{
			clientToolbar.removeNavigation(navButton);
		}
		else
		{
			clientToolbar.addNavigation(navButton);
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		keyManager.unregisterKeyListener(setTimerKeyListener);
		clientToolbar.removeNavigation(navButton);
		infoBoxManager.removeIf(SetTimer.class::isInstance);
		panel.Reset();
	}

	@Provides
	SetTimerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SetTimerConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(CONFIG_GROUP))
		{
			return;
		}

		if (event.getKey().equals(HIDE_KEY))
		{
			clientToolbar.addNavigation(navButton);
		}
		else if (event.getKey().equals(INFOBOX_KEY))
		{
			if (config.infobox() && SetTimerPanel.isActive())
			{
				infoBoxManager.addInfoBox(SetTimerPanel.setTimer);
			}
			else
			{
				infoBoxManager.removeIf(SetTimer.class::isInstance);
			}
		}
	}

	private boolean isInInferno()
	{
		return client.getMapRegions() != null && ArrayUtils.contains(client.getMapRegions(), INFERNO_REGION_ID);
	}
}
