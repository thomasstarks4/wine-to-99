/*
 * Copyright (c) 2026, sailo
 * All rights reserved.
 */
package com.winetoninetynine;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class WineTo99PluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(WineTo99Plugin.class);
		RuneLite.main(args);
	}
}
