/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.wifi;

import org.luwrain.core.*;
import org.luwrain.util.*;

public interface Settings
{
    static public final String NETWORK_PATH = "/org/luwrain/network";
    static public final String NETWORKS_PATH = "/org/luwrain/network/wifi-networks";

public interface Network
{
    String getDefaultWifiNetwork(String defValue);
    void setDefaultWifiNetwork(String value);
}

    public interface WifiNetwork
    {
	String getPassword(String defValue);
	void setPassword(String value);
    }

    static public Network createNetwork(Registry registry)
    {
	NullCheck.notNull(registry, "registry");
	return RegistryProxy.create(registry, NETWORK_PATH, Network.class);
    }

    static public WifiNetwork createWifiNetwork(Registry registry, org.luwrain.linux.wifi.Network network)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notNull(network, "network");
	return RegistryProxy.create(registry, Registry.join(NETWORKS_PATH, makeRegistryName(network.name)), WifiNetwork.class);
    }

    static String makeRegistryName(String value)
    {
	return value.replaceAll("/", "_").replaceAll("\n", "_").replaceAll(" ", "_");
    }
}
