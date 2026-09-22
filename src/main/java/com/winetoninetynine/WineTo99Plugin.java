/*
 * Copyright (c) 2026, sailo
 * All rights reserved.
 */
package com.winetoninetynine;

import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;

@Slf4j
@PluginDescriptor(
	name = "Wine to 99",
	description = "Tracks wines remaining to 99 Cooking, fermenting wines, and session rates",
	tags = {"wine", "cooking", "ferment", "xp", "99", "skilling", "tracker"}
)
public class WineTo99Plugin extends Plugin
{
	private static final int FERMENT_DURATION_TICKS = 21;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ItemManager itemManager;

	private final WineTrackerSession session = new WineTrackerSession();
	private final WineProductionCounter productionCounter = new WineProductionCounter();

	private WineTo99Panel panel;
	private NavigationButton navigationButton;
	private int inventoryFermenting;
	private int bankFermenting;
	private int bankGrapes;
	private int bankJugsOfWater;
	private boolean bankContentsKnown;
	private int fermentEndTick = -1;
	private Instant lastSessionTick;

	@Override
	protected void startUp()
	{
		resetAllState();

		WineTo99Panel newPanel = new WineTo99Panel(
			itemManager.getImage(ItemID.JUG_WINE),
			() -> clientThread.invoke(this::resetSession));
		NavigationButton newNavigationButton = NavigationButton.builder()
			.tooltip("Wine to 99")
			.icon(itemManager.getImage(ItemID.JUG_WINE))
			.priority(5)
			.panel(newPanel)
			.build();

		panel = newPanel;
		navigationButton = newNavigationButton;
		SwingUtilities.invokeLater(() -> clientToolbar.addNavigation(newNavigationButton));

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			lastSessionTick = Instant.now();
		}
		refreshPanel();
		log.debug("Wine to 99 started");
	}

	@Override
	protected void shutDown()
	{
		NavigationButton oldNavigationButton = navigationButton;
		if (oldNavigationButton != null)
		{
			SwingUtilities.invokeLater(() -> clientToolbar.removeNavigation(oldNavigationButton));
		}

		panel = null;
		navigationButton = null;
		resetAllState();
		log.debug("Wine to 99 stopped");
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		ItemContainer container = event.getItemContainer();
		if (event.getContainerId() == InventoryID.INV)
		{
			inventoryFermenting = container.count(ItemID.JUG_UNFERMENTED_WINE);
		}
		else if (event.getContainerId() == InventoryID.BANK)
		{
			bankFermenting = container.count(ItemID.JUG_UNFERMENTED_WINE);
			bankGrapes = container.count(ItemID.GRAPES);
			bankJugsOfWater = container.count(ItemID.JUG_WATER);
			bankContentsKnown = true;
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		if (event.getSkill() == Skill.COOKING)
		{
			refreshPanel();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		advanceSessionClock();
		int currentTick = client.getTickCount();
		int fermenting = totalFermenting();
		int newlyMixed = productionCounter.update(fermenting);

		if (newlyMixed > 0)
		{
			session.recordWinesMixed(newlyMixed);
			fermentEndTick = currentTick + FERMENT_DURATION_TICKS;
		}
		else if (fermenting > 0 && fermentEndTick < 0)
		{
			fermentEndTick = currentTick + FERMENT_DURATION_TICKS;
		}

		if (fermentEndTick >= 0 && currentTick >= fermentEndTick)
		{
			inventoryFermenting = 0;
			bankFermenting = 0;
			fermenting = 0;
			productionCounter.clearKnownBatch();
		}

		refreshPanel();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		GameState state = event.getGameState();
		if (state == GameState.LOGGED_IN)
		{
			lastSessionTick = Instant.now();
		}
		else
		{
			lastSessionTick = null;
			if (state == GameState.HOPPING)
			{
				resetFermentingState();
			}
			else if (state == GameState.LOGIN_SCREEN)
			{
				resetAllState();
			}
		}

		refreshPanel();
	}

	private void advanceSessionClock()
	{
		Instant now = Instant.now();
		if (lastSessionTick != null)
		{
			session.advance(Duration.between(lastSessionTick, now).toMillis());
		}
		lastSessionTick = now;
	}

	private void resetSession()
	{
		session.reset();
		if (totalFermenting() > 0)
		{
			session.start();
		}
		lastSessionTick = client.getGameState() == GameState.LOGGED_IN ? Instant.now() : null;
		refreshPanel();
	}

	private void resetAllState()
	{
		session.reset();
		resetFermentingState();
		bankGrapes = 0;
		bankJugsOfWater = 0;
		bankContentsKnown = false;
		lastSessionTick = null;
	}

	private void resetFermentingState()
	{
		inventoryFermenting = 0;
		bankFermenting = 0;
		productionCounter.resetBaseline();
		fermentEndTick = -1;
	}

	private int totalFermenting()
	{
		return inventoryFermenting + bankFermenting;
	}

	private void refreshPanel()
	{
		WineTo99Panel currentPanel = panel;
		if (currentPanel == null)
		{
			return;
		}

		boolean loggedIn = client.getGameState() == GameState.LOGGED_IN;
		int currentXp = loggedIn ? client.getSkillExperience(Skill.COOKING) : 0;
		int cookingLevel = loggedIn ? client.getRealSkillLevel(Skill.COOKING) : 0;
		int fermenting = loggedIn ? totalFermenting() : 0;
		WineTrackerSnapshot snapshot = new WineTrackerSnapshot(
			loggedIn,
			cookingLevel,
			currentXp,
			fermenting,
			bankContentsKnown,
			bankGrapes,
			bankJugsOfWater,
			WineProgressCalculator.winesRemaining(currentXp, fermenting),
			session.getWinesPerHour(),
			session.getCookingXpPerHour(),
			session.getWinesMixed(),
			session.getPotentialCookingXp(),
			session.getElapsedMillis());
		SwingUtilities.invokeLater(() -> currentPanel.update(snapshot));
	}
}
