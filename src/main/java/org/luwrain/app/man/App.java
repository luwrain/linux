/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import org.luwrain.template.*;
import org.luwrain.controls.*;
import org.luwrain.core.queries.*;
import org.luwrain.popups.Popups;
import org.luwrain.linux.*;

public final class App extends AppBase<Strings> implements MonoApp
{
    private String[] pages = new String[0];

    public App()
    {
	super(Strings.NAME, Strings.class);
    }

    boolean search(String query)
    {
	NullCheck.notEmpty(query, "query");
	final List<String> res = new LinkedList();
	final Scripts scripts = new Scripts(getLuwrain());
	final Process p = scripts.runAsync(Scripts.ID.MAN_SEARCH, new String[]{query}, false);
	if (p == null)
	    return false;
	try {
	    try {
		p.getOutputStream().close();
		final BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = r.readLine();
		while (line != null)
		{
		    res.add(line);
		    line = r.readLine();
		}
		p.waitFor();
		pages = res.toArray(new String[res.size()]);
		return true;
	    }
	    finally {
		p.getInputStream().close();
	    }
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	    return false;
	}
	catch(IOException e)
	{
	    getLuwrain().crash(e);
	    return false;
	}
    }

    @Override protected boolean onAppInit()
    {
	return true;
    }

    @Override protected AreaLayout getDefaultAreaLayout()
    {
	return null;
    }

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }
}
