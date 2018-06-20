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

package org.luwrain.linux.wifi;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.*;

import org.luwrain.core.*;
import org.luwrain.linux.*;

class Connections
{
    static private final String LOG_COMPONENT = Linux.LOG_COMPONENT;
    static private final String INTERFACES_DIR = "/sys/class/net";

    interface ConnectionListener
    {
	void onConnectionProgressLine(String line);
    }

    private final Scripts scripts;

    Connections(org.luwrain.base.PropertiesBase props)
    {
	NullCheck.notNull(props, "props");
	this.scripts = new Scripts(props);
    }

    synchronized boolean connect(Network connectTo, ConnectionListener listener)
    {
	NullCheck.notNull(connectTo, "connectTo");
	NullCheck.notNull(listener, "listener");
	final String wlanInterface = getWlanInterface();
	if (wlanInterface == null || wlanInterface.trim().isEmpty())
	    return false;
	Log.debug(LOG_COMPONENT, "wlan interface is " + wlanInterface);
	try {
	    final Process p = scripts.runAsync(Scripts.ID.WIFI_CONNECT, new String[]{
		    wlanInterface,
		    connectTo.name,
		    connectTo.getPassword()}, true);
	    if (p == null)
	    {
		Log.error(LOG_COMPONENT, "unable to connect to the wifi network \'" + wlanInterface + "\'");
		return false;
	    }
	    p.getOutputStream().close();
	    final BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    String line = null;
	    while( (line = r.readLine()) != null)
		listener.onConnectionProgressLine(line);
	    p.waitFor();
	    return p.exitValue() == 0;
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	    return false;
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to process wifi connection output:" + e.getClass().getName() + ":" + e.getMessage());
	    return false;
	}
    }

    synchronized ScanResult scan()
    {
	final String wlanInterface = getWlanInterface();
	if (wlanInterface == null || wlanInterface.trim().isEmpty())
	    return new ScanResult();
	Log.debug(LOG_COMPONENT, "wlan interface is " + wlanInterface);
	final String dir;
	try {
	    final Process p = scripts.runAsync(Scripts.ID.WIFI_SCAN, new String[]{wlanInterface}, true);
	    if (p == null)
	    {
		Log.error(LOG_COMPONENT, "unable to scan for wifi networks through the interface \'" + wlanInterface + "\'");
		return new ScanResult();
	    }
	    p.getOutputStream().close();
	    final BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    dir = r.readLine();
	    p.waitFor();
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	    return new ScanResult();
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to scan for wifi networks through the interface \'" + wlanInterface + "\':" + e.getClass().getName() + ":" + e.getMessage());
	    return new ScanResult();
	}
	Log.debug(LOG_COMPONENT, "wifi scan result directory is " + dir);
	if (dir == null || dir.trim().isEmpty())
	    return new ScanResult();
	final List<Network> networks = new LinkedList();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(dir))) {
	    for (Path p : directoryStream) 
	    {
		final Network n = readNetworkData(p);
		if (n != null)
		    networks.add(n);
	    }
	} 
	catch (IOException e) 
	{
	    e.printStackTrace();
	    return new ScanResult();
	}
	return new ScanResult(networks.toArray(new Network[networks.size()]));
    }

    private Network readNetworkData(Path dir)
    {
	NullCheck.notNull(dir, "dir");
	final String name = readFirstLine(dir.resolve("name"));
	if (name == null || name.trim().isEmpty())
	{
	    Log.warning("network", "no name value in " + dir.toString());
	    return null;
	}
	final String encryption = readFirstLine(dir.resolve("encryption"));
	if (encryption == null || encryption.trim().isEmpty())
	{
	    Log.warning("network", "no encryption value in " + dir.toString());
	    return null;
	}
	return new Network(name, encryption.toLowerCase().trim().equals("on"));
    }

    private String getWlanInterface()
    {
	final List<Path> dirs = new LinkedList();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(INTERFACES_DIR))) {
		for (Path p : directoryStream) 
		    if (Files.isDirectory(p))
			dirs.add(p);
	    } 
	catch (IOException e) 
	{
	    e.printStackTrace();
	    return null;
	}
	for(Path p: dirs)
	    if (Files.exists(p.resolve("wireless")))
		return p.getFileName().toString();
	return null;
    }

    private String readFirstLine(Path path)
    {
	NullCheck.notNull(path, "path");
	try {
	    List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
	    if (lines == null || lines.size() < 1)
		return null;
	    return lines.get(0);
	}
	catch(Exception e)
	{e.printStackTrace();
	    return null;
	}
    }
}
