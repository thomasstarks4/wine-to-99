/*
 * Copyright (c) 2026, Zeno
 * All rights reserved.
 */
package com.winetoninetynine;

final class WineTrackerSnapshot
{
	private final boolean loggedIn;
	private final int cookingLevel;
	private final int currentXp;
	private final int fermentingWines;
	private final boolean bankContentsKnown;
	private final int bankGrapes;
	private final int bankJugsOfWater;
	private final int winesRemaining;
	private final long winesPerHour;
	private final long cookingXpPerHour;
	private final int sessionWines;
	private final long sessionXp;
	private final long sessionMillis;

	WineTrackerSnapshot(
		boolean loggedIn,
		int cookingLevel,
		int currentXp,
		int fermentingWines,
		boolean bankContentsKnown,
		int bankGrapes,
		int bankJugsOfWater,
		int winesRemaining,
		long winesPerHour,
		long cookingXpPerHour,
		int sessionWines,
		long sessionXp,
		long sessionMillis)
	{
		this.loggedIn = loggedIn;
		this.cookingLevel = cookingLevel;
		this.currentXp = currentXp;
		this.fermentingWines = fermentingWines;
		this.bankContentsKnown = bankContentsKnown;
		this.bankGrapes = bankGrapes;
		this.bankJugsOfWater = bankJugsOfWater;
		this.winesRemaining = winesRemaining;
		this.winesPerHour = winesPerHour;
		this.cookingXpPerHour = cookingXpPerHour;
		this.sessionWines = sessionWines;
		this.sessionXp = sessionXp;
		this.sessionMillis = sessionMillis;
	}

	boolean isLoggedIn()
	{
		return loggedIn;
	}

	int getCookingLevel()
	{
		return cookingLevel;
	}

	int getCurrentXp()
	{
		return currentXp;
	}

	int getFermentingWines()
	{
		return fermentingWines;
	}

	boolean isBankContentsKnown()
	{
		return bankContentsKnown;
	}

	int getBankGrapes()
	{
		return bankGrapes;
	}

	int getBankJugsOfWater()
	{
		return bankJugsOfWater;
	}

	int getWinesRemaining()
	{
		return winesRemaining;
	}

	long getWinesPerHour()
	{
		return winesPerHour;
	}

	long getCookingXpPerHour()
	{
		return cookingXpPerHour;
	}

	int getSessionWines()
	{
		return sessionWines;
	}

	long getSessionXp()
	{
		return sessionXp;
	}

	long getSessionMillis()
	{
		return sessionMillis;
	}
}
