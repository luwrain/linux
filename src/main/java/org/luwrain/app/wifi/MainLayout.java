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

package org.luwrain.app.wifi;

import java.util.*;
import java.util.regex.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.linux.*;
import org.luwrain.controls.ListUtils.*;
import org.luwrain.app.base.*;

import static org.luwrain.core.DefaultEventResponse.*;

final class MainLayout extends LayoutBase implements ListArea.ClickHandler<WifiNetwork>
{
    private final App app;
    final ListArea<WifiNetwork> networksArea;
    final SimpleArea statusArea;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	this.networksArea = new ListArea<WifiNetwork>(listParams((params)->{
		    params.model = new ListModel(app.networks);
		    params.name = app.getStrings().networksAreaName();
		    params.appearance = new Appearance(app);
		    params.clickHandler = this;
		})){
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    if (event.getType() == SystemEvent.Type.REGULAR)
			switch(event.getCode())
		    {
		    case REFRESH:
		    return app.updateNetworkList();
		    }
		    return super.onSystemEvent(event);
		}
	    };
	this.statusArea = new SimpleArea(getControlContext(), app.getStrings().statusAreaName());
	setAreaLayout(networksArea, actions(
					    action("disconnect", app.getStrings().actionDisconnect(), new InputEvent(InputEvent.Special.F5), this::actDisconnect, ()->(findConnected() != null))
					    ));
	//	setAreaLayout(AreaLayout.LEFT_RIGHT, networksArea, null, statusArea, null);
    }

    @Override public boolean onListClick(ListArea area, int index, WifiNetwork network)
    {
	final WifiNetwork wifi = networksArea.selected();
	if (wifi == null || app.isBusy())
	    return false;
	final String password;
	if (wifi.getProtectionType() != null && !wifi.getProtectionType().trim().isEmpty())
	{
	    password = app.getConv().askPassword();
	    if (password == null)
		return true;
	}else
	    password = "";
	final App.TaskId taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
		final boolean res = app.nmCli.connect(wifi, password);
		if (!res)
		{
		    app.finishedTask(taskId, ()->			    app.message(app.getStrings().errorConnecting(), Luwrain.MessageType.ERROR));
		    return;
		}
		app.updateNetworksSync();
		app.finishedTask(taskId, ()->{
			networksArea.refresh();
			    app.message(app.getStrings().connectionEstablished(), Luwrain.MessageType.DONE);

		    });
	    });
    }

    private boolean actDisconnect()
    {
	final WifiNetwork wifi = findConnected();
	if (wifi == null)
	    return false;
	if (!app.getConv().disconnectCurrent(wifi.getName()))
	    return true;
		final App.TaskId taskId = app.newTaskId();
	return app.runTask(taskId, ()->{
		final boolean res = app.nmCli.disconnect();
		app.finishedTask(taskId, ()->{
			if (res)
			    app.message(app.getStrings().successfullyDisconnected(), Luwrain.MessageType.DONE); else
			    app.message(app.getStrings().errorDisconnecting(), Luwrain.MessageType.ERROR);
		    });
	    });
    }

    private WifiNetwork findConnected()
    {
	final int count = networksArea.getListModel().getItemCount();
	for(int i = 0;i < count;i++)
	    if (networksArea.getListModel().getItem(i).isConnected())
		return networksArea.getListModel().getItem(i);
	return null;
    }
}
