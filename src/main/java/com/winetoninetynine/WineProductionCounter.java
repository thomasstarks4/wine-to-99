/*
 * Copyright (c) 2026, sailo
 * All rights reserved.
 */
package com.winetoninetynine;

/**
 * Counts rises in the combined inventory-and-bank unfermented total. The first
 * observation after login or a world hop is a baseline, preventing an existing
 * batch from being counted again as newly mixed.
 */
final class WineProductionCounter
{
	private int previousCount;
	private boolean baselinePending = true;

	int update(int count)
	{
		int normalizedCount = Math.max(0, count);
		if (baselinePending)
		{
			previousCount = normalizedCount;
			baselinePending = false;
			return 0;
		}

		int newlyMixed = Math.max(0, normalizedCount - previousCount);
		previousCount = normalizedCount;
		return newlyMixed;
	}

	void resetBaseline()
	{
		previousCount = 0;
		baselinePending = true;
	}

	void clearKnownBatch()
	{
		previousCount = 0;
		baselinePending = false;
	}
}
