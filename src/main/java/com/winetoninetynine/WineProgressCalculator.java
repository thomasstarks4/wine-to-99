/*
 * Copyright (c) 2026, Zeno
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES ARE DISCLAIMED.
 */
package com.winetoninetynine;

import net.runelite.api.Experience;

final class WineProgressCalculator
{
	static final int XP_PER_WINE = 200;
	static final int TARGET_XP = Experience.getXpForLevel(Experience.MAX_REAL_LEVEL);
	static final long SECONDS_PER_HOUR = 3_600L;

	private WineProgressCalculator()
	{
	}

	/**
	 * Returns the successful wines still required after optimistically counting the
	 * unfermented batch. If part of the batch fails, the value rises again when the
	 * fermenting count clears without the corresponding Cooking XP.
	 */
	static int winesRemaining(int currentXp, int fermentingWines)
	{
		int xpRemaining = Math.max(0, TARGET_XP - Math.max(0, currentXp));
		int successfulWinesRequired = (xpRemaining + XP_PER_WINE - 1) / XP_PER_WINE;
		return Math.max(0, successfulWinesRequired - Math.max(0, fermentingWines));
	}

	static long bankedXp(int fermentingWines)
	{
		return (long) Math.max(0, fermentingWines) * XP_PER_WINE;
	}

	/**
	 * Estimates the time needed to finish the goal at the current production rate.
	 * Returns {@code -1} until a production rate is available.
	 */
	static long secondsUntil99(int winesRemaining, long winesPerHour)
	{
		if (winesRemaining <= 0)
		{
			return 0;
		}

		if (winesPerHour <= 0)
		{
			return -1;
		}

		long totalWineSeconds = (long) winesRemaining * SECONDS_PER_HOUR;
		return (totalWineSeconds + winesPerHour - 1) / winesPerHour;
	}
}
