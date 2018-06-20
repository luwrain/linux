/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.linux.wifi;

import org.luwrain.core.*;

public final class Network
{
    public final String name;
    public final boolean hasPassword;
private String password = "";

    Network(String name, boolean hasPassword)
    {
	NullCheck.notNull(name, "name");
	this.name = name;
	this.hasPassword = hasPassword;
    }

    public void setPassword(String password)
    {
	NullCheck.notNull(password, "password");
	this.password = password;
    }

    public String getPassword()
    {
	return password;
    }

    @Override public String toString()
    {
	return name;
    }
}
