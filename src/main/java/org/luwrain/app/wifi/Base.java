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

import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.linux.wifi.*;
import org.luwrain.controls.*;

final class Base
{
        private final App app;
    private final Luwrain luwrain;
    private final Strings strings;
    private final Connections connections;
    final Conversations conv;
    private Network[] networks = new Network[0];//null means a scanning is in progress
    private FutureTask task = null;

    Base(App app, Luwrain luwrain, Strings strings, Connections connections)
    {
	NullCheck.notNull(app, "app");
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(connections, "connections");
	this.app = app;
	this.luwrain = luwrain;
	this.strings = strings;
	this.connections = connections;
	this.conv = new Conversations(luwrain, strings);
    }

    boolean launchScanning(ListArea listArea)
    {
	if (isBusy())
	    return false;
	task = new FutureTask(()->{
		networks = null;//to indicate a scanning is in progress
		final ScanResult res = connections.scan();
		luwrain.runUiSafely(()->acceptResult(listArea, res));
	    }, null);
	luwrain.executeBkg(task);
	return true;
    }

    boolean launchConnection(ProgressArea destArea, Network connectTo)
    {
	if (isBusy())
	    return false;
	if (!connections.getConnectionLock(this))
	{
	    luwrain.message("Устанавливается фоновое соединение", Luwrain.MessageType.ERROR);
	    return true;
	}
	if (connectTo.hasPassword && !askForPassword(connectTo))
	    return false;
	task = new FutureTask(()->{
		if (connections.connect(connectTo, (line)->luwrain.runUiSafely(()->destArea.addProgressLine(line)), Base.this))
		    luwrain.runUiSafely(()->luwrain.message("Подключение к сети установлено", Luwrain.MessageType.DONE)); else
		    luwrain.runUiSafely(()->luwrain.message("Подключиться к сети не удалось", Luwrain.MessageType.ERROR));
	}, null);
		luwrain.executeBkg(task);
	return true;
    }

    private void acceptResult(ListArea listArea, ScanResult scanRes)
    {
	NullCheck.notNull(listArea, "listArea");
	NullCheck.notNull(scanRes, "scanRes");
	if (scanRes.type != ScanResult.Type.SUCCESS)
	{
	    this.networks = new Network[0];
	    luwrain.onAreaNewBackgroundSound(listArea);
	    luwrain.playSound(Sounds.ERROR);
	    listArea.refresh();
	    return;
	}
this.networks = scanRes.networks;
luwrain.onAreaNewBackgroundSound(listArea);
luwrain.playSound(Sounds.OK);
listArea.refresh();
    }

    boolean isBusy()
    {
	return task != null && !task.isDone();
    }

        boolean isScanning()
    {
	return isBusy() && networks == null;
    }


    private boolean askForPassword(Network network)
    {
	NullCheck.notNull(network, "network");
	final org.luwrain.linux.Settings.WifiNetwork sett = org.luwrain.linux.Settings.createWifiNetwork(luwrain.getRegistry(), network);
	if (!sett.getPassword("").isEmpty() &&
	    conv.useSavedPassword())
	{
	    network.setPassword(sett.getPassword(""));
	    return true;
	}
	final String password = conv.askPassword();
	if (password == null)
	    return false;
	if (conv.saveThePassword())
	    sett.setPassword(password);
	network.setPassword(password);
	return true;
    }

        ListArea.Model getListModel()
    {
	return new Model();
    }

    private final class Model implements ListArea.Model
    {
	@Override public int getItemCount()
	{
	    return networks != null?networks.length:0;
	}
	@Override public Object getItem(int index)
	{
	    return networks[index];
	}
	@Override public void refresh()
	{
	}
    }
}
