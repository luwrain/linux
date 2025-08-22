/*
   Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.linux_rec;

import org.luwrain.core.*;
import org.luwrain.core.annotations.*;
import org.luwrain.app.base.*;

@AppNoArgs(name = "man", title = { "en=Man", "ru=Man" })
public final class App extends AppBase<Strings> implements MonoApp
{
    private MainLayout mainLayout = null;

    public App() { super(Strings.class, "luwrain.linux.man"); }

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

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	return MonoApp.Result.BRING_FOREGROUND;
    }
}
