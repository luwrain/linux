/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.linux;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;

import static org.luwrain.util.FileUtils.*;

public final class BlockDevices
{
    static private final String LOG_COMPONENT = Linux.LOG_COMPONENT;
        static public final File SYS_BLOCK = new File("/sys/block");
            static public final File DEV = new File("/dev");

    public String[] getHardDrives()
    {
	final List<String> res = new ArrayList<>();
	final File[] dev = SYS_BLOCK.listFiles();
	if (dev == null)
	    return new String[0];
	final File devDir = new File("/dev");
	for(File f: dev)
	{
	    if (!f.isDirectory())
		continue;
	    final File removable = new File(f, "removable");
	    final File cap = new File(f, "capability");
	    try {
		if (new File(f, "loop").exists())
		    continue;
		if (new File(f, "dm").exists()) //Appears on crypt devices
		    continue;
		if (readTextFileSingleString(removable, "UTF-8").trim().equals("1"))
		    continue;
		final Integer capValue = Integer.parseInt(readTextFileSingleString(cap, "UTF-8").trim(), 16);
		if ((capValue.intValue() & 0x0010) == 0) //The device is down
		    continue;
		if ((capValue.intValue() & 0x0200) != 0) //Partition scanning is disabled. Used for loop devices in their default settings
		    continue;
	    }
	    catch(Exception e)
	    {
		Log.debug(LOG_COMPONENT, "exploring " + f.getAbsolutePath() + ": " + e.getClass().getName() + ": " + e.getMessage());
		e.printStackTrace();
		continue;
	    }
	    res.add(f.getName());
	}
	final String[] r = res.toArray(new String[res.size()]);
	Arrays.sort(r);
	return r;
    }

        public String getDeviceName(String dev)
    {
	NullCheck.notNull(dev, "dev");
	try {
	    return readTextFileSingleString(new File(new File(SYS_BLOCK, dev), "device/model"), "UTF-8").trim();
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to get the name of the device " + dev + ": " + e.getClass().getName() + ": " + e.getMessage());
	    e.printStackTrace();
	    return "";
	}
    }

    public String getDeviceSize(String dev)
    {
	NullCheck.notNull(dev, "dev");
	try {
	    final String sizeStr = readTextFileSingleString(new File(new File(SYS_BLOCK, dev), "size"), "UTF-8").trim();
	    final Long l = Long.parseLong(sizeStr);
	    return Long.toString(l.longValue() / 1048576) + "G";
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to get the name of the device " + dev + ": " + e.getClass().getName() + ": " + e.getMessage());
	    e.printStackTrace();
	    return "";
	}
    }



}
