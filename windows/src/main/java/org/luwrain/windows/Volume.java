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

import java.io.*;

import org.luwrain.core.*;

public final class Volume
{
    public enum Type {
	REGULAR,
	REMOVABLE,
	REMOTE,
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
}
