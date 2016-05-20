/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.linux;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.os.*;
import org.luwrain.speech.*;
import org.luwrain.linux.speech.*;

public class Linux implements org.luwrain.os.OperatingSystem
{
    private static final String LUWRAIN_LINUX_LIBRARY_NAME = "luwrainlinux";

    private Path scriptsDir;
    private Scripts scripts = null;
    private Hardware hardware;
    private String[] cpus = new String[0];
    private int ramSizeKb = 0;

    @Override public boolean init(String dataDir)
    {
	NullCheck.notNull(dataDir, "dataDir");
	System.loadLibrary(LUWRAIN_LINUX_LIBRARY_NAME);
	scriptsDir = Paths.get(dataDir).resolve("scripts");
	scripts = new Scripts(scriptsDir);
	readCpuInfo();
	readMemInfo();
	return true;
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notNull(propName, "propName");
	if (propName.startsWith("luwrain.hardware.cpu."))
	{
	    final String numStr = propName.substring("luwrain.hardware.cpu.".length());
	    try {
		final int n = Integer.parseInt(numStr);
		return n < cpus.length?cpus[n]:"";
	    }
	    catch(NumberFormatException e)
	    {
		e.printStackTrace();
		return "";
	    }
	}
	switch(propName)
	{
	case "luwrain.hardware.ramsizekb":
	    return "" + ramSizeKb; 
	case "luwrain.hardware.ramsizemb":
	    return "" + (ramSizeKb / 1024); 
	default:
	    return "";
	}
    }

    @Override public org.luwrain.hardware.Hardware getHardware()
    {
	if (hardware == null)
	    hardware = new Hardware(scriptsDir);
	return hardware;
    }

    @Override public boolean shutdown()
    {
	return scripts.runSync("lwr-shutdown", true);
    }

    @Override public boolean reboot()
    {
	return scripts.runSync("lwr-reboot", true);
    }

    @Override public boolean suspend(boolean hibernate)
    {
	return scripts.runSync("lwr-suspend", true);
    }

    @Override public void openFileInDesktop(Path path)
    {
	throw new UnsupportedOperationException("Linux has no support of opening files in desktop environment");
    }

    @Override public KeyboardHandler getCustomKeyboardHandler(String subsystem)
    {
	NullCheck.notNull(subsystem, "subsystem");
	switch(subsystem.toLowerCase().trim())
	{
	case "javafx":
	    return new KeyboardJavafxHandler();
	default:
	    return null;
	}
    }

    @Override public Channel loadSpeechChannel(String type)
    {
	NullCheck.notNull(type, "type");
	switch(type)
	{
	case "command":
	    return new Command();
	case "voiceman":
	    return new VoiceMan();
	case "emacspeak":
	    return new Emacspeak();
	default:
	    Log.error("linux", "unknown speech channel type:" + type);
	    return null;
	}
    }

    private void readCpuInfo()
    {
	try {
	    final LinkedList<String> res = new LinkedList<String>();
	    for(String s: Files.readAllLines(Constants.CPU_INFO))
		if (s.matches("model\\s*name\\s*:.*"))
		    res.add(s.substring(s.indexOf(":") + 1).trim());
	    cpus = res.toArray(new String[res.size()]);
	}
	catch(IOException e)
	{
	    Log.debug("linux", "unable to read CPU info:" + e.getMessage());
	    e.printStackTrace();
	}
    }

    private void readMemInfo()
    {
	try {
	    String totalStr = "";
	    String swapStr = "";
	    final LinkedList<String> res = new LinkedList<String>();
	    for(String s: Files.readAllLines(Constants.MEM_INFO))
	    {
		if (s.matches("MemTotal\\s*:.* kB"))
		    totalStr = s.substring(s.indexOf(":") + 1).trim();
		if (s.matches("SwapTotal\\s*:.* kB"))
		    swapStr = s.substring(s.indexOf(":") + 1).trim();
	    }
	    if (totalStr.endsWith("kB"))
		totalStr = totalStr.substring(0, totalStr.length() - 2).trim();
	    if (swapStr.endsWith("kB"))
		swapStr = swapStr.substring(0, swapStr.length() - 2).trim();
	    final int total = Integer.parseInt(totalStr);
	    final int swap = Integer.parseInt(swapStr);
	    ramSizeKb = total - swap;
	}
	catch(IOException | NumberFormatException e)
	{
	    Log.debug("linux", "unable to read memory info:" + e.getMessage());
	    e.printStackTrace();
	}
    }
}
