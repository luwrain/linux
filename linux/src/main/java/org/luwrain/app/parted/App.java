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

package org.luwrain.app.parted;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.app.base.*;
import org.luwrain.linux.*;

public final class App extends AppBase<Strings> implements MonoApp
{
    private MainLayout mainLayout = null;
    final List<Parted> disks = new ArrayList();
    final List<String> parts = new ArrayList<>();

    public App() { super(Strings.NAME, Strings.class, "luwrain.linux.parted"); }

    @Override protected AreaLayout onAppInit()
    {
	loadDisks();
	this.mainLayout = new MainLayout(this);
	setAppName(getStrings().appName());
	return mainLayout.getAreaLayout();
    }

    private void loadDisks()
    {
	final BlockDevices blockDevices = new BlockDevices();
	for(String dev: blockDevices.getHardDrives())
	{
	    final File devFile = new File(BlockDevices.DEV, dev);
	    disks.add(new Parted(devFile.getPath()));
	}
	try {
	    for(Parted p: disks)
		p.init();
	}
	catch(IOException e)
	{
	    crash(e);
	}
    }

    @Override public boolean onEscape()
    {
	closeApp();
	return true;
    }

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }
}
