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

package org.luwrain.linux.wifi;

import org.luwrain.core.NullCheck;

public final class ScanResult
{
    public enum Type {SUCCESS, FAILED};

    public final Type type;
    public final Network[] networks;

    ScanResult()
    {
	this.type = Type.FAILED;
	this.networks = new Network[0];
    }

    ScanResult(Network[] networks)
    {
	NullCheck.notNullItems(networks, "networks");
	this.type = Type.SUCCESS;
	this.networks = networks;
    }
}
