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

import java.awt.Desktop;
import java.io.File;

import org.luwrain.core.Log;

public class Linux implements org.luwrain.os.OperatingSystem
{
    private static final String LUWRAIN_LINUX_LIBRARY_NAME = "luwrainlinux";

    public String init()
    {
	System.loadLibrary(LUWRAIN_LINUX_LIBRARY_NAME);
	return null;
    }

    @Override public org.luwrain.hardware.Hardware getHardware()
    {
	return new Hardware();
    }
	
    @Override public void fileOpendDesktopDefault(File file)
	{
    	// FIXME: this code identical to Windows implementation, move code to other place
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
			desktop.open(file);
		}
		catch(Exception e)
		{
			// FEXME: make better error handling
			Log.debug("windows",e.getMessage());
		}
	}
}
