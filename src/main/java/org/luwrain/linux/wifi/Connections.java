/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import org.luwrain.util.*;

public final class Connections
{
    static private final String LOG_COMPONENT = Linux.LOG_COMPONENT;
    static private final String INTERFACES_DIR = "/sys/class/net";

    public interface ConnectionListener
    {
	void onConnectionProgressLine(String line);
    }

    private final Scripts scripts;
    private final String wlanInterface;
    private Network connectedNetwork = null;
    private Object lockOwner = null;

    public Connections(org.luwrain.base.PropertiesBase props)
    {
	NullCheck.notNull(props, "props");
	this.scripts = new Scripts(props);
this.wlanInterface = getWlanInterface();
	if (this.wlanInterface == null || this.wlanInterface.trim().isEmpty())
	Log.debug(LOG_COMPONENT, "no wlan interfacewlan interface"); else
	Log.debug(LOG_COMPONENT, "wlan interface is " + wlanInterface);
    }

    synchronized public boolean getConnectionLock(Object owner)
    {
	NullCheck.notNull(owner, "owner");
	if (lockOwner != null ||hasConnection())
	    return false;
	lockOwner = owner;
	return true;
    }

    public void releaseConnectionLock(Object owner)
    {
	NullCheck.notNull(owner, "owner");
	if (lockOwner == null)
	    return;
	if (lockOwner != owner)
	    throw new IllegalArgumentException("Illegal lock owner");
	this.lockOwner = null;
    }

    public String getConnectedNetworkName()
    {
	return connectedNetwork != null?connectedNetwork.name:"";
    }

    public boolean connect(Network connectTo, ConnectionListener listener, Object lockOwner)
    {
	NullCheck.notNull(connectTo, "connectTo");
	NullCheck.notNull(listener, "listener");
	NullCheck.notNull(lockOwner, "lockOwner");
	if (this.lockOwner != lockOwner)
	    throw new IllegalArgumentException("Illegal lock owner");
	if (hasConnection())
	    throw new RuntimeException("Already connected to the network \'" + getConnectedNetworkName() + "\'");
	try {
	if (wlanInterface == null || wlanInterface.trim().isEmpty())
	    return false;
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
	    if (p.exitValue() == 0)
	    {
		this.connectedNetwork = connectTo;
		return true;
	    }
	    return false;
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
	finally {
	    this.lockOwner = null;
	}
    }

    public boolean disconnect()
    {
	if (wlanInterface == null || wlanInterface.isEmpty())
	    return false;
	if (connectedNetwork == null)
	    return false;
	if (!scripts.runSync(Scripts.ID.WIFI_DISCONNECT, new String[]{wlanInterface}, true))
	    return false;
	connectedNetwork = null;
	return true;
    }

    public boolean hasConnection()
    {
	return connectedNetwork != null;
    }

public ScanResult scan()
    {
	if (wlanInterface == null || wlanInterface.trim().isEmpty())
	    return new ScanResult();
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
	final File[] files = new File(dir).listFiles();
	if (files == null)
	    return new ScanResult();
	for(File p: files)
	    {
		if (!p.isDirectory())
		    continue;
		final Network n = readNetworkData(p);
		if (n != null)
		    networks.add(n);
	    }
	final Network[] res = networks.toArray(new Network[networks.size()]);
					       Arrays.sort(res);
	return new ScanResult(res);
    }

    private Network readNetworkData(File dir)
    {
	NullCheck.notNull(dir, "dir");
	final String name = readFirstLine(new File(dir, "name"));
	if (name == null || name.trim().isEmpty())
	{
	    Log.warning("network", "no name value in " + dir.toString());
	    return null;
	}
	final String encryption = readFirstLine(new File(dir, "encryption"));
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

    private String readFirstLine(File file)
    {
	NullCheck.notNull(file, "file");
	try {
	    final String[] lines = FileUtils.readTextFileMultipleStrings(file, "UTF-8", null);
	    if (lines.length == 0)
		return null;
	    return lines[0];
	}
	catch(IOException e)
	{e.printStackTrace();
	    return null;
	}
    }
}
