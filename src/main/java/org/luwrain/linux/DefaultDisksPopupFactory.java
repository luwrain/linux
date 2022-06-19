/*
   Copyright 2012-2022 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.linux.services.*;
import org.luwrain.popups.*;

public final class DefaultDisksPopupFactory implements DisksPopup.Factory
    {
	private UdisksCliMonitor monitor;
	DefaultDisksPopupFactory(UdisksCliMonitor monitor) { this.monitor = monitor; }

	@Override public DisksPopup.Disks newDisks(Luwrain luwrain)
	{
	    return new DisksImpl();
	}

private final class DisksImpl implements DisksPopup.Disks
	{
	    @Override public DisksPopup.Disk[] getDisks()
	    {
		final List<DiskImpl> res = new ArrayList<>();
		monitor.enumBlockDevices((m)->{
			final String
			obj = m.containsKey("obj")?m.get("obj").toString():"",
			device = m.containsKey("device")?m.get("device").toString():"",
						fsType = m.containsKey("fsType")?m.get("fsType").toString():"";
			if (!fsType.trim().isEmpty())
			res.add(new DiskImpl(device));
		    });

		return res.toArray(new DisksPopup.Disk[res.size()]);
	    }
	}

	private final class DiskImpl implements DisksPopup.Disk
	{
	    final String title;
	    DiskImpl(String title)
	    {
		this.title = title;
	    }
	    	@Override public File activate()
	    {
		final UdisksCli u = new UdisksCli();
		try {
		return u.mount(title);
		}
		catch(Throwable e)
		{
		     throw new RuntimeException(e);
		     }
	    }
	    @Override public String toString()
	    {
		return title;
	    }
	}
    }
