/*
   Copyright 2012-2025 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import com.google.auto.service.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

@AutoService(org.luwrain.core.OperatingSystem.class)
public final class Linux implements OperatingSystem
{
    static public final String
	LOG_COMPONENT = "linux";

    static public final Syscalls syscalls = new Syscalls();
    private PropertiesBase props = null;

    @Override public InitResult init(PropertiesBase props)
    {
	Extension.setLinux(this);
	this.props = props;
	return new InitResult();
    }

    @Override public String escapeString(String style, String value)
    {
	switch(style.trim().toUpperCase())
	{
	case "CMD":
	case "SHELL":
	case "BASH":
	    return BashProcess.escape(value);
	default:
	    Log.warning(LOG_COMPONENT, "unknown escaping style: " + style);
	    return value;
	}
    }

    public String getProperty(String propName)
    {
	NullCheck.notNull(propName, "propName");
	return "";
    }

    @Override public Braille getBraille()
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

    PropertiesBase getProps()
    {
	return props;
    }
}
