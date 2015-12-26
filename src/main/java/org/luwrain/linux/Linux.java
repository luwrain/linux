/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.io.File;

import org.luwrain.core.*;
import org.luwrain.os.*;
import org.luwrain.speech.*;
import org.luwrain.linux.speech.*;

public class Linux implements org.luwrain.os.OperatingSystem
{
    private static final String LUWRAIN_LINUX_LIBRARY_NAME = "luwrainlinux";

    interface ChannelBasicData
    {
	String getType();
    };


    public String init()
    {
	System.loadLibrary(LUWRAIN_LINUX_LIBRARY_NAME);
	return null;
    }

    @Override public org.luwrain.hardware.Hardware getHardware()
    {
	return new Hardware();
    }

    @Override public void openFileInDesktop(File file)
    {
	throw new UnsupportedOperationException("This OS does not support for Desktop action OPEN");
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

    @Override public Channel loadSpeechChannel(String[] cmdLine, Registry registry, String regPath)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notNull(regPath, "regPath");
	try {
	    final ChannelBasicData data = RegistryProxy.create(registry, regPath, ChannelBasicData.class);
	    switch(data.getType())
	    {
	    case "command":
		return new Command2();

	    case "voiceman":
		return new VoiceMan();


	    default:
		return null;
	    }
	}
	catch (Exception e)
	{
	    Log.error("linux", "unexpected exception while loading speech channel from " + regPath);
	    e.printStackTrace();
	    return null;
	}
    }
}
