/*
 * Copyright (c) 2026, sailo
 * All rights reserved.
 */
package com.winetoninetynine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

final class WineTo99Panel extends PluginPanel
{
	private static final Color WINE_RED = new Color(132, 40, 63);
	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance(Locale.US);

	private final JLabel status = new JLabel("Log in to begin tracking", SwingConstants.CENTER);
	private final JLabel level = new JLabel("Cooking level --", SwingConstants.CENTER);
	private final JLabel xpRemaining = new JLabel("-- XP remaining", SwingConstants.CENTER);
	private final JProgressBar progress = new JProgressBar(0, 10_000);
	private final JLabel winesRemaining = valueLabel();
	private final JLabel fermenting = valueLabel();
	private final JLabel bankGrapes = valueLabel();
	private final JLabel bankJugsOfWater = valueLabel();
	private final JLabel winesPerHour = valueLabel();
	private final JLabel xpPerHour = valueLabel();
	private final JLabel timeUntil99 = valueLabel();
	private final JLabel session = new JLabel("Session starts with your first wine", SwingConstants.CENTER);
	private final JButton reset = new JButton("Reset session");

	WineTo99Panel(BufferedImage wineIcon, Runnable resetAction)
	{
		super(false);
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBackground(ColorScheme.DARK_GRAY_COLOR);
		content.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel title = new JLabel("Wine to 99", new ImageIcon(wineIcon), SwingConstants.CENTER);
		title.setAlignmentX(CENTER_ALIGNMENT);
		title.setFont(FontManager.getRunescapeBoldFont().deriveFont(Font.PLAIN, 18f));
		title.setIconTextGap(8);
		content.add(title);
		content.add(Box.createRigidArea(new Dimension(0, 8)));

		status.setAlignmentX(CENTER_ALIGNMENT);
		status.setForeground(Color.LIGHT_GRAY);
		content.add(status);
		content.add(Box.createRigidArea(new Dimension(0, 12)));

		JPanel goal = new JPanel();
		goal.setLayout(new BoxLayout(goal, BoxLayout.Y_AXIS));
		goal.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		goal.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.MEDIUM_GRAY_COLOR),
			new EmptyBorder(10, 8, 10, 8)));

		level.setAlignmentX(CENTER_ALIGNMENT);
		level.setFont(FontManager.getRunescapeBoldFont());
		goal.add(level);
		goal.add(Box.createRigidArea(new Dimension(0, 3)));

		xpRemaining.setAlignmentX(CENTER_ALIGNMENT);
		xpRemaining.setForeground(Color.LIGHT_GRAY);
		goal.add(xpRemaining);
		goal.add(Box.createRigidArea(new Dimension(0, 8)));

		progress.setAlignmentX(CENTER_ALIGNMENT);
		progress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		progress.setForeground(WINE_RED);
		progress.setBackground(ColorScheme.DARK_GRAY_COLOR);
		progress.setBorderPainted(false);
		progress.setStringPainted(true);
		goal.add(progress);
		content.add(goal);
		content.add(Box.createRigidArea(new Dimension(0, 12)));

		JPanel metrics = new JPanel(new GridLayout(0, 1, 0, 1));
		metrics.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		metrics.setBorder(new EmptyBorder(4, 8, 4, 8));
		metrics.add(metricRow("Successful wines to 99", winesRemaining));
		metrics.add(metricRow("Currently fermenting", fermenting));
		metrics.add(metricRow("Grapes in bank", bankGrapes));
		metrics.add(metricRow("Jugs of Water in bank", bankJugsOfWater));
		metrics.add(metricRow("Wines / hour", winesPerHour));
		metrics.add(metricRow("Cooking XP / hour", xpPerHour));
		metrics.add(metricRow("Time until 99", timeUntil99));
		content.add(metrics);
		content.add(Box.createRigidArea(new Dimension(0, 10)));

		session.setAlignmentX(CENTER_ALIGNMENT);
		session.setForeground(Color.LIGHT_GRAY);
		content.add(session);
		content.add(Box.createRigidArea(new Dimension(0, 8)));

		reset.setAlignmentX(CENTER_ALIGNMENT);
		reset.setFocusable(false);
		reset.addActionListener(event -> resetAction.run());
		content.add(reset);

		winesRemaining.setToolTipText("Successful wines still needed after subtracting the current fermenting batch.");
		fermenting.setToolTipText("Unfermented wine seen in your inventory and most recently opened bank.");
		bankGrapes.setToolTipText("Most recently observed grape count. Open your bank to refresh it.");
		bankJugsOfWater.setToolTipText("Most recently observed jug-of-water count. Open your bank to refresh it.");
		winesPerHour.setToolTipText("All wines mixed this session, whether they are still fermenting or have finished.");
		xpPerHour.setToolTipText("Wines mixed per hour multiplied by 200 XP. Approximate below level 68.");
		timeUntil99.setToolTipText("Estimated time to 99 at your current wines-per-hour rate (DD:HH:MM:SS).");

		add(content, BorderLayout.NORTH);
		showLoggedOut();
	}

	private static JLabel valueLabel()
	{
		JLabel label = new JLabel("--", SwingConstants.RIGHT);
		label.setFont(FontManager.getRunescapeBoldFont());
		return label;
	}

	private static JPanel metricRow(String name, JLabel value)
	{
		JPanel row = new JPanel(new BorderLayout());
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setBorder(new EmptyBorder(6, 0, 6, 0));
		JLabel nameLabel = new JLabel(name);
		nameLabel.setForeground(Color.LIGHT_GRAY);
		row.add(nameLabel, BorderLayout.WEST);
		row.add(value, BorderLayout.EAST);
		return row;
	}

	void update(WineTrackerSnapshot snapshot)
	{
		if (!snapshot.isLoggedIn())
		{
			showLoggedOut();
			return;
		}

		status.setText(snapshot.getCookingLevel() >= 99 ? "Goal reached!" : "Live Cooking progress");
		status.setForeground(snapshot.getCookingLevel() >= 99 ? new Color(90, 200, 120) : Color.LIGHT_GRAY);
		level.setText("Cooking level " + snapshot.getCookingLevel());

		int remainingXp = Math.max(0, WineProgressCalculator.TARGET_XP - snapshot.getCurrentXp());
		xpRemaining.setText(NUMBER_FORMAT.format(remainingXp) + " XP remaining");

		double fraction = Math.max(0.0, Math.min(1.0,
			snapshot.getCurrentXp() / (double) WineProgressCalculator.TARGET_XP));
		progress.setValue((int) Math.round(fraction * 10_000));
		progress.setString(String.format(Locale.US, "%.1f%% to 99", fraction * 100));

		winesRemaining.setText(NUMBER_FORMAT.format(snapshot.getWinesRemaining()));
		fermenting.setText(NUMBER_FORMAT.format(snapshot.getFermentingWines()));
		bankGrapes.setText(snapshot.isBankContentsKnown()
			? NUMBER_FORMAT.format(snapshot.getBankGrapes()) : "--");
		bankJugsOfWater.setText(snapshot.isBankContentsKnown()
			? NUMBER_FORMAT.format(snapshot.getBankJugsOfWater()) : "--");
		winesPerHour.setText(NUMBER_FORMAT.format(snapshot.getWinesPerHour()));
		String estimatePrefix = snapshot.getCookingLevel() < 68 ? "~" : "";
		xpPerHour.setText(estimatePrefix + NUMBER_FORMAT.format(snapshot.getCookingXpPerHour()));
		long secondsUntil99 = WineProgressCalculator.secondsUntil99(
			snapshot.getWinesRemaining(), snapshot.getWinesPerHour());
		timeUntil99.setText(secondsUntil99 < 0 ? "--" : formatGoalDuration(secondsUntil99));

		session.setText("<html><center>Session: "
			+ NUMBER_FORMAT.format(snapshot.getSessionWines()) + " wines &bull; "
			+ estimatePrefix + NUMBER_FORMAT.format(snapshot.getSessionXp()) + " XP<br>"
			+ formatDuration(snapshot.getSessionMillis()) + " active</center></html>");
		reset.setEnabled(true);
	}

	private void showLoggedOut()
	{
		status.setText("Log in to begin tracking");
		status.setForeground(Color.LIGHT_GRAY);
		level.setText("Cooking level --");
		xpRemaining.setText("-- XP remaining");
		progress.setValue(0);
		progress.setString("Waiting for player");
		winesRemaining.setText("--");
		fermenting.setText("--");
		bankGrapes.setText("--");
		bankJugsOfWater.setText("--");
		winesPerHour.setText("--");
		xpPerHour.setText("--");
		timeUntil99.setText("--");
		session.setText("Session starts with your first wine");
		reset.setEnabled(false);
	}

	private static String formatDuration(long millis)
	{
		long totalSeconds = Math.max(0, millis / 1_000);
		long hours = totalSeconds / 3_600;
		long minutes = totalSeconds % 3_600 / 60;
		long seconds = totalSeconds % 60;
		return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
	}

	private static String formatGoalDuration(long totalSeconds)
	{
		long nonNegativeSeconds = Math.max(0, totalSeconds);
		long days = nonNegativeSeconds / 86_400;
		long hours = nonNegativeSeconds % 86_400 / 3_600;
		long minutes = nonNegativeSeconds % 3_600 / 60;
		long seconds = nonNegativeSeconds % 60;
		return String.format(Locale.US, "%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
	}
}
