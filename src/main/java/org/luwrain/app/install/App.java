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

package org.luwrain.app.install;

import java.net.*;
import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.linux.*;
import org.luwrain.app.base.*;
import org.luwrain.util.*;

public final class App extends AppBase<Strings>
{
    static final String LOG_COMPONENT = "install";

    final BlockDevices blockDevices = new BlockDevices();
    private MainLayout mainLayout = null;

    public App() { super(Strings.NAME, Strings.class, "luwrain.install.iso"); }

    @Override protected AreaLayout onAppInit()
    {
	this.mainLayout = new MainLayout(this);
	setAppName(getStrings().appName());
	return mainLayout.getAreaLayout();
    }


    @Override public boolean onEscape()
    {
	closeApp();
	return true;
    }

}
