/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.linux.services;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.linux.*;

public final class NmCli
{
    static private final String
	IN_USE = "IN-USE:",
	SECURITY = "SECURITY:",
	SSID = "SSID:";

    public interface Caller
    {
	String[] call(String[] args) throws IOException;
    }

static private final class Network implements WifiNetwork
{
    private final String name;
    Network(String name)
    {
	NullCheck.notNull(name, "name");
	this.name = name;
    }
    public String getName() { return name; }
    @Override public String toString() { return name; }
}

    private final Caller caller;

    public NmCli(Caller caller)
    {
	NullCheck.notNull(caller, "caller");
	this.caller = caller;
    }

    public NmCli()
    {
	this(createDefaultCaller());
    }

    public WifiNetwork[] scan() throws IOException
    {
	final List<WifiNetwork> res = new ArrayList<>();
	final String[] lines = caller.call(new String[]{"device", "wifi", "list"});
	String ssid = "", security = "", inUse = "";
	for (String l: lines)
	{
	    final String line = l.trim();
	    if (line.startsWith(IN_USE))
	    {
		inUse = line.substring(IN_USE.length()).trim();
		continue;
	    }
	    if (line.startsWith(SSID))
	    {
		ssid = line.substring(SSID.length()).trim();
		continue;
	    }
	    if (line.startsWith(SECURITY))
	    {
		security = line.substring(SECURITY .length()).trim();
		res.add(new Network(ssid));
		inUse = "";
		ssid = "";
		security = "";
		continue;
	    }
	}
	return res.toArray(new WifiNetwork[res.size()]);
    }

    static public Caller createDefaultCaller()
    {
	return (args)->{
	    final StringBuilder cmd = new StringBuilder();
	    cmd.append("nmcli -m multiline");
	    if (args != null)
		for(String a: args)
		    cmd.append(" ").append(BashProcess.escape(a));
	    final BashProcess p = new BashProcess(new String(cmd), EnumSet.of(BashProcess.Flags.ROOT));
	    p.run();
	    final int exitCode = p.waitFor();
	    if (exitCode != 0)
		throw new IOException("nmcli returned " + String.valueOf(exitCode));
	    return p.getOutput();
	};
    }
}
