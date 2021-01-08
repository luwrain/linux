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

package org.luwrain.app.man;

import java.net.*;
import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.app.base.*;
import org.luwrain.controls.*;
import org.luwrain.core.queries.*;
import org.luwrain.popups.Popups;
import org.luwrain.linux.*;

public final class App extends AppBase<Strings> implements MonoApp
{
    private MainLayout mainLayout = null;

    public App()
    {
	super(Strings.NAME, Strings.class, "luwrain.linux.man");
    }

    @Override protected boolean onAppInit()
    {
	this.mainLayout = new MainLayout(this);
	return true;
    }

    @Override public boolean onInputEvent(Area area, InputEvent event)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(event, "event");
	if (event.isSpecial())
	    switch(event.getSpecial())
	{
	case ESCAPE:
	    closeApp();
	    return true;
	}
	return super.onInputEvent(area, event);
    }

    @Override protected AreaLayout getDefaultAreaLayout()
    {
	return this.mainLayout.getLayout();
    }

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }
}
