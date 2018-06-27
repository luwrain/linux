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

package org.luwrain.app.wifi;

import java.net.*;
import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.core.queries.*;
import org.luwrain.popups.Popups;
import org.luwrain.linux.wifi.*;

public class App implements Application, MonoApp
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private Base base = null;
    private ListArea listArea = null;
    private ProgressArea progressArea = null;
    private AreaLayoutHelper layout;

    private final Connections connections;

    public App(Connections connections)
    {
	NullCheck.notNull(connections, "connections");
	this.connections = connections;
    }

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	strings = (Strings)o;
	this.luwrain = luwrain;
	this.base = new Base(this, luwrain, strings, connections);
	createArea();
	this.layout = new AreaLayoutHelper(()->{
		luwrain.onNewAreaLayout();
		luwrain.announceActiveArea();
	    }, listArea);
	base.launchScanning(listArea);
	return new InitResult();
    }

    private void createArea()
    {
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.model = base.getListModel();
	params.appearance = new Appearance(luwrain, strings, connections);
	params.clickHandler = (area, index, obj)->onConnect(obj);
	params.name = strings.appName();

	listArea = new ListArea(params){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case ESCAPE:
			    return onCloseApp();
			case TAB:
			    if (layout.hasAdditionalArea())
				luwrain.setActiveArea(layout.getAdditionalArea());
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
					    case ACTION:
			if (ActionEvent.isAction(event, "disconnect"))
			    return onDisconnect();
			return false;
					    case CLOSE:
			return onCloseApp();
		    case REFRESH:
			doScanning();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
		@Override public boolean onAreaQuery(AreaQuery query)		{
		    NullCheck.notNull(query, "query");
		    switch(query.getQueryCode())
		    {
		    case AreaQuery.BACKGROUND_SOUND:
			if (!base.isScanning())
			    return false;
			((BackgroundSoundQuery)query).answer(new BackgroundSoundQuery.Answer(BkgSounds.WIFI));
			return true;
		    default:
			return super.onAreaQuery(query);
		    }}
		@Override public Action[] getAreaActions()
		{
		    if (base.isBusy())
			return new Action[0];
		    if (connections.hasConnection())
			return new Action[]{
			    new Action("disconnect", strings.actionDisconnect()),
			};
		    return new Action[0];
		}
		@Override protected String noContentStr()
		{
		    return base.isScanning()?strings.scanningInProgress():strings.noWifiNetworks();
		}
	    };

	this.progressArea = new ProgressArea(new DefaultControlEnvironment(luwrain), "Подключение"){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case TAB:
			case BACKSPACE:
			    luwrain.setActiveArea(listArea);
			    return true;
			case ESCAPE:
			    if (base.isBusy())
				return false;
			    layout.closeAdditionalArea();
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
		    case ACTION:
			if (ActionEvent.isAction(event, "disconnect"))
			    return onDisconnect();
			return false;
		    case CLOSE:
			return onCloseApp();
		    default:
			return super.onSystemEvent(event);
		    }
		}
				@Override public Action[] getAreaActions()
		{
		    if (base.isBusy())
			return new Action[0];
		    if (connections.hasConnection())
			return new Action[]{
			    new Action("disconnect", strings.actionDisconnect()),
			};
		    return new Action[0];
		}
			    };
    }

    private void doScanning()
    {
	if (!base.launchScanning(listArea))
	    return;
	listArea.refresh();
	luwrain.onAreaNewBackgroundSound(listArea);
    }

    private boolean onConnect(Object obj)
    {
	NullCheck.notNull(obj, "obj");
	if (!(obj instanceof Network))
	    return false;
	progressArea.clear();
	if (!base.launchConnection(progressArea, (Network)obj))
	    return false;
	layout.openAdditionalArea(progressArea, AreaLayoutHelper.Position.BOTTOM);
	return true;
    }

    public boolean onDisconnect()
    {
	if (base.isBusy())
	    return false;
	if (!connections .hasConnection())
	    return false;
	connections.disconnect();
	return true;
    }

    private boolean onCloseApp()
    {
	if (base.isBusy())
	    return false;
	closeApp();
	return true;
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
	return layout.getLayout();
    }

    @Override public void closeApp()
    {
	luwrain.closeApp();
    }
}
