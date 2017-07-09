/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.io.File;

import org.luwrain.base.*;
import org.luwrain.core.*;

class PartitionImpl implements Partition
{
    private final Type type;
    private final File file;
    private final String name;
    private final boolean mounted;

    PartitionImpl(Type type, File file, String name, boolean mounted)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNull(file, "file");
	NullCheck.notNull(name, "name");
	this.type = type;
	this.file = file;
	this.name = name;
	this.mounted = mounted;
    }

    @Override public Type getPartType()
    {
	return type;
    }

    @Override public File getPartFile()
    {
	return file;
    }

    @Override public String getPartName()
    {
	return name;
    }

    @Override public boolean isMounted()
    {
	return mounted;
    }
}
