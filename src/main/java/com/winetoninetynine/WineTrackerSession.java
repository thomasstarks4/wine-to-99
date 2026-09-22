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

final class WineTrackerSession
{
	private boolean started;
	private long elapsedMillis;
	private int winesMixed;

	void start()
	{
		started = true;
	}

	void advance(long millis)
	{
		if (started && millis > 0)
		{
			elapsedMillis += millis;
		}
	}

	void recordWinesMixed(int wines)
	{
		if (wines > 0)
		{
			start();
			winesMixed += wines;
		}
	}

	void reset()
	{
		started = false;
		elapsedMillis = 0;
		winesMixed = 0;
	}

	long getCookingXpPerHour()
	{
		return getWinesPerHour() * WineProgressCalculator.XP_PER_WINE;
	}

	long getWinesPerHour()
	{
		return hourlyRate(winesMixed);
	}

	private long hourlyRate(long value)
	{
		if (!started || elapsedMillis <= 0)
		{
			return 0;
		}

		return Math.round(value * 3_600_000.0 / elapsedMillis);
	}

	long getElapsedMillis()
	{
		return elapsedMillis;
	}

	long getPotentialCookingXp()
	{
		return (long) winesMixed * WineProgressCalculator.XP_PER_WINE;
	}

	int getWinesMixed()
	{
		return winesMixed;
	}
}
