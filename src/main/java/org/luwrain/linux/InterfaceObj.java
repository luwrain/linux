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

package org.luwrain.linux;

import java.util.function.*;

import org.luwrain.core.*;
import org.luwrain.script.*;

final class InterfaceObj extends EmptyHookObject implements Interface
{
    private final Linux linux;

    InterfaceObj(Linux linux)
    {
	NullCheck.notNull(linux, "linux");
	this.linux = linux;
    }

    @Override public boolean mount(MountParams params)
    {
	return false;
    }

        @Override public boolean suspend()
    {
	/*
	final PropertiesBase props = linux.getProps();
	if (props == null)
	    return false;
	new Scripts(props).runSync(Scripts.ID.SUSPEND, true);
	*/
	return true;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	switch(name)
	{
	case "suspend":
	    return (BooleanSupplier)this::suspend;
	default:
	    return super.getMember(name);
	}
    }

}
