/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.linux;

import java.io.File;

import org.luwrain.os.Location;

public class Linux implements org.luwrain.os.OperatingSystem
{
    private static final String LUWRAIN_LINUX_LIBRARY_NAME = "luwrainlinux";

    public String init()
    {
	System.loadLibrary(LUWRAIN_LINUX_LIBRARY_NAME);
	return null;
    }

    @Override public Location[] getImportantLocations()
    {
	return ImportantLocations.getImportantLocations();
    }

    @Override public File getRoot(File relativeTo)
    {
	return new File("/");
    }

    @Override public org.luwrain.hardware.Hardware getHardware()
    {
	return new Hardware();
    }
}
