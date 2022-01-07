/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

import java.util.*;
import java.util.regex.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.linux.*;
import org.luwrain.controls.ListUtils.*;
import org.luwrain.app.base.*;

import static org.luwrain.core.DefaultEventResponse.*;

final class MainLayout extends LayoutBase implements ListArea.ClickHandler<WifiNetwork>
{
    private final App app;
    final ListArea<WifiNetwork> networksArea;
    final SimpleArea statusArea;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	this.networksArea = new ListArea<>(listParams((params)->{
		    params.model = new ListModel(app.networks);
		    params.name = app.getStrings().networksAreaName();
		    params.clickHandler = this;
		}));
	this.statusArea = new SimpleArea(getControlContext(), app.getStrings().statusAreaName());
	setAreaLayout(AreaLayout.LEFT_RIGHT, networksArea, null, statusArea, null);
    }

    @Override public boolean onListClick(ListArea area, int index, WifiNetwork network)
    {
	NullCheck.notNull(network, "network");
	return false;
    }

}
