/*
 * Copyright (c) 2026, Zeno
 * All rights reserved.
 */
package com.winetoninetynine;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WineTrackerSessionTest
{
	@Test
	public void ratesUseActiveSessionTime()
	{
		WineTrackerSession session = new WineTrackerSession();
		session.recordWinesMixed(1_000);
		session.advance(30 * 60 * 1_000L);

		assertEquals(400_000, session.getCookingXpPerHour());
		assertEquals(2_000, session.getWinesPerHour());
	}

	@Test
	public void resetClearsEverything()
	{
		WineTrackerSession session = new WineTrackerSession();
		session.recordWinesMixed(14);
		session.advance(60_000);
		session.reset();

		assertEquals(0, session.getPotentialCookingXp());
		assertEquals(0, session.getWinesMixed());
		assertEquals(0, session.getElapsedMillis());
		assertEquals(0, session.getCookingXpPerHour());
		assertEquals(0, session.getWinesPerHour());
	}

	@Test
	public void eachMixedWineIsWorthTwoHundredPotentialXp()
	{
		WineTrackerSession session = new WineTrackerSession();
		session.recordWinesMixed(14);
		assertEquals(2_800, session.getPotentialCookingXp());
	}

	@Test
	public void displayedXpRateIsExactlyTwoHundredTimesWineRate()
	{
		WineTrackerSession session = new WineTrackerSession();
		session.recordWinesMixed(14);
		session.advance(19_321);
		assertEquals(session.getWinesPerHour() * 200, session.getCookingXpPerHour());
	}

	@Test
	public void timeDoesNotAdvanceBeforeFirstActivity()
	{
		WineTrackerSession session = new WineTrackerSession();
		session.advance(60_000);
		assertEquals(0, session.getElapsedMillis());
	}
}
