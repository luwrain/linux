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
import org.luwrain.controls.*;
import org.luwrain.core.queries.*;
import org.luwrain.popups.Popups;

public final class App implements Application, MonoApp
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private Base base = null;
    private ConsoleArea2 searchArea = null;
    private NavigationArea pageArea = null;

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	strings = (Strings)o;
	this.luwrain = luwrain;
	this.base = new Base(luwrain, strings);
	createAreas();
	return new InitResult();
    }

    private void createAreas()
    {
	final ConsoleArea2.Params params = new ConsoleArea2.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.model = base.getSearchAreaModel();
	params.appearance = base.getSearchAreaAppearance();
	params.areaName = strings.appName();
	params.inputPos = ConsoleArea2.InputPos.TOP;
	params.inputPrefix = "man>";
	
	searchArea = new ConsoleArea2(params){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case ESCAPE:
			    closeApp();
			    return true;
			case TAB:
			    luwrain.setActiveArea(pageArea);
			    return true;
			}
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
	    };
	
	searchArea.setConsoleClickHandler((area,index,obj)->{
		    return false;
	    });
	
	searchArea.setConsoleInputHandler((area,text)->{
		NullCheck.notNull(text, "text");
		if (text.trim().isEmpty())
		    return ConsoleArea2.InputHandler.Result.REJECTED;
		if (!base.search(text.trim().toLowerCase()))
		    		    return ConsoleArea2.InputHandler.Result.REJECTED;
		area.refresh();
		luwrain.playSound(Sounds.DONE);
		return ConsoleArea2.InputHandler.Result.OK;
	    });

	pageArea = new SimpleArea(new DefaultControlEnvironment(luwrain)){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case ESCAPE:
			    closeApp();
			    return true;
			case TAB:
			    luwrain.setActiveArea(searchArea);
			    return true;
			}
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR )
			return super.onSystemEvent(event);
		    switch(event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
	    };
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(AreaLayout.TOP_BOTTOM, searchArea, pageArea);
    }

    @Override public void closeApp()
    {
	luwrain.closeApp();
    }
}
