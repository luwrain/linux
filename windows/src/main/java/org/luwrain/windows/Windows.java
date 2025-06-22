/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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

package org.luwrain.windows;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.luwrain.core.*;
import org.luwrain.interaction.KeyboardHandler;

public final class Windows implements OperatingSystem
{
    interface ChannelBasicData
    {
	String getType();
    };

    @Override public String escapeString(String style, String str)
    {
	return str;
    }

    @Override public InitResult init(PropertiesBase props)
    {
	NullCheck.notNull(props, "props");
    	return new InitResult();
    }

    @Override public Braille getBraille()
    {
	return null;
    }

	@Override public void openFileInDesktop(Path path)
	{
		if(!Desktop.isDesktopSupported())
		{
			throw new UnsupportedOperationException("This OS does not support for Desktop");
		}
		Desktop desktop = Desktop.getDesktop();
		if(!desktop.isSupported(Desktop.Action.OPEN))
		{
			throw new UnsupportedOperationException("This OS does not support for Desktop action OPEN");
		}
		try
		{
			desktop.open(path.toFile());
		}
		catch(Exception e)
		{
			// FEXME: make better error handling
			Log.debug("windows",e.getMessage());
		}
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

}
