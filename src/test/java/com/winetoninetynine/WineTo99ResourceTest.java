/*
 * Copyright (c) 2026, Zeno
 * All rights reserved.
 */
package com.winetoninetynine;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class WineTo99ResourceTest
{
	@Test
	public void bundledIconUsesPluginSpecificResourcePath()
	{
		assertNotNull(WineTo99Plugin.class.getResource("wine_to_99_icon.png"));
	}
}