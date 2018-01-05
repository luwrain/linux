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

package org.luwrain.app.wifi;

import org.luwrain.core.NullCheck;

class WifiScanResult
{
    enum Type {SUCCESS, FAILED};

    final Type type;
    final WifiNetwork[] networks;

    WifiScanResult()
    {
	this.type = Type.FAILED;
	this.networks = new WifiNetwork[0];
    }

    WifiScanResult(WifiNetwork[] networks)
    {
	NullCheck.notNullItems(networks, "networks");
	this.type = Type.SUCCESS;
	this.networks = networks;
    }
}
