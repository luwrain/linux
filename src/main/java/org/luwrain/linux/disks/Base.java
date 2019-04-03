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

package org.luwrain.linux.disks;

import java.io.*;

import org.luwrain.core.*;
import org.luwrain.script.*;

public class Base extends EmptyHookObject
{
    protected final File path;

    Base(File path)
    {
	NullCheck.notNull(path, "path");
	this.path = path;
    }

    public File getDevFile()
    {
	return new File("/dev/" + getDevName());
    }

    public String getDevName()
    {
	return path.getName();
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	switch(name)
	{
	case "device":
	    return getDevName();
	default:
	    return super.getMember(name);
	}
    }

    @Override public String toString()
    {
	return path.getAbsolutePath();
    }
}
