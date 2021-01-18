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
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onSystemEvent(this, event))
			return true;
		    return super.onSystemEvent(event);
		}
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    NullCheck.notNull(query, "query");
		    if (app.onAreaQuery(this, query))
			return true;
		    return super.onAreaQuery(query);
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
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onSystemEvent(this, event))
			return true;
		    return super.onSystemEvent(event);
		}
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    NullCheck.notNull(query, "query");
		    if (!app.onAreaQuery(this, query))
			return true;
		    return super.onAreaQuery(query);
		}
	    };
		searchArea.setConsoleInputHandler(this);
				searchArea.setConsoleClickHandler(this);
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
	params.model = new ConsoleUtils.ArrayModel(()->{return pages;});
	params.appearance = new SearchAreaAppearance();
	params.name = app.getStrings().appName();
	params.inputPos = ConsoleArea.InputPos.TOP;
	params.inputPrefix = "man>";
	return params;
    }

    boolean search(String query)
    {
	NullCheck.notEmpty(query, "query");
	final List<String> res = new ArrayList();
	final BashProcess p = new BashProcess("man -k " + BashProcess.escape(query.trim()));
	try {
	    p.run();
	}
	catch(IOException e)
	{
	    app.getLuwrain().crash(e);
	    return true;
	}
	p.waitFor();
	pages = p.getOutput();
	return true;
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
	    app.getLuwrain().setEventResponse(DefaultEventResponse.text(app.getLuwrain().getSpeakableText(item.toString(), Luwrain.SpeakableTextType.PROGRAMMING)));
	}
	@Override public String getTextAppearance(Object item)
	{
	    NullCheck.notNull(item, "item");
	    return item.toString();
	}
    }
}
