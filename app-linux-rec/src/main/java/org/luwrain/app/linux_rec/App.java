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

import java.util.*;
import java.text.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.core.annotations.*;
import org.luwrain.app.base.*;

import static java.nio.file.Files.*;

@AppNoArgs(name = "rec", title = { "en=Recorder", "ru=Диктофон" })
public final class App extends AppBase<Strings> implements MonoApp
{
    static public final Path REC_DIR = Paths.get(System.getProperty("user.home")).resolve("Recordings");
    static public final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm");

    public Config conf;
    private MainLayout mainLayout = null;

    public App() { super(Strings.class); }

    @Override protected AreaLayout onAppInit() throws IOException
    {
	createDirectories(REC_DIR);
	conf = getLuwrain().loadConf(Config.class);
	if (conf == null)
	    conf = new Config();
	if (conf.entries != null)
	    conf.entries = new ArrayList<>(conf.entries); else
	    conf.entries = new ArrayList<>();
	mainLayout = new MainLayout(this);
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
