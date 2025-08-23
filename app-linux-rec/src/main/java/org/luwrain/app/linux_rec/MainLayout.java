/*
   Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.linux_rec;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import org.apache.logging.log4j.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.linux.*;
import org.luwrain.controls.ListUtils.*;
import org.luwrain.app.base.*;
import org.luwrain.app.linux_rec.layouts.*;

import static org.luwrain.core.DefaultEventResponse.*;
import static org.luwrain.core.events.InputEvent.*;

final class MainLayout extends LayoutBase
{
    static private final Logger log = LogManager.getLogger();

final App app;
    final ListArea<Entry> entriesArea;
    final List<Entry> entries = new ArrayList<>();

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	final var s = app.getStrings();
	this.entriesArea = new ListArea<>(listParams( p -> {
		    p.model = new ListModel<>(entries);
		    p.appearance = new MainListAppearance();
		    p.name = s.entriesAreaName();
		}));
	setAreaLayout(entriesArea, actions(
					   action("rec", s.actionRec(), new InputEvent(Special.F5), this::onStartRecording)
));
    }

    boolean onStartRecording()
    {
	app.setAreaLayout(new RecordingProgressLayout(app, getReturnAction()));
	getLuwrain().announceActiveArea();
	return true;
    }


    final class MainListAppearance  extends DoubleLevelAppearance<Entry>
    {
	MainListAppearance()
	{
	    super(getControlContext());
	}
	
	@Override public boolean isSectionItem(Entry entry)
	{
	    return false;
	}
	
	@Override public void announceNonSection(Entry entry)
	{
	}

	@Override public String getNonSectionScreenAppearance(Entry entry)
	{
	    return "";
	}
    }
}
