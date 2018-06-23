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
import org.luwrain.controls.*;
import org.luwrain.popups.*;
import org.luwrain.linux.wifi.*;

class Base
{
        private final App app;
    private final Luwrain luwrain;
    private final Strings strings;
    private final Connections connections;
    private final ListUtils.FixedModel listModel = new ListUtils.FixedModel();
    private FutureTask scanningTask;
    private FutureTask connectionTask;

    private boolean scanningInProgress = false;

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
    }

    ListArea.Model getListModel()
    {
	return listModel;
    }

    boolean launchScanning()
    {
	if (scanningTask != null && !scanningTask.isDone())
	    return false;
	scanningTask = createScanningTask();
	scanningInProgress = true;
	luwrain.executeBkg(scanningTask);
	return true;
    }

    boolean launchConnection(ProgressArea destArea, Network connectTo)
    {
	if (connectionTask != null && !connectionTask.isDone())
	    return false;
	if (connectTo.hasPassword && !askForPassword(connectTo))
	    return false;
	connectionTask = createConnectionTask(destArea, connectTo);
	luwrain.executeBkg(connectionTask);
	return true;
    }

    private void acceptResult(ScanResult scanRes)
    {
	NullCheck.notNull(scanRes, "scanRes");
	scanningInProgress = false;
	if (scanRes.type != ScanResult.Type.SUCCESS)
	{
	    listModel.clear();
	    app.onReady(false);
	} else
	{
	    listModel.setItems(scanRes.networks);
	    app.onReady(true);
	}
    }

    private FutureTask createScanningTask()
    {
	return new FutureTask(()->{
		final ScanResult res = connections.scan();
		luwrain.runUiSafely(()->acceptResult(res));
	}, null);
    }

    private FutureTask createConnectionTask(final ProgressArea destArea, final Network connectTo)
    {
	return new FutureTask(()->{
		if (connections.connect(connectTo, (line)->luwrain.runUiSafely(()->destArea.addProgressLine(line))))
		    luwrain.runUiSafely(()->luwrain.message("Подключение к сети установлено", Luwrain.MessageType.DONE)); else
		    luwrain.runUiSafely(()->luwrain.message("Подключиться к сети не удалось", Luwrain.MessageType.ERROR));
	}, null);
    }

    boolean isScanning()
    {
	return scanningTask != null && !scanningTask.isDone() && scanningInProgress;
    }

    private boolean askForPassword(Network network)
    {
	NullCheck.notNull(network, "network");
	final org.luwrain.linux.Settings.WifiNetwork settings = org.luwrain.linux.Settings.createWifiNetwork(luwrain.getRegistry(), network);
	if (!settings.getPassword("").isEmpty() &&
	    Popups.confirmDefaultYes(luwrain, strings.connectionPopupName(), strings.useSavedPassword()))
	{
	    network.setPassword(settings.getPassword(""));
	    return true;
	}
	final String password = Popups.simple(luwrain, strings.connectionPopupName(), strings.enterThePassword(), "");
	if (password == null)
	    return false;
    if (Popups.confirmDefaultYes(luwrain, strings.connectionPopupName(), strings.saveThePassword()))
	settings.setPassword(password);
    network.setPassword(password);
    return true;
    }
}
