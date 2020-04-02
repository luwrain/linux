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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

final class MainLayout
{
    private final App app;
        private ConsoleArea searchArea = null;
    private NavigationArea pageArea = null;

        private String[] pages = new String[0];


    MainLayout(App app)
    {
	this.app = app;
	this.searchArea = new ConsoleArea(getSearchAreaParams()){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case ESCAPE:
			    app.closeApp();
			    return true;
			}
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
	
	searchArea.setConsoleClickHandler((area,index,obj)->{
		    return false;
	    });
	
	searchArea.setConsoleInputHandler((area,text)->{
		NullCheck.notNull(text, "text");
		if (text.trim().isEmpty())
		    return ConsoleArea.InputHandler.Result.REJECTED;
		if (!app.search(text.trim().toLowerCase()))
		    		    return ConsoleArea.InputHandler.Result.REJECTED;
		area.refresh();
		app.getLuwrain().playSound(Sounds.DONE);
		return ConsoleArea.InputHandler.Result.OK;
	    });

	this.pageArea = new SimpleArea(new DefaultControlContext(app.getLuwrain())){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case ESCAPE:
			    app.closeApp();
			    return true;
			}
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

    }
    
    
    ConsoleArea.Params getSearchAreaParams()
    {
		final ConsoleArea.Params params = new ConsoleArea.Params();
		params.context = new DefaultControlContext(app.getLuwrain());
		params.model = new SearchAreaModel();
		params.appearance = new SearchAreaAppearance();
		params.areaName = app.getStrings().appName();
	params.inputPos = ConsoleArea.InputPos.TOP;
	params.inputPrefix = "man>";
	return params;
    }

        private class SearchAreaAppearance implements ConsoleArea.Appearance
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

    private class SearchAreaModel implements ConsoleArea.Model
    {
        @Override public int getConsoleItemCount()
	{
	    return pages.length;
	}
	@Override public Object getConsoleItem(int index)
	{
	    if (index < 0 || index >= pages.length)
		throw new IllegalArgumentException("index (" + index + ") must be greater or equal to zero and less than " + pages.length);
	    return pages[index];
	}
    }



}
