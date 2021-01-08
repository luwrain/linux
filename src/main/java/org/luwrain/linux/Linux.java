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

package org.luwrain.linux;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.base.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;


public final class Linux implements org.luwrain.base.OperatingSystem
{
    static public final String LOG_COMPONENT = "linux";
    static private final String LUWRAIN_LINUX_LIBRARY_NAME = "luwrainlinux";

    private InterfaceObj interfaceObj = new InterfaceObj(this);
    private PropertiesBase props = null;
    private org.luwrain.linux.wifi.Connections wifiConnections = null;
    private org.luwrain.linux.disks.Disk newlyAvailableDisk = null;

    @Override public InitResult init(PropertiesBase props)
    {
	NullCheck.notNull(props, "props");
	Extension.setLinux(this);
	this.props = props;
	this.wifiConnections = new org.luwrain.linux.wifi.Connections(props );
	return new InitResult();
    }

    Scripts getScripts()
    {
	return new Scripts(props);
    }

    @Override public OsInterface getInterface()
    {
	return interfaceObj;
    }

    public String getProperty(String propName)
    {
	NullCheck.notNull(propName, "propName");
	return "";
    }

    @Override public org.luwrain.base.Braille getBraille()
    {
	return new BrlApi();
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

    @Override public org.luwrain.core.OsCommand runOsCommand(String cmd, String dir,
							     org.luwrain.core.OsCommand.Output output , org.luwrain.core.OsCommand.Listener listener )
    {
	NullCheck.notEmpty(cmd, "cmd");
	NullCheck.notNull(dir, "dir");
	final List<String> arg = new LinkedList();
	arg.add("/bin/bash");
	arg.add("-c");
	arg.add(cmd);
	return new OsCommand(output, listener, arg, dir);
    }

    org.luwrain.linux.disks.Disk getNewlyAvailableDisk()
    {
	return this.newlyAvailableDisk;
    }

    public org.luwrain.linux.wifi.Connections getWifiConnections()
    {
	return wifiConnections;
    }

    void onUsbDiskAttached(Luwrain luwrain, String path)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(path, "path");
	this.newlyAvailableDisk = new org.luwrain.linux.disks.Disk(new File("/sys" + path));
	luwrain.message("Подключён новый съёмный диск", Luwrain.MessageType.ANNOUNCEMENT);//FIXME:
	luwrain.sendBroadcastEvent(new SystemEvent(SystemEvent.Type.BROADCAST, SystemEvent.Code.REFRESH, "", "disksvolumes:"));
    }

    void onCdromChanged(Luwrain luwrain, String path)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(path, "path");
	this.newlyAvailableDisk = new org.luwrain.linux.disks.Disk(new File("/sys" + path));
	luwrain.message("Обнаружен новый носитель в приводе компакт-дисков", Luwrain.MessageType.ANNOUNCEMENT);//FIXME:
    }

    PropertiesBase getProps()
    {
	return props;
    }
}
