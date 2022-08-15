package com.settimer;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.border.EmptyBorder;
import javax.swing.*;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

@Slf4j
@Singleton
public class SetTimerPanel extends PluginPanel {
	private final SetTimerPlugin plugin;
	private final SetTimerConfig config;

	private static JLabel timerLabel;
	private static JButton button;

	@Inject
	public SetTimerPanel(SetTimerPlugin plugin, SetTimerConfig config)
	{
		this.plugin = plugin;
		this.config = config;

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

		button.addActionListener(e -> this.plugin.next());

		mainContent.add(timerLabel, BorderLayout.NORTH);
		mainContent.add(button, BorderLayout.SOUTH);

		add(mainContent, BorderLayout.CENTER);
	}

	public void setButtonText(String text)
	{
		button.setText(text);
	}

	public void setTimerText(String text)
	{
		timerLabel.setText(text);
	}

	public void reset()
	{
		setTimerText("3:30");
		setButtonText("Start");
	}
}