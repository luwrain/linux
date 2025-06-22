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

package org.luwrain.app.wifi;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.app.base.*;
import org.luwrain.linux.*;
import org.luwrain.linux.services.*;

public final class App extends AppBase<Strings> implements MonoApp
{
    final List<WifiNetwork> networks = new ArrayList<>();
    final NmCli nmCli = new NmCli();
    private MainLayout mainLayout = null;
    private Conv conv = null;

    public App() { super(Strings.NAME, Strings.class, "luwrain.linux.wifi"); }

    @Override protected AreaLayout onAppInit()
    {
	this.conv = new Conv(this);
	this.mainLayout = new MainLayout(this);
	setAppName(getStrings().appName());
	updateNetworkList();
	return mainLayout.getAreaLayout();
    }

    boolean updateNetworkList()
    {
	final TaskId taskId = newTaskId();
	return runTask(taskId, ()->{
		final WifiNetwork[] n = nmCli.scan();
		finishedTask(taskId, ()->{
			networks.clear();
			networks.addAll(Arrays.asList(n));
			if (mainLayout != null)
			    mainLayout.networksArea.refresh();
			getLuwrain().playSound(Sounds.DONE);
		    });
	    });
    }

    void updateNetworksSync() throws Exception
    {
	final WifiNetwork[] n = nmCli.scan();
	networks.clear();
	networks.addAll(Arrays.asList(n));
    }

    @Override public boolean onEscape()
    {
	closeApp();
	return true;
    }

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	return MonoApp.Result.BRING_FOREGROUND;
    }

    Conv getConv() { return conv; }
}
