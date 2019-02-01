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

import java.io.*;
import java.util.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

final class SystemProperties
{
    static final class Battery implements PropertiesProvider
    {
	private final Scripts scripts;
	private PropertiesProvider.Listener listener = null;
	Battery(PropertiesBase props)
	{
	    NullCheck.notNull(props, "props");
	    this.scripts = new Scripts(props);
	}
	@Override public String getExtObjName()
	{
	    return this.getClass().getName();
	}
	@Override public String[] getPropertiesRegex()
	{
	    return new String[]{"^battery\\."};
	}
	@Override public Set<org.luwrain.base.PropertiesProvider.Flags> getPropertyFlags(String propName)
	{
	    NullCheck.notEmpty(propName, "propName");
	    return EnumSet.of(PropertiesProvider.Flags.PUBLIC);
	}
	@Override public File getFileProperty(String propName)
	{
	    NullCheck.notNull(propName, "propName");
	    return null;
	}
	@Override public boolean setFileProperty(String propName, File value)
	{
	    NullCheck.notEmpty(propName, "propName");
	    NullCheck.notNull(value, "value");
	    return false;
	}
	@Override public String getProperty(String propName)
	{
	    NullCheck.notEmpty(propName, "propName");
	    switch(propName)
	    {
	    case "battery.percent":
		{
		    final String res = scripts.runSingleLineOutput(Scripts.ID.BATTERY_PERCENT, new String[0], false);
		    return res != null?res:"";
		}
	    default:
		return null;
	    }
	}
	@Override public boolean setProperty(String propName, String value)
	{
	    NullCheck.notEmpty(propName, "propName");
	    NullCheck.notNull(value, "value");
	    return false;
	}
	@Override public void setListener(org.luwrain.base.PropertiesProvider.Listener listener)
	{
	    this.listener = listener;
	}
    }
}
