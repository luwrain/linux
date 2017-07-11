/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.linux;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.base.*;

public final class Linux implements org.luwrain.base.OperatingSystem
{
    static final String LOG_COMPONENT = "linux";
    static private final String LUWRAIN_LINUX_LIBRARY_NAME = "luwrainlinux";

    private org.luwrain.base.CoreProperties props = null;
    private Hardware hardware = null;
    private final org.luwrain.linux.fileops.Operations filesOperations = new org.luwrain.linux.fileops.Operations();
    private Scripts scripts = null;
    private String[] cpus = new String[0];
    private int ramSizeKb = 0;

    @Override public InitResult init(org.luwrain.base.CoreProperties props)
    {
	NullCheck.notNull(props, "props");
	try {
	    this.props = props;
	    System.loadLibrary(LUWRAIN_LINUX_LIBRARY_NAME + "-" + System.getProperty("sun.arch.data.model"));
	    scripts = new Scripts(props.getFileProperty("luwrain.dir.scripts"));
	    readCpuInfo();
	    readMemInfo();
	    return new InitResult();
	}
	catch(Throwable e)
	{
	    return new InitResult(e);
	}
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

    @Override public Braille getBraille()
    {
	return new BrlApi();
    }

    @Override public org.luwrain.base.Hardware getHardware()
    {
	if (hardware == null)
	    hardware = new Hardware(scripts, props);
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

    @Override public org.luwrain.interaction.KeyboardHandler getCustomKeyboardHandler(String subsystem)
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

    private void readCpuInfo()
    {
	final File cpuInfoFile = props.getFileProperty("luwrain.linux.cpuinfo");
	if (cpuInfoFile == null)
	{
	    Log.warning(LOG_COMPONENT, "no luwrain.linux.cpuinfo property, skipping reading CPU information");
	    return;
	}
	try {
	    final List<String> res = new LinkedList<String>();
	    for(String s: Files.readAllLines(cpuInfoFile.toPath()))
		if (s.matches("model\\s*name\\s*:.*"))
		    res.add(s.substring(s.indexOf(":") + 1).trim());
	    cpus = res.toArray(new String[res.size()]);
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to read CPU info:" + e.getClass().getName() + ":" + e.getMessage());
	}
	Log.debug(LOG_COMPONENT, "Found CPUs (" + cpus.length + "):");
	for(String s: cpus)
	    Log.debug(LOG_COMPONENT, s);
    }

    private void readMemInfo()
    {
	final File memInfoFile = props.getFileProperty("luwrain.linux.meminfo");
	if (memInfoFile == null)
	{
	    Log.warning(LOG_COMPONENT, "no luwrain.linux.meminfo property, skipping reading memory information");
	    return;
	}
	try {
	    String totalStr = "";
	    String swapStr = "";
	    for(String s: Files.readAllLines(memInfoFile.toPath()))
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
	    Log.debug(LOG_COMPONENT, "RAM size (kb):" + ramSizeKb);
	    Log.debug(LOG_COMPONENT, "swap size (kb):" + swap);
	}
	catch(IOException | NumberFormatException e)
	{
	    Log.error(LOG_COMPONENT, "unable to read memory information:" + e.getClass().getName() + ":" + e.getMessage());
	}
    }

    @Override public org.luwrain.core.OsCommand runOsCommand(String cmd, String dir,
							     org.luwrain.core.OsCommand.Output output , org.luwrain.core.OsCommand.Listener listener )
    {
	NullCheck.notEmpty(cmd, "cmd");
	NullCheck.notNull(dir, "dir");
	final LinkedList<String> arg = new LinkedList<String>();
	arg.add("/bin/bash");
	arg.add("-c");
	arg.add(cmd);
	return new OsCommand(output, listener, arg, dir);
    }

    @Override public FilesOperations getFilesOperations()
    {
	return filesOperations;
    }
}
