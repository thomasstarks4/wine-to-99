/*
 * Copyright (c) 2026, sailo
 * All rights reserved.
 */
package com.winetoninetynine;

import static org.junit.Assert.assertEquals;

import net.runelite.api.Experience;
import org.junit.Test;

public class WineProgressCalculatorTest
{
	@Test
	public void exactXpBoundaryNeedsNoWines()
	{
		assertEquals(0, WineProgressCalculator.winesRemaining(Experience.getXpForLevel(99), 0));
	}

	@Test
	public void partialWineXpRoundsUp()
	{
		assertEquals(1, WineProgressCalculator.winesRemaining(Experience.getXpForLevel(99) - 1, 0));
		assertEquals(2, WineProgressCalculator.winesRemaining(Experience.getXpForLevel(99) - 201, 0));
	}

	@Test
	public void fermentingBatchIsSubtracted()
	{
		int currentXp = Experience.getXpForLevel(99) - 2_000;
		assertEquals(10, WineProgressCalculator.winesRemaining(currentXp, 0));
		assertEquals(6, WineProgressCalculator.winesRemaining(currentXp, 4));
		assertEquals(0, WineProgressCalculator.winesRemaining(currentXp, 20));
	}

	@Test
	public void level68To99MatchesKnownSuccessfulWineCount()
	{
		assertEquals(62_147,
			WineProgressCalculator.winesRemaining(Experience.getXpForLevel(68), 0));
	}

	@Test
	public void timeUntil99UsesCurrentWineRate()
	{
		assertEquals(90_000, WineProgressCalculator.secondsUntil99(50_000, 2_000));
	}

	@Test
	public void timeUntil99RoundsUpToAWholeSecond()
	{
		assertEquals(2, WineProgressCalculator.secondsUntil99(1, 2_399));
	}

	@Test
	public void timeUntil99IsUnavailableWithoutAProductionRate()
	{
		assertEquals(-1, WineProgressCalculator.secondsUntil99(10, 0));
	}

	@Test
	public void timeUntil99IsZeroWhenGoalIsComplete()
	{
		assertEquals(0, WineProgressCalculator.secondsUntil99(0, 0));
	}
}
