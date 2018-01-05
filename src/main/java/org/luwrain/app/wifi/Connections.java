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

import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.*;

import org.luwrain.core.*;

class Connections
{
    static private final String INTERFACES_DIR = "/sys/class/net";

    interface ConnectionListener
    {
	void onConnectionProgressLine(String line);
    }

    private final Luwrain luwrain;

    Connections(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    synchronized boolean connect(WifiNetwork connectTo, ConnectionListener listener)
    {
	NullCheck.notNull(connectTo, "connectTo");
	NullCheck.notNull(listener, "listener");
	final String wlanInterface = getWlanInterface();
	Log.debug("network", "wlan interface is " + wlanInterface);
	if (wlanInterface == null || wlanInterface.trim().isEmpty())
	    return false;
	try {
	    final Process p = new ProcessBuilder("sudo", luwrain.getFileProperty("luwrain.dir.scripts").toPath().resolve("lwr-wifi-connect").toString(), wlanInterface, connectTo.name, connectTo.getPassword()).start();
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
	    e.printStackTrace();
	    return false;
	}
    }

    synchronized WifiScanResult scan()
    {
	final String wlanInterface = getWlanInterface();
	Log.debug("network", "wlan interface is " + wlanInterface);
	if (wlanInterface == null || wlanInterface.trim().isEmpty())
	    return new WifiScanResult();
	final String dir;
	try {
	    final Process p = new ProcessBuilder("sudo", luwrain.getFileProperty("luwrain.dir.scripts").toPath().resolve("lwr-wifi-scan").toString(), wlanInterface).start();
	    p.getOutputStream().close();
	    final BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    dir = r.readLine();
	    p.waitFor();
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	    return new WifiScanResult();
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	    return new WifiScanResult();
	}
	Log.debug("network", "wifi scan result directory is " + dir);
	if (dir == null || dir.trim().isEmpty())
	    return new WifiScanResult();
	final LinkedList<WifiNetwork> networks = new LinkedList<WifiNetwork>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(dir))) {
		for (Path p : directoryStream) 
		{
		    Log.debug("network", "reading wifi network information from " + p.toString());
		    final WifiNetwork n = readNetworkData(p);
		    if (n != null)
			networks.add(n);
		}
	    } 
	catch (IOException e) 
	{
	    e.printStackTrace();
	    return new WifiScanResult();
	}
	return new WifiScanResult(networks.toArray(new WifiNetwork[networks.size()]));
    }

    private WifiNetwork readNetworkData(Path dir)
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
	return new WifiNetwork(name, encryption.toLowerCase().trim().equals("on"));
    }

    private String getWlanInterface()
    {
	final LinkedList<Path> dirs = new LinkedList<Path>();
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
