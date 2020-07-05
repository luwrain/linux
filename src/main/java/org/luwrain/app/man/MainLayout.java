/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.linux.*;

final class MainLayout implements ConsoleArea.ClickHandler, ConsoleArea.InputHandler
{
    private final App app;
    private final ConsoleArea searchArea;
    private final NavigationArea pageArea;

    private String[] pages = new String[0];

    MainLayout(App app)
    {
	this.app = app;
	this.searchArea = new ConsoleArea(getSearchAreaParams()){
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onInputEvent(this, event))
			return true;
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onSystemEvent(this, event))
			return true;
		    return super.onSystemEvent(event);
		}
	    };
	this.pageArea = new SimpleArea(new DefaultControlContext(app.getLuwrain())){
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onInputEvent(this, event))
			return true;
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onSystemEvent(this, event))
			return true;
		    return super.onSystemEvent(event);
		}
	    };
		searchArea.setConsoleInputHandler(this);
		searchArea.setConsoleInputHandler(this);
    }

@Override public boolean onConsoleClick(ConsoleArea area, int index, Object obj)
    {
			    return false;
	    }

@Override public ConsoleArea.InputHandler.Result onConsoleInput(ConsoleArea area, String text)
    {
		NullCheck.notNull(text, "text");
		if (text.trim().isEmpty())
		    return ConsoleArea.InputHandler.Result.REJECTED;
		if (!search(text.trim().toLowerCase()))
		    		    return ConsoleArea.InputHandler.Result.REJECTED;
		area.refresh();
		app.getLuwrain().playSound(Sounds.DONE);
		return ConsoleArea.InputHandler.Result.OK;
    }

    ConsoleArea.Params getSearchAreaParams()
    {
	final ConsoleArea.Params params = new ConsoleArea.Params();
	params.context = new DefaultControlContext(app.getLuwrain());
	params.model = new SearchAreaModel();
	params.appearance = new SearchAreaAppearance();
	params.name = app.getStrings().appName();
	params.inputPos = ConsoleArea.InputPos.TOP;
	params.inputPrefix = "man>";
	return params;
    }

    boolean search(String query)
    {
	NullCheck.notEmpty(query, "query");
	final List<String> res = new LinkedList();
	final Scripts scripts = new Scripts(app.getLuwrain());
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
	    app.getLuwrain().crash(e);
	    return false;
	}
    }

    AreaLayout getLayout()
    {
	return new AreaLayout(AreaLayout.TOP_BOTTOM, searchArea, pageArea);
    }

    private final class SearchAreaAppearance implements ConsoleArea.Appearance
    {
	@Override public void announceItem(Object item)
	{
	    NullCheck.notNull(item, "item");
	    app.getLuwrain().setEventResponse(DefaultEventResponse.text(item.toString()));
	}
	@Override public String getTextAppearance(Object item)
	{
	    NullCheck.notNull(item, "item");
	    return item.toString();
	}
    }

    private final class SearchAreaModel implements ConsoleArea.Model
    {
        @Override public int getItemCount()
	{
	    return pages.length;
	}
	@Override public Object getItem(int index)
	{
	    if (index < 0 || index >= pages.length)
		throw new IllegalArgumentException("index (" + index + ") must be greater or equal to zero and less than " + pages.length);
	    return pages[index];
	}
    }
}
