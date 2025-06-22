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

package org.luwrain.app.parted;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.controls.ListUtils.*;
import org.luwrain.app.base.*;
import org.luwrain.linux.*;

import static org.luwrain.core.DefaultEventResponse.*;

final class MainLayout extends LayoutBase
{
    private final App app;
    final ListArea<Parted> disksArea;
    final ListArea<String> partArea;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	this.disksArea = new ListArea<>(listParams((params)->{
		    params.model = new ListModel(app.disks);
		    params.name = app.getStrings().disksAreaName();
		}));
	this.partArea = new ListArea<>(listParams((params)->{
		    params.name = app.getStrings().partAreaName();
		    params.model = new ListModel(app.parts);
		}));
	setAreaLayout(AreaLayout.TOP_BOTTOM, disksArea, null, partArea, null);
    }
}
