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

import java.io.File;

import org.luwrain.core.*;
import org.luwrain.script.*;

public final class Volume extends EmptyHookObject
{
    public enum Type {
	REGULAR,
	REMOVABLE,
	REMOTE,
	USER_HOME,
	ROOT,
    };

    public final Type type;
    public final File file;
    public final String name;

    Volume(Type type, File file, String name)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNull(file, "file");
	NullCheck.notNull(name, "name");
	this.type = type;
	this.file = file;
	this.name = name;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	switch(name)
	{
	case "type":
	    return "volume_" + type.toString().toLowerCase();
	case "file":
	    return file.getAbsolutePath();
	case "name":
	    return this.name;
	default:
	    return super.getMember(name);
	}
    }
}
