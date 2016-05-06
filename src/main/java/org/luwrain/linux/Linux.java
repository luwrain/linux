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

import java.io.File;
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

    @Override public boolean init(String dataDir)
    {
	NullCheck.notNull(dataDir, "dataDir");
	System.loadLibrary(LUWRAIN_LINUX_LIBRARY_NAME);
	scriptsDir = Paths.get(dataDir).resolve("scripts");
	scripts = new Scripts(scriptsDir);
	return true;
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
}

