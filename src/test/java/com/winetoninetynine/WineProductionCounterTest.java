/*
 * Copyright (c) 2026, Zeno
 * All rights reserved.
 */
package com.winetoninetynine;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WineProductionCounterTest
{
	@Test
	public void firstObservationOnlyEstablishesBaseline()
	{
		WineProductionCounter counter = new WineProductionCounter();
		assertEquals(0, counter.update(14));
		assertEquals(1, counter.update(15));
	}

	@Test
	public void countIncreaseRecordsNewlyMixedWines()
	{
		WineProductionCounter counter = new WineProductionCounter();
		counter.update(0);
		assertEquals(4, counter.update(4));
		assertEquals(10, counter.update(14));
	}

	@Test
	public void fermentationOrTransferDoesNotAddProduction()
	{
		WineProductionCounter counter = new WineProductionCounter();
		counter.update(0);
		counter.update(14);
		assertEquals(0, counter.update(14));
		assertEquals(0, counter.update(0));
	}

	@Test
	public void hopBaselineDoesNotDoubleCountExistingBatch()
	{
		WineProductionCounter counter = new WineProductionCounter();
		counter.update(0);
		counter.update(14);
		counter.resetBaseline();
		assertEquals(0, counter.update(14));
	}
}
