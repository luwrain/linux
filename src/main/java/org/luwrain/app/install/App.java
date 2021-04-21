/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.install;

import java.net.*;
import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.app.base.*;
import org.luwrain.util.*;

public final class App extends AppBase<Strings>
{
    static final String LOG_COMPONENT = "install";
    
    private MainLayout mainLayout = null;

    public App()
    {
	super(Strings.NAME, Strings.class);
    }

    @Override protected boolean onAppInit()
    {
	this.mainLayout = new MainLayout(this);
	return true;
    }

    File[] getDevices()
    {
	final List<File> res = new ArrayList();
	final File[] dev = new File("/sys/block").listFiles();
	if (dev == null)
	    return new File[0];
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
		if (FileUtils.readTextFileSingleString(removable, "UTF-8").trim().equals("1"))
		    continue;
		final Integer capValue = Integer.parseInt(FileUtils.readTextFileSingleString(cap, "UTF-8").trim(), 16);
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
	    res.add(new File(devDir, f.getName()));
	}
	final File[] r = res.toArray(new File[res.size()]);
	Arrays.sort(r);
	return r;
    }

    @Override public boolean onEscape(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	closeApp();
	return true;
    }

	@Override public AreaLayout getDefaultAreaLayout()
    {
	return this.mainLayout.getAreaLayout();
    }
}
