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
import java.util.regex.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.linux.*;

public final class UdisksCliMonitor implements BashProcess.Listener
{
    static private final String
	LOG_COMPONENT = "udisksctl";

    static private final Pattern
	RE_ADDED = Pattern.compile("^\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d:\\sAdded\\s(.*)$"),
	RE_REMOVED = Pattern.compile("^\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d:\\sRemoved\\s(.*)$");

    static private final String
	OBJ_DRIVES = "/org/freedesktop/UDisks2/drives/",
	IFACE_DRIVE = "org.freedesktop.UDisks2.Drive",
	IFACE_BLOCK = "org.freedesktop.UDisks2.Block",
	PREFIX_REMOVABLE = "Removable:",
	PREFIX_SIZE = "Size:",
	PREFIX_MODEL = "Model:",
	PREFIX_DEVICE = "Device:",
	PREFIX_DRIVE = "Drive:";

    private final Luwrain luwrain;
    private final BashProcess p ;
    private final Map<String, Disk> disks = new HashMap<>();
    private Disk activeDisk = null;

    public UdisksCliMonitor(Luwrain luwrain) throws IOException
    {
	this.luwrain = luwrain;
	p = launch();
	    }

    BashProcess launch() throws IOException
    {
	final BashProcess b = new BashProcess("udisksctl monitor", null, EnumSet.noneOf(BashProcess.Flags.class), this);
	b.run();
	return b;
    }

    		@Override public void onOutputLine(String line)
		{
		    //		    Log.debug(LOG_COMPONENT, line);
		    Matcher m = RE_ADDED.matcher(line);
		    if (m.find())
		    {
			final String obj = m.group(1).trim();
			if (obj.startsWith(OBJ_DRIVES))
			{
			    activeDisk = new Disk();
			    disks.put(obj, activeDisk);
			    Log.debug(LOG_COMPONENT, "added new disk: " + obj);
			    return;
			}
		    }
		}

	@Override public void onErrorLine(String line)
	{
	}

	@Override public void onFinishing(int exitCode)
	{
	}

    static private final class Disk
    {
    }
}
